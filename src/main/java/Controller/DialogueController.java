package Controller;

import Model.*;
import Repository.StateRepository; // <- Adicionado
import Service.PlayerService;
import Service.StoryService;
import Service.AffinityTracker;
import View.DialogueView;

public class DialogueController {
    private InputManager inputManager;
    private Player player;
    private AffinityTracker affinityTracker;
    private DialogueView view;
    private PlayerService playerService;
    private GameConfig config;
    private StateRepository stateRepository; // <- Adicionado

    public DialogueController(Player player, InputManager inputManager,
                              AffinityTracker affinityTracker, GameConfig config,
                              StateRepository stateRepository) {
        this.player = player;
        this.inputManager = inputManager;
        this.affinityTracker = affinityTracker;
        this.view = new DialogueView();
        this.playerService = new PlayerService();
        this.config = config;
        this.stateRepository = stateRepository; // <- Inicializado
    }

    public void advanceDialogue() {
        view.printAdvancePrompt();
        inputManager.waitEnter();
    }

    public void playChapter(Chapter chapter) {
        String currentId = "d1.1"; // primeiro dialogo de todos
        Scene currentScene = null;
        EndingManager endingManager = new EndingManager();

        while (currentId != null && !currentId.isEmpty()) {
            // localiza a cena que tem o dialogo atual
            Scene nextScene = StoryService.findSceneByDialogueId(chapter, currentId);

            if (nextScene == null) {
                view.printDebugWarning("JOGO ENCERRADO POR NÃO TER ACHADO ID DE PRÓXIMA CENA (" + currentId + ")");
                break;
            }

            if (endingManager.isEndingCode(currentId)) {
                endingManager.triggerEnding(currentId);
                break; // Encerra o laço e o jogo
            }

            if (currentScene != nextScene) {
                currentScene = nextScene;
                view.printSceneHeader(currentScene); // Delega para a View
            }

            Dialogue current = currentScene.getDialogueById(currentId);

            // AUTO-SAVE: Registra a posição atual do diálogo antes de exibir
            triggerAutoSave(chapter.getChapterNumber(), currentScene.getTitle(), current.getId());

            view.printDialogue(current, config.getTextSpeedMs());

            if (current.getOptions().isEmpty()) {
                view.printDebugWarning("JOGO ENCERRADO POR NÃO TER ACHADO NENHUMA OPÇÃO DE DIÁLOGO (" + currentId + ")");
                break;
            }

            if (current.getOptions().size() == 1 && current.getOptions().get(0).getText().equals("...")) {
                advanceDialogue();
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

    // Método centralizado para gerenciar o auto-save
    private void triggerAutoSave(int chapterId, String sceneTitle, String dialogId) {
        if (stateRepository != null) {
            GameStates state = stateRepository.getCurrentStates();

            // 1. Dados de progresso da história
            state.setCurrentChapterId(chapterId);
            state.setCurrentSceneId(sceneTitle);
            state.setCurrentDialogId(dialogId);

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

    public InputManager getInputManager() {
        return inputManager;
    }
}