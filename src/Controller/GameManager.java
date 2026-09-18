package Controller;

import Model.StoryState;
import Model.Player;
import Model.Chapter;
import Repository.InMemoryStoryRepository; // Importando o novo repositório!

public class GameManager {
    private StoryState currentState;
    private boolean isRunning;
    private DialogueController dialogueController;

    public GameManager() {
        this.dialogueController = new DialogueController();
    }

    public boolean startGame(Player player) {
        this.isRunning = true;

        // Usamos o Repository em vez do antigo StoryBuilder
        InMemoryStoryRepository storyRepo = new InMemoryStoryRepository();
        Chapter prologue = storyRepo.loadPrologue();

        // O DialogueController assume o controle para exibir a cena
        dialogueController.playChapter(prologue);

        return true;
    }

    public void gameLoop(){

    }
}