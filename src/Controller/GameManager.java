package Controller;

import Model.StoryState;
import Model.Player;
import Model.Chapter;
import Repository.StoryRepository;
import Service.AffinityTracker;

public class GameManager {
    private StoryState currentState;
    private boolean isRunning;
    private DialogueController dialogueController;


    public GameManager(Player player, InputManager inputManager) {
        AffinityTracker tracker = new AffinityTracker();
        this.dialogueController = new DialogueController(player, inputManager, tracker);
    }

    public void startGame() {
        this.isRunning = true;

        // Usamos o Repository em vez do antigo StoryBuilder
        StoryRepository storyRepo = new StoryRepository();
        Chapter prologue = storyRepo.buildPrologue();

        // O DialogueController assume o controle para exibir a cena
        dialogueController.playChapter(prologue);
        System.out.println("> fim de jogo, acabaram-se os capitulos (AVISO)");

    }

    public void gameLoop(){

    }
}