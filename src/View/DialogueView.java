package View;

import Model.Dialogue;
import Model.Scene;

public class DialogueView {

    public void printSceneHeader(Scene scene) {
        System.out.println("\n<--------------------------->");
        System.out.println("  CENA: " + scene.getTitle().toUpperCase());
        System.out.println("  DESCRIÇÃO: " + scene.getDescription());
        System.out.println("<--------------------------->");
    }

    public void printDialogue(Dialogue dialogue) {
        System.out.println("\n--------------------------------------------------");
        if (!dialogue.getSpeaker().equals("Narrador")) {
            System.out.println("[" + dialogue.getSpeaker() + "]:");
        }
        System.out.println(dialogue.getText());
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