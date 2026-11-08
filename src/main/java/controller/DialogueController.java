package controller;

import exception.GamePersistenceException;
import model.*;
import repository.ChapterCatalog;
import repository.StateRepository;
import service.AchievementService;
import service.AffinityTracker;
import service.AssetResolver;
import service.DialogueSnapshot;
import service.GameSession;
import service.StepResult;
import service.ConsequenceReport;
import service.PlayerService;
import view.AchievementView;
import view.DialogueView;
import view.MenuView;

import java.util.HashMap;
import java.util.Map;

/**
 * Camada de console por cima do {@link GameSession}: lê o teclado, imprime e delega todas as
 * regras (escolhas, consequências, conquistas, finais, autosave) à sessão. Uma tela JavaFX faz o
 * mesmo papel trocando apenas as entradas/saídas.
 */
public class DialogueController {
    private final InputManager inputManager;
    private final Player player;
    private final AffinityTracker affinityTracker;
    private final DialogueView view;
    private final MenuView menuView;
    private final AchievementView achievementView;
    private final PlayerService playerService;
    private final AchievementService achievementService;
    private final EndingManager endingManager;
    private final GameConfig config;
    private final StateRepository stateRepository;
    private final AssetResolver assets = new AssetResolver();
    private GameSession session;
    private boolean returnToMenuRequested = false;

    public DialogueController(Player player, InputManager inputManager,
                              AffinityTracker affinityTracker, GameConfig config,
                              StateRepository stateRepository,
                              AchievementService achievementService,
                              EndingManager endingManager) {
        this.player = player;
        this.inputManager = inputManager;
        this.affinityTracker = affinityTracker;
        this.view = new DialogueView();
        this.menuView = new MenuView();
        this.achievementView = new AchievementView();
        this.playerService = new PlayerService();
        this.achievementService = achievementService;
        this.endingManager = endingManager;
        this.config = config;
        this.stateRepository = stateRepository;
    }

    /**
     * Joga a partir de {@code startingDialogueId} (nulo = primeiro diálogo do capítulo).
     * Em modo campanha segue para os capítulos seguintes do catálogo até um final, o último
     * capítulo, um erro de roteiro ou o jogador voltar ao menu.
     */
    public void playChapter(ChapterCatalog catalog, int chapterNumber, String startingDialogueId,
                            boolean continueToNextChapter) {
        returnToMenuRequested = false;
        session = new GameSession(catalog, chapterNumber, continueToNextChapter, player, affinityTracker,
                config, stateRepository, achievementService, endingManager.getService(), playerService, assets);

        boolean firstScreen = true;
        StepResult step = session.start(startingDialogueId);
        while (step != null) {
            if (step.snapshot() != null && step.snapshot().chapterChanged() && !firstScreen) {
                menuView.printMessage("\nCarregando Capítulo " + step.snapshot().chapterNumber() + "...");
            }
            firstScreen = false;
            printStep(step);
            if (!step.isShowing() || returnToMenuRequested) {
                break;
            }
            DialogueSnapshot snapshot = step.snapshot();
            step = snapshot.linear() ? advanceDialogue(snapshot) : processInput(snapshot);
            if (returnToMenuRequested) {
                break;
            }
        }
    }

    /** Mostra o diálogo do passo (com cabeçalho de cena e conquistas) e os avisos de fim. */
    private void printStep(StepResult step) {
        DialogueSnapshot snapshot = step.snapshot();
        if (snapshot != null) {
            if (snapshot.sceneChanged()) {
                view.printSceneHeader(snapshot.sceneTitle(), snapshot.sceneDescription());
            }
            view.printDialogue(snapshot.speaker(), snapshot.text(), config.getTextSpeedMs());
            step.dialogueAchievements().forEach(achievementView::printAchievementBanner);
        }
        switch (step.status()) {
            case ENDING -> endingManager.print(step.ending());
            case ERROR -> view.printDebugWarning(step.message());
            default -> { }
        }
    }

    /** Espera ENTER ("S" salva, "MENU" sai) e avança; devolve nulo se o jogador voltou ao menu. */
    public StepResult advanceDialogue(DialogueSnapshot snapshot) {
        while (true) {
            String command = inputManager.waitEnterOrCommand();

            if (command != null && command.equalsIgnoreCase("S")) {
                handleManualSave();
                view.printDialogue(snapshot.speaker(), snapshot.text(), 0);
            } else if (command != null && command.equalsIgnoreCase("MENU")) {
                handleExitToMenuPrompt();
                return null;
            } else {
                return session.advance();
            }
        }
    }

    /** Mostra as opções, lê a escolha e a aplica; devolve nulo se o jogador voltou ao menu. */
    public StepResult processInput(DialogueSnapshot snapshot) {
        view.printOptionsHeader();
        var options = snapshot.choices();

        for (DialogueSnapshot.ChoiceView option : options) {
            view.printOption(option.index(), option.text(), option.blockReason());
        }

        while (true) {
            int choice = inputManager.readChoiceOrCommand(1, options.size());

            if (choice == -99) { // Comando MENU
                handleExitToMenuPrompt();
                return null;
            }
            if (choice == -98) { // Comando S (Salvar)
                handleManualSave();
                continue;
            }

            DialogueSnapshot.ChoiceView chosen = options.get(choice - 1);

            if (!chosen.available()) {
                view.printRequirementWarning();
                continue;
            }
            if (config != null && config.isConfirmChoices()) {
                menuView.printMessage("Confirmar escolha \"" + chosen.text() + "\"? (Digite 'S' para confirmar ou outra tecla para cancelar):");
                String confirm = inputManager.readString().trim();
                if (!confirm.equalsIgnoreCase("S")) {
                    menuView.printMessage("Escolha cancelada. Selecione novamente.");
                    continue;
                }
            }

            StepResult result = session.choose(chosen.index());
            if (result.report() != null) {
                result.report().messages().forEach(view::printConsequence);
            }
            result.choiceAchievements().forEach(achievementView::printAchievementBanner);
            return result;
        }
    }

    private void handleExitToMenuPrompt() {
        menuView.printMessage("\nDeseja salvar o jogo antes de voltar ao Menu Principal? (Digite 'S' para Sim ou 'N' para Não):");
        System.out.print("> ");
        String confirm = inputManager.readString().trim();
        if (confirm.equalsIgnoreCase("S")) {
            handleManualSave();
        }
        menuView.printMessage("\nVoltando ao Menu Principal...");
        returnToMenuRequested = true;
    }

    public void handleManualSave() {
        Map<Integer, GameStates> slotsMap = new HashMap<>();
        for (int i = 1; i <= 4; i++) {
            slotsMap.put(i, stateRepository.loadStateFromSlot(i));
        }

        menuView.displayManualSaveMenu(slotsMap);
        int choice = inputManager.readValidChoice(0, 4);

        if (choice == 0) {
            menuView.printMessage("\nSalvamento cancelado.");
            return;
        }

        if (stateRepository.slotExists(choice)) {
            menuView.printOverwriteConfirmation(choice);
            String confirmation = inputManager.readString().trim();

            if (!confirmation.equalsIgnoreCase("S")) {
                menuView.printMessage("\n[Cancelado] O save do Slot " + choice + " não foi modificado.");
                menuView.printMessage("[Pressione ENTER para continuar]");
                inputManager.waitEnter();
                return;
            }
        }

        try {
            session.saveToSlot(choice);
            menuView.printMessage("\n[Sucesso] Jogo salvo no Slot " + choice + "!");
        } catch (GamePersistenceException e) {
            menuView.printMessage("\n[Erro de Gravação] " + e.getMessage());
        }

        menuView.printMessage("[Pressione ENTER para continuar]");
        inputManager.waitEnter();
    }

    public boolean isReturnToMenuRequested() {
        return returnToMenuRequested;
    }

    public InputManager getInputManager() {
        return inputManager;
    }
}
