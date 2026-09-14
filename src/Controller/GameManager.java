package Controller;

import Model.StoryState;
import Model.Player;
import Model.Chapter;

public class GameManager {
    private StoryState currentState;
    private boolean isRunning;
    private DialogueController dialogueController;

    public GameManager() {
        this.dialogueController = new DialogueController();
    }

    public boolean startGame(Player player) {
        this.isRunning = true;

        Chapter prologue = StoryBuilder.buildPrologue(player);

        dialogueController.startScene(prologue.getScenes().get(0));

        return true;
    }

    public void gameLoop(){

    }

}
