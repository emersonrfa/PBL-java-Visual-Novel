package Controller;

import Model.*;
import Repository.ConfigRepository;
import Repository.StateRepository;
import Repository.StoryRepository;
import Service.AffinityTracker;

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

    public GameManager(Player player, InputManager inputManager) {
        AffinityTracker tracker = new AffinityTracker();

        // Carrega as configurações
        ConfigRepository configRepository = new ConfigRepository();
        this.config = configRepository.getCurrentConfig();

        // Estado do Jogo (Progresso / Save)
        this.stateRepository = new StateRepository();
        this.currentGameState = this.stateRepository.getCurrentStates();

        this.dialogueController = new DialogueController(player, inputManager, tracker, this.config, this.stateRepository);

        this.menuController = new MenuController(inputManager, configRepository);
        this.storyRepository = new StoryRepository();
        this.endingManager = new EndingManager();
    }

    public void startGame() {
        this.isRunning = true;
        gameLoop();
    }

    // NOVO: Retoma o jogo diretamente a partir do capítulo e diálogo salvos
    public void loadAndStartGame() {
        this.isRunning = true;

        if (this.currentGameState == null) {
            System.out.println("\nNenhum save encontrado. Iniciando novo jogo...");
            startGame();
            return;
        }

        int savedChapter = this.currentGameState.getCurrentChapterId();
        String savedDialogId = this.currentGameState.getCurrentDialogId();

        System.out.println("\nRetomando a partida no Capítulo " + savedChapter + " [Diálogo: " + savedDialogId + "]...");

        switch (savedChapter) {
            case 1:
                Chapter prologue = storyRepository.buildPrologue();
                dialogueController.playChapter(prologue, savedDialogId);

                // Ao terminar o prólogo, avança naturalmente para a continuação
                Chapter entrevista = storyRepository.buildEntrevista();
                dialogueController.playChapter(entrevista);
                break;

            case 2:
                Chapter cap2 = storyRepository.buildEntrevista();
                dialogueController.playChapter(cap2, savedDialogId);
                break;

            default:
                System.out.println("\nCapítulo desconhecido no save (" + savedChapter + "). Abrindo seleção de capítulos...");
                gameLoop();
                break;
        }

        // Caso termine os capítulos do save, entra no loop geral para permitir novas escolhas ou seleção
        gameLoop();
    }

    public void gameLoop() {
        while (isRunning) {
            int choice = menuController.chapterSelection();

            switch (choice) {
                case 1:
                    Chapter prologue = storyRepository.buildPrologue();
                    Chapter entrevista = storyRepository.buildEntrevista();
                    System.out.println("\nCarregando capítulo 1...");
                    dialogueController.playChapter(prologue);
                    dialogueController.playChapter(entrevista);
                    break;
                case 2:
                    Chapter cap2 = storyRepository.buildEntrevista();
                    System.out.println("\nCarregando capítulo 2...");
                    dialogueController.playChapter(cap2);
                    break;

                // fazer os outros

                case 11:
                    endingGalleryLoop();
                    break;
                case 0:
                    System.out.println("\nVoltando para o Menu Principal...");
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
            }
            else {
                Ending selected = allEndings.get(choice - 1);
                endingManager.triggerEnding(selected.getId());

                System.out.println("\n[Pressione ENTER para voltar à galeria]");
                dialogueController.getInputManager().waitEnter();
            }
        }
    }
}