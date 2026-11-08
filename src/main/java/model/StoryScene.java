package model;

import java.util.ArrayList;
import java.util.List;

public class StoryScene {
    private String title;
    private String description;
    private List<Dialogue> dialogues;
    private String backgroundId; // opcional: nome da imagem de fundo (sem extensão); padrão = título da cena

    public StoryScene(String title, String description) {
        this.title = title;
        this.description = description;
        this.dialogues = new ArrayList<>();
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

    public void addDialogue(Dialogue dialogue) {
        this.dialogues.add(dialogue);
    }

    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public List<Dialogue> getDialogues() { return dialogues; }


    /** Id da imagem de fundo definido no roteiro, ou {@code null} para usar o título da cena. */
    public String getBackgroundId() { return backgroundId; }

    public void setBackgroundId(String backgroundId) { this.backgroundId = backgroundId; }
}
