package Controller;

import Model.*;
import Repository.ConfigRepository;
import Repository.StateRepository;
import Repository.StoryRepository;
import Service.AffinityTracker;
import View.MenuView;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private boolean isRunning;
    private DialogueController dialogueController;
    private MenuController menuController;
    private StoryRepository storyRepository;
    private EndingManager endingManager;
    private GameConfig config;
    private StateRepository stateRepository;
    private GameStates currentGameState;
    private MenuView menuView;

    public GameManager(Player player, InputManager inputManager) {
        this(player, inputManager, null);
    }

    public GameManager(Player player, InputManager inputManager, GameStates activeState) {
        AffinityTracker tracker = new AffinityTracker();
        if (activeState != null) {
            new Service.PlayerService().restoreAffinityFromState(activeState, tracker);
        }
        this.menuView = new MenuView();

        ConfigRepository configRepository = new ConfigRepository();
        this.config = configRepository.getCurrentConfig();

        this.stateRepository = new StateRepository();
        this.currentGameState = (activeState != null) ? activeState : this.stateRepository.getCurrentStates();

        this.dialogueController = new DialogueController(player, inputManager, tracker, this.config, this.stateRepository);
        this.menuController = new MenuController(inputManager, configRepository);
        this.storyRepository = new StoryRepository();
        this.endingManager = new EndingManager();
    }

    /**
     * Novo Jogo: vai apenas para o Capítulo 1 inicial.
     */
    public void startGame() {
        this.isRunning = true;
        Chapter prologue = storyRepository.buildPrologue();
        menuView.printMessage("\nCarregando Capítulo 1...");
        dialogueController.playChapter(prologue);
        this.isRunning = false;
    }

    /**
     * Carrega o jogo a partir do capítulo e diálogo salvos, sem cair na seleção de capítulos ao sair.
     */
    public void loadAndStartGame() {
        this.isRunning = true;

        if (this.currentGameState == null) {
            menuView.printMessage("\nNenhum save encontrado. Iniciando novo jogo...");
            startGame();
            return;
        }

        int savedChapter = this.currentGameState.getCurrentChapterId();
        String savedDialogId = this.currentGameState.getCurrentDialogId();

        menuView.printMessage("\nRetomando a partida no Capítulo " + savedChapter + " [Diálogo: " + savedDialogId + "]...");

        switch (savedChapter) {
            case 1:
                Chapter prologue = storyRepository.buildPrologue();
                dialogueController.playChapter(prologue, savedDialogId);
                break;

            case 2:
                Chapter cap2 = storyRepository.buildEntrevista();
                dialogueController.playChapter(cap2, savedDialogId);
                break;

            default:
                menuView.printMessage("\nCapítulo desconhecido no save (" + savedChapter + ").");
                break;
        }

        this.isRunning = false;
    }

    /**
     * Menu de seleção de capítulos (acessível apenas ao digitar ADMIN no Menu Inicial).
     */
    public void gameLoop() {
        this.isRunning = true;
        while (isRunning) {
            int choice = menuController.chapterSelection();

            switch (choice) {
                case 1:
                    Chapter prologue = storyRepository.buildPrologue();
                    menuView.printMessage("\nCarregando capítulo 1...");
                    dialogueController.playChapter(prologue);
                    break;
                case 2:
                    Chapter cap2 = storyRepository.buildEntrevista();
                    menuView.printMessage("\nCarregando capítulo 2...");
                    dialogueController.playChapter(cap2);
                    break;
                case 11:
                    endingGalleryLoop();
                    break;
                case 0:
                    menuView.printMessage("\nVoltando para o Menu Principal...");
                    isRunning = false;
                    break;
            }
        }
    }

    private void endingGalleryLoop() {
        boolean inGallery = true;
        List<Ending> allEndings = endingManager.getAllEndings();

        List<String> titles = new ArrayList<>();
        for (Ending e : allEndings) {
            titles.add(e.getTitle());
        }

        while (inGallery) {
            int choice = menuController.endingSelection(titles);

            if (choice == 0) {
                inGallery = false;
            } else {
                Ending selected = allEndings.get(choice - 1);
                endingManager.triggerEnding(selected.getId());

                menuView.printMessage("\n[Pressione ENTER para voltar à galeria]");
                dialogueController.getInputManager().waitEnter();
            }
        }
    }
}