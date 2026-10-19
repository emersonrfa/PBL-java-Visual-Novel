package Controller;

import Model.*;
import Repository.StateRepository;
import Service.PlayerService;
import Service.StoryService;
import Service.AffinityTracker;
import View.DialogueView;
import View.MenuView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DialogueController {
    private InputManager inputManager;
    private Player player;
    private AffinityTracker affinityTracker;
    private DialogueView view;
    private PlayerService playerService;
    private GameConfig config;
    private StateRepository stateRepository;
    //esse metodo e final para garatir que o valor nn vai ser sobrescrito e static para economizar memoria
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public DialogueController(Player player, InputManager inputManager,
                              AffinityTracker affinityTracker, GameConfig config,
                              StateRepository stateRepository) {
        this.player = player;
        this.inputManager = inputManager;
        this.affinityTracker = affinityTracker;
        this.view = new DialogueView();
        this.playerService = new PlayerService();
        this.config = config;
        this.stateRepository = stateRepository;
    }

    /**
     * Aguarda o avanço do diálogo ou a ativação do menu de gravação manual ao digitar 'S'.
     * Permanece no loop caso o jogador aceda ao menu de gravação e depois queira prosseguir.
     */
    public void advanceDialogue(Dialogue currentDialogue, Scene currentScene, int chapterNumber) {
        boolean waitingAdvance = true;

        while (waitingAdvance) {
            String command = inputManager.waitEnterOrCommand();

            if (command != null && command.equalsIgnoreCase("S")) {
                String sceneTitle = (currentScene != null) ? currentScene.getTitle() : "Cena";
                handleManualSave(currentDialogue.getId(), sceneTitle, chapterNumber);
                // Após o save, exibe a fala resumida para manter o contexto visual
                view.printDialogue(currentDialogue, 0);;
            } else {
                waitingAdvance = false;
            }
        }
    }

    // Método padrão (início do capítulo a partir do início)
    public void playChapter(Chapter chapter) {
        playChapter(chapter, "d1.1");
    }

    // Sobrecarga: permite iniciar a partir de qualquer diálogo guardado
    public void playChapter(Chapter chapter, String startingDialogueId) {
        String currentId = (startingDialogueId != null && !startingDialogueId.isEmpty())
                ? startingDialogueId
                : "d1.1";

        Scene currentScene = null;
        EndingManager endingManager = new EndingManager();

        while (currentId != null && !currentId.isEmpty()) {
            Scene nextScene = StoryService.findSceneByDialogueId(chapter, currentId);

            if (nextScene == null) {
                view.printDebugWarning("JOGO ENCERRADO POR NÃO TER ACHADO ID DE PRÓXIMA CENA (" + currentId + ")");
                break;
            }

            if (endingManager.isEndingCode(currentId)) {
                endingManager.triggerEnding(currentId);
                break;
            }

            if (currentScene != nextScene) {
                currentScene = nextScene;
                view.printSceneHeader(currentScene);
            }

            Dialogue current = currentScene.getDialogueById(currentId);

            // Auto-save a cada avanço
            triggerAutoSave(chapter.getChapterNumber(), currentScene.getTitle(), current.getId());

            view.printDialogue(current, config.getTextSpeedMs());

            if (current.getOptions().isEmpty()) {
                view.printDebugWarning("JOGO ENCERRADO POR NÃO TER ACHADO NENHUMA OPÇÃO DE DIÁLOGO (" + currentId + ")");
                break;
            }

            if (current.getOptions().size() == 1 && current.getOptions().get(0).getText().equals("...")) {
                // Passa o contexto atual para permitir salvar ao premir 'S'
                advanceDialogue(current, currentScene, chapter.getChapterNumber());
                currentId = current.getOptions().get(0).getNextDialogueId();
            } else {
                int choiceIndex = processinput(current);
                currentId = current.getOptions().get(choiceIndex).getNextDialogueId();
            }
        }
    }

    public int processinput(Dialogue currentDialogue) {
        view.printOptionsHeader();
        var options = currentDialogue.getOptions();

        // EXIBIÇÃO DAS OPÇÕES
        for (int i = 0; i < options.size(); i++) {
            Options opt = options.get(i);

            boolean validPlayer = playerService.validatePlayerRequirements(player, opt);
            boolean validAffinity = true;

            if (opt.getTargetNpc() != null) {
                if (affinityTracker.getAffinity(opt.getTargetNpc()) < opt.getRequiredAffinity())
                    validAffinity = false;
                if (affinityTracker.getSuspicion(opt.getTargetNpc()) > opt.getMaxSuspicionAllowed())
                    validAffinity = false;
            }

            if (validPlayer && validAffinity) {
                System.out.println((i + 1) + " - " + opt.getText());
            }
            else {
                String motivo = "";

                if (opt.getMoneyChange() < 0 && player.getMoney() < Math.abs(opt.getMoneyChange())) {
                    motivo = "[Trocos insuficientes]";
                }
                else if (opt.getRequiredFlag() != null && !player.getFlag(opt.getRequiredFlag())) {
                    motivo = "[???]";
                }
                else if (opt.getEnergyChange() < 0 && player.getEnergy() < Math.abs(opt.getEnergyChange())) {
                    motivo = "[Energia Insuficiente]";
                }
                else if (opt.getRequiredItemName() != null && !player.getInventory().hasItem(opt.getRequiredItemName())) {
                    motivo = "[Requer: " + opt.getRequiredItemName() + "]";
                }
                else if (opt.getTargetNpc() != null) {
                    if (affinityTracker.getAffinity(opt.getTargetNpc()) < opt.getRequiredAffinity())
                        motivo = "[Afinidade Insuficiente com " + opt.getTargetNpc() + "]";
                    else if (affinityTracker.getSuspicion(opt.getTargetNpc()) > opt.getMaxSuspicionAllowed())
                        motivo = "[Você é suspeita demais para esta opção]";
                }

                view.printOption(i, opt.getText(), motivo);
            }
        }

        // VALIDAÇÃO DA ESCOLHA DA OPÇÃO
        int choice = -1;
        boolean validChoice = false;

        while (!validChoice) {
            choice = inputManager.readValidChoice(1, options.size());
            Options chosenOption = options.get(choice - 1);

            if (!playerService.validatePlayerRequirements(player, chosenOption)) {
                view.printRequirementWarning();
            } else {
                validChoice = true;
            }
        }

        int chosenIndex = choice - 1;
        Options chosenOption = options.get(chosenIndex);

        // CONSEQUÊNCIAS DE ESCOLHA LÓGICA:
        playerService.applyOptionConsequences(player, chosenOption);

        if (chosenOption.getTargetNpc() != null) {
            affinityTracker.modifyAffinity(chosenOption.getTargetNpc(), chosenOption.getAffinityChange());
            affinityTracker.modifySuspicion(chosenOption.getTargetNpc(), chosenOption.getSuspicionChange());
        }

        // ATIVAÇÃO E PERSISTÊNCIA DA FLAG DE ESCOLHA
        if (chosenOption.getFlagToSet() != null && !chosenOption.getFlagToSet().trim().isEmpty()) {
            if (stateRepository != null) {
                GameStates state = stateRepository.getCurrentStates();
                state.setFlag(chosenOption.getFlagToSet(), true);

                if (config != null && config.isTurnAutoSave()) {
                    stateRepository.saveState(state);
                }
            }
        }

        return chosenIndex;
    }

    // Método centralizado para gerir o auto-save
    private void triggerAutoSave(int chapterId, String sceneTitle, String dialogId) {
        if (stateRepository != null) {
            GameStates state = stateRepository.getCurrentStates();

            // 1. Dados de progresso da história
            state.setCurrentChapterId(chapterId);
            state.setCurrentSceneId(sceneTitle);
            state.setCurrentDialogId(dialogId);
            state.setSaveTimestamp(LocalDateTime.now().format(DATE_FORMATTER));

            // 2. Dados do Player e inventário
            if (this.player != null) {
                state.setPlayerBloodType(this.player.getBloodType());
                state.setPlayerMoney(this.player.getMoney());
                state.setPlayerEnergy(this.player.getEnergy());

                if (this.player.getInventory() != null) {
                    state.setPlayerInventoryItems(this.player.getInventory().getItemNames());
                }
            }

            // 3. Persistência em disco conforme configuração
            if (config != null && config.isTurnAutoSave()) {
                stateRepository.saveState(state);
            }
        }
    }

    /**
     * Abre a interface para o jogador salvar o estado atual num slot manual (1 a 4).
     */
    public void handleManualSave(String currentDialogId, String currentSceneId, int currentChapterId) {
        MenuView menuView = new MenuView();

        // 1. Carrega os 4 slots manuais para mostrar o estado atual na consola
        Map<Integer, GameStates> slotsMap = new HashMap<>();
        for (int i = 1; i <= 4; i++) {
            slotsMap.put(i, this.stateRepository.loadStateFromSlot(i));
        }

        menuView.displayManualSaveMenu(slotsMap);
        int choice = this.inputManager.readValidChoice(0, 4);

        if (choice == 0) {
            System.out.println("\nSalvamento cancelado. Retomando o diálogo...");
            return;
        }

        // 2. Monta o snapshot atual do jogo
        GameStates manualState = new GameStates();
        manualState.setCurrentChapterId(currentChapterId);
        manualState.setCurrentSceneId(currentSceneId);
        manualState.setCurrentDialogId(currentDialogId);
        manualState.setPlayerBloodType(this.player.getBloodType());
        manualState.setPlayerEnergy(this.player.getEnergy());
        manualState.setPlayerMoney(this.player.getMoney());
        manualState.setSaveTimestamp(LocalDateTime.now().format(DATE_FORMATTER));

        // Clona as flags ativas do autosave corrente para manter coerência da história
        GameStates currentState = this.stateRepository.getCurrentStates();
        if (currentState != null && currentState.getStoryFlags() != null) {
            manualState.setStoryFlags(new HashMap<>(currentState.getStoryFlags()));
        }

        // Coleta itens do inventário
        List<String> items = new ArrayList<>();
        if (this.player.getInventory() != null && this.player.getInventory().getItems() != null) {
            for (Item item : this.player.getInventory().getItems()) {
                items.add(item.getName());
            }
        }
        manualState.setPlayerInventoryItems(items);

        // 3. Grava no slot escolhido (1 a 4)
        boolean success = this.stateRepository.saveStateToSlot(manualState, choice);
        if (success) {
            System.out.println("\n[Sucesso] Jogo salvo no Slot " + choice + "!");
        } else {
            System.out.println("\n[Erro] Não foi possível salvar no Slot " + choice + ".");
        }

        System.out.println("\n[Pressione ENTER para continuar a história]");
        this.inputManager.waitEnter();
    }

    public InputManager getInputManager() {
        return inputManager;
    }

    public void handleDeleteSave() {
        MenuView menuView = new MenuView();

        // 1. Carrega os estados de todos os slots para mostrar na tela
        Map<Integer, GameStates> slotsMap = new HashMap<>();
        for (int i = 0; i <= 4; i++) {
            slotsMap.put(i, this.stateRepository.loadStateFromSlot(i));
        }

        menuView.displayDeleteMenu(slotsMap);
        int choice = this.inputManager.readValidChoice(0, 9);

        if (choice == 9) {
            System.out.println("\nOperação de exclusão cancelada.");
            return;
        }

        // 2. Valida se o slot escolhido realmente tem um arquivo existente
        if (!stateRepository.slotExists(choice)) {
            System.out.println("\n[Aviso] O Slot " + choice + " já está vazio. Nada para deletar.");
            return;
        }

        // 3. Pede a confirmação do jogador usando a instância menuView
        menuView.printDeleteConfirmation(choice);
        String confirmation = this.inputManager.waitEnterOrCommand();

        if (confirmation != null && confirmation.equalsIgnoreCase("S")) {
            boolean deleted = stateRepository.deleteSlot(choice);
            if (deleted) {
                System.out.println("\n[Sucesso] O save do Slot " + choice + " foi deletado com sucesso!");
            } else {
                System.out.println("\n[Erro] Falha ao tentar deletar o arquivo do Slot " + choice + ".");
            }
        } else {
            System.out.println("\n[Cancelado] A exclusão do Slot " + choice + " foi cancelada.");
        }

        System.out.println("\n[Pressione ENTER para continuar]");
        this.inputManager.waitEnter();
    }
}