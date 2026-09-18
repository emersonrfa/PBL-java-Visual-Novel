package Model;

import java.util.ArrayList;
import java.util.List;

public class Scene {
    private String title;
    private String description;
    private List<Dialogue> dialogues;

    public Scene(String title, String description) {
        this.title = title;
        this.description = description;
        this.dialogues = new ArrayList<>();
    }

    public void addDialogue(Dialogue dialogue) {
        this.dialogues.add(dialogue);
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public List<Dialogue> getDialogues() { return dialogues; }

    public void setDialogues(List<Dialogue> dialogues) {
        this.dialogues = dialogues;
    }
}
