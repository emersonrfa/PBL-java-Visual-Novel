package Model;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Scene {
    private String title;
    private String description;
    private List<Dialogue> dialogues;

    public Scene(String title, String description) { //metodo construtor
        this.title = title;
        this.description = description;
        this.dialogues = new ArrayList<>();
    }

    public void addDialogue(Dialogue dialogue) {
        this.dialogues.add(dialogue);
    }

    // para adicionar dialogo simples direto (os que estao entre dialogos e basta clicar pra passar)
    public void addSimpleDialogue(String id, String text, String nextDialogueId) {
        this.dialogues.add(new Dialogue(id, text, nextDialogueId));
    }

    public void addSimpleDialogue(String id, String speaker, String expression, String text, String nextDialogueId) {
        this.dialogues.add(new Dialogue(id, speaker, expression, text, nextDialogueId));
    }

    public Dialogue getDialogueById(String id) {
        if (id == null) {
            return null;
        }

        for (Dialogue d : this.dialogues) {
            if (d.getId().equals(id)) {
                return d;
            }
        }

        // Retorna null se não encontrar o ID ou se a cena chegou ao fim
        return null;
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public List<Dialogue> getDialogues() { return dialogues; }
}
