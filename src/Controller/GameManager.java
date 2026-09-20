package Controller;

import Model.Player;
import Model.Chapter;
import Repository.StoryRepository;
import Service.AffinityTracker;
import Model.Ending;
import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private boolean isRunning;
    private DialogueController dialogueController;
    private MenuController menuController;
    private StoryRepository storyRepository;
    private EndingManager endingManager;

    public GameManager(Player player, InputManager inputManager) {
        AffinityTracker tracker = new AffinityTracker();
        this.dialogueController = new DialogueController(player, inputManager, tracker);
        this.menuController = new MenuController(inputManager);
        this.storyRepository = new StoryRepository();
        this.endingManager = new EndingManager();
    }

    public void startGame() {
        this.isRunning = true;

        gameLoop();
    }

    public void gameLoop(){
        while(isRunning) {
            int choice = menuController.chapterSelection();

            switch (choice) {
                case 1:
                    Chapter prologue = storyRepository.buildPrologue();
                    System.out.println("\nCarregando capítulo 1...");
                    dialogueController.playChapter(prologue);
                    break;
                case 2:
                    Chapter cap2 = storyRepository.buildEntrevista();
                    System.out.println("\nCarregando capítulo 2...");
                    dialogueController.playChapter(cap2);
                    break;

                    //fazer os outros

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