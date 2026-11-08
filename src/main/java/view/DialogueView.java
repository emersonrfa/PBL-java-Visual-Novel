package view;

import model.Dialogue;
import model.StoryScene;

public class DialogueView {

    public void printSceneHeader(StoryScene scene) {
        printSceneHeader(scene.getTitle(), scene.getDescription());
    }

    public void printSceneHeader(String title, String description) {
        System.out.println("\n<--------------------------->");
        System.out.println("  CENA: " + title.toUpperCase());
        System.out.println("  DESCRIÇÃO: " + description);
        System.out.println("<--------------------------->");
    }

    public void printDialogue(Dialogue dialogue, int textSpeedMs) {
        printDialogue(dialogue.getSpeaker(), dialogue.getText(), textSpeedMs);
    }

    public void printDialogue(String speaker, String text, int textSpeedMs) {
        System.out.println("\n--------------------------------------------------");
        if (!speaker.equals("Narrador")) {
            System.out.println("[" + speaker + "]:");
        }

        if (textSpeedMs <= 0) {
            System.out.println(text);
        } else {
            for (char c : text.toCharArray()) {
                System.out.print(c);
                try {
                    Thread.sleep(textSpeedMs);
                } catch (InterruptedException ignored) {}
            }
            System.out.println();
        }
    }

    public void printAdvancePrompt() {
        System.out.print("\n[Pressione ENTER para continuar]");
    }

    public void printOptionsHeader() {
        System.out.println(); // Apenas pular uma linha antes das opções
    }

    public void printOption(int index, String text, String blockReason) {
        if (blockReason == null || blockReason.isEmpty()) {
            System.out.println((index + 1) + " - " + text);
        } else {
            System.out.println((index + 1) + " - [BLOQUEADA] " + text + " " + blockReason);
        }
    }

    public void printRequirementWarning() {
        System.out.println("Você não cumpre os requisitos para essa opção.");
    }

    public void printConsequence(String message) {
        System.out.println("\n" + message);
    }

    public void printDebugWarning(String message) {
        System.out.println("> AVISO DEBUG: " + message);
    }
}