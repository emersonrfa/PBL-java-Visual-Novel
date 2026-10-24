package Controller;

import Exception.GamePersistenceException;
import Model.*;
import Repository.StateRepository;
import Service.AchievementService;
import Service.AffinityTracker;
import Service.PlayerService;
import Service.StoryService;
import View.AchievementView;
import View.DialogueView;
import View.MenuView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DialogueController {
    private InputManager inputManager;
    private Player player;
    private AffinityTracker affinityTracker;
    private DialogueView view;
    private MenuView menuView;
    private AchievementView achievementView;
    private PlayerService playerService;
    private AchievementService achievementService;
    private GameConfig config;
    private StateRepository stateRepository;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    public DialogueController(Player player, InputManager inputManager,
                              AffinityTracker affinityTracker, GameConfig config,
                              StateRepository stateRepository) {
        this.player = player;
        this.inputManager = inputManager;
        this.affinityTracker = affinityTracker;
        this.view = new DialogueView();
        this.menuView = new MenuView();
        this.achievementView = new AchievementView();
        this.playerService = new PlayerService();
        this.achievementService = new AchievementService();
        this.config = config;
        this.stateRepository = stateRepository;
    }

    public void advanceDialogue(Dialogue currentDialogue, Scene currentScene, int chapterNumber) {
        boolean waitingAdvance = true;

        while (waitingAdvance) {
            String command = inputManager.waitEnterOrCommand();

            if (command != null && command.equalsIgnoreCase("S")) {
                String sceneTitle = (currentScene != null) ? currentScene.getTitle() : "Cena";
                handleManualSave(currentDialogue.getId(), sceneTitle, chapterNumber);
                view.printDialogue(currentDialogue, 0);
            } else {
                waitingAdvance = false;
            }
        }
    }

    public void playChapter(Chapter chapter) {
        playChapter(chapter, "d1.1");
    }

    public void playChapter(Chapter chapter, String startingDialogueId) {
        String currentId = (startingDialogueId != null && !startingDialogueId.isEmpty())
                ? startingDialogueId
                : "d1.1";

        Scene currentScene = null;
        EndingManager endingManager = new EndingManager();

        while (currentId != null && !currentId.isEmpty()) {
            if (endingManager.isEndingCode(currentId)) {
                endingManager.triggerEnding(currentId);
                break;
            }

            Scene nextScene = StoryService.findSceneByDialogueId(chapter, currentId);

            if (nextScene == null) {
                view.printDebugWarning("JOGO ENCERRADO POR NÃO TER ACHADO ID DE PRÓXIMA CENA (" + currentId + ")");
                break;
            }

            if (currentScene != nextScene) {
                currentScene = nextScene;
                view.printSceneHeader(currentScene);
            }

            Dialogue current = currentScene.getDialogueById(currentId);

            triggerAutoSave(chapter.getChapterNumber(), currentScene.getTitle(), current.getId());

            view.printDialogue(current, config.getTextSpeedMs());

            Achievement unlockedByDialogue = achievementService.evaluateDialogueTrigger(current.getId());
            if (unlockedByDialogue != null) {
                achievementView.printAchievementBanner(unlockedByDialogue);
            }

            if (current.getOptions().isEmpty()) {
                view.printDebugWarning("JOGO ENCERRADO POR NÃO TER ACHADO NENHUMA OPÇÃO DE DIÁLOGO (" + currentId + ")");
                break;
            }

            if (current.getOptions().size() == 1 && current.getOptions().get(0).getText().equals("...")) {
                advanceDialogue(current, currentScene, chapter.getChapterNumber());
                currentId = current.getOptions().get(0).getNextDialogueId();
            } else {
                int choiceIndex = processinput(current);
                currentId = current.getOptions().get(choiceIndex).getNextDialogueId();
            }
        }
    }

    private boolean isAffinityValid(Options opt) {
        if (opt.getTargetNpc() != null) {
            if (affinityTracker.getAffinity(opt.getTargetNpc()) < opt.getRequiredAffinity()) {
                return false;
            }
            if (affinityTracker.getSuspicion(opt.getTargetNpc()) > opt.getMaxSuspicionAllowed()) {
                return false;
            }
        }
        return true;
    }

    public int processinput(Dialogue currentDialogue) {
        view.printOptionsHeader();
        var options = currentDialogue.getOptions();

        for (int i = 0; i < options.size(); i++) {
            Options opt = options.get(i);

            boolean validPlayer = playerService.validatePlayerRequirements(player, opt);
            boolean validAffinity = isAffinityValid(opt);

            if (validPlayer && validAffinity) {
                view.printOption(i, opt.getText(), null);
            } else {
                String motivo = "";

                if (opt.getMoneyChange() < 0 && player.getMoney() < Math.abs(opt.getMoneyChange())) {
                    motivo = "[Trocos insuficientes]";
                } else if (opt.getRequiredFlag() != null && !player.getFlag(opt.getRequiredFlag())) {
                    motivo = "[???]";
                } else if (opt.getEnergyChange() < 0 && player.getEnergy() < Math.abs(opt.getEnergyChange())) {
                    motivo = "[Energia Insuficiente]";
                } else if (opt.getRequiredItemName() != null && !player.getInventory().hasItem(opt.getRequiredItemName())) {
                    motivo = "[Requer: " + opt.getRequiredItemName() + "]";
                } else if (opt.getTargetNpc() != null) {
                    if (affinityTracker.getAffinity(opt.getTargetNpc()) < opt.getRequiredAffinity())
                        motivo = "[Afinidade Insuficiente com " + opt.getTargetNpc() + "]";
                    else if (affinityTracker.getSuspicion(opt.getTargetNpc()) > opt.getMaxSuspicionAllowed())
                        motivo = "[Você é suspeita demais para esta opção]";
                }

                view.printOption(i, opt.getText(), motivo);
            }
        }

        int choice = -1;
        boolean validChoice = false;

        while (!validChoice) {
            choice = inputManager.readValidChoice(1, options.size());
            Options chosenOption = options.get(choice - 1);

            if (!playerService.validatePlayerRequirements(player, chosenOption) || !isAffinityValid(chosenOption)) {
                view.printRequirementWarning();
            } else {
                if (config != null && config.isConfirmChoices()) {
                    menuView.printMessage("Confirmar escolha \"" + chosenOption.getText() + "\"? (Digite 'S' para confirmar ou outra tecla para cancelar):");
                    String confirm = inputManager.readString().trim();
                    if (!confirm.equalsIgnoreCase("S")) {
                        menuView.printMessage("Escolha cancelada. Selecione novamente.");
                        continue;
                    }
                }
                validChoice = true;
            }
        }

        int chosenIndex = choice - 1;
        Options chosenOption = options.get(chosenIndex);

        List<String> messages = playerService.applyOptionConsequences(player, chosenOption);
        for (String msg : messages) {
            view.printConsequence(msg);
        }

        if (chosenOption.getTargetNpc() != null) {
            affinityTracker.modifyAffinity(chosenOption.getTargetNpc(), chosenOption.getAffinityChange());
            affinityTracker.modifySuspicion(chosenOption.getTargetNpc(), chosenOption.getSuspicionChange());

            int currentAff = affinityTracker.getAffinity(chosenOption.getTargetNpc());
            Achievement unlockedAff = achievementService.evaluateAffinityTrigger(chosenOption.getTargetNpc(), currentAff);
            if (unlockedAff != null) {
                achievementView.printAchievementBanner(unlockedAff);
            }
        }

        if (chosenOption.getFlagToSet() != null && !chosenOption.getFlagToSet().trim().isEmpty()) {
            Achievement unlockedFlag = achievementService.evaluateFlagTrigger(chosenOption.getFlagToSet());
            if (unlockedFlag != null) {
                achievementView.printAchievementBanner(unlockedFlag);
            }
        }

        return chosenIndex;
    }

    private void triggerAutoSave(int chapterId, String sceneTitle, String dialogId) {
        if (stateRepository != null) {
            GameStates state = stateRepository.getCurrentStates();

            state.setCurrentChapterId(chapterId);
            state.setCurrentSceneId(sceneTitle);
            state.setCurrentDialogId(dialogId);
            state.setSaveTimestamp(LocalDateTime.now().format(DATE_FORMATTER));

            playerService.populateStateFromPlayer(state, this.player, this.affinityTracker);

            if (config != null && config.isTurnAutoSave()) {
                stateRepository.saveState(state);
            }
        }
    }

    public void handleManualSave(String currentDialogId, String currentSceneId, int currentChapterId) {
        Map<Integer, GameStates> slotsMap = new HashMap<>();
        for (int i = 1; i <= 4; i++) {
            slotsMap.put(i, this.stateRepository.loadStateFromSlot(i));
        }

        menuView.displayManualSaveMenu(slotsMap);
        int choice = this.inputManager.readValidChoice(0, 4);

        if (choice == 0) {
            menuView.printMessage("\nSalvamento cancelado.");
            return;
        }

        if (stateRepository.slotExists(choice)) {
            menuView.printOverwriteConfirmation(choice);
            String confirmation = this.inputManager.readString().trim();

            if (!confirmation.equalsIgnoreCase("S")) {
                menuView.printMessage("\n[Cancelado] O save do Slot " + choice + " não foi modificado.");
                menuView.printMessage("[Pressione ENTER para continuar]");
                this.inputManager.waitEnter();
                return;
            }
        }

        GameStates manualState = new GameStates();
        manualState.setCurrentChapterId(currentChapterId);
        manualState.setCurrentSceneId(currentSceneId);
        manualState.setCurrentDialogId(currentDialogId);
        manualState.setSaveTimestamp(LocalDateTime.now().format(DATE_FORMATTER));

        playerService.populateStateFromPlayer(manualState, this.player, this.affinityTracker);

        try {
            stateRepository.saveStateToSlotChecked(manualState, choice);
            menuView.printMessage("\n[Sucesso] Jogo salvo no Slot " + choice + "!");
        } catch (GamePersistenceException e) {
            menuView.printMessage("\n[Erro de Gravação] " + e.getMessage());
        }

        menuView.printMessage("[Pressione ENTER para continuar]");
        this.inputManager.waitEnter();
    }

    public InputManager getInputManager() {
        return inputManager;
    }
}