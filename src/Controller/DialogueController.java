package Controller;

import Model.Dialogue;
import Model.Chapter;
import Model.Scene;
import java.util.Scanner;

public class DialogueController {
    private Scanner scanner = new Scanner(System.in);

    // Lógica de busca que foi removida da Entidade Chapter
    private Scene findSceneByDialogueId(Chapter chapter, String dialogueId) {
        if (dialogueId == null) return null;
        for (Scene scene : chapter.getScenes()) {
            if (findDialogueById(scene, dialogueId) != null) {
                return scene;
            }
        }
        return null;
    }

    // Lógica de busca que foi removida da Entidade Scene
    private Dialogue findDialogueById(Scene scene, String id) {
        if (id == null) return null;
        for (Dialogue d : scene.getDialogues()) {
            if (d.getId().equals(id)) {
                return d;
            }
        }
        return null;
    }

    public void playChapter(Chapter chapter) {
        String currentId = "d1.1";
        Scene currentScene = null;

        while (currentId != null && !currentId.isEmpty()) {
            // Usa o novo método auxiliar de busca
            Scene nextScene = findSceneByDialogueId(chapter, currentId);

            if (nextScene == null) {
                break;
            }

            currentScene = nextScene;
            System.out.println("\n<--------------------------->");
            System.out.println("  CENA: " + currentScene.getTitle().toUpperCase());
            System.out.println("  DESCRIÇÃO: " + currentScene.getDescription());
            System.out.println("<--------------------------->");

            // Usa o novo método auxiliar de busca
            Dialogue current = findDialogueById(currentScene, currentId);
            displayDialogue(current);

            if (current.getOptions().isEmpty()) {
                break;
            }

            if (current.getOptions().size() == 1 && current.getOptions().get(0).getText().equals("...")) {
                advanceDialogue();
                currentId = current.getOptions().get(0).getNextDialogueId();
            } else {
                int choiceIndex = processinput(current);
                currentId = current.getOptions().get(choiceIndex).getNextDialogueId();
            }
        }
    }

    public void displayDialogue(Dialogue dialogue) {
        System.out.println("\n--------------------------------------------------");
        if (!dialogue.getSpeaker().equals("Narrador")) {
            System.out.println("[" + dialogue.getSpeaker() + "]:");
        }
        System.out.println(dialogue.getText());
    }

    public void advanceDialogue() {
        System.out.print("\n[Pressione ENTER para continuar]");
        scanner.nextLine();
    }

    public int processinput(Dialogue currentDialogue) {
        System.out.println();
        for (int i = 0; i < currentDialogue.getOptions().size(); i++) {
            System.out.println((i + 1) + " - " + currentDialogue.getOptions().get(i).getText());
        }

        int choice = -1;
        while (choice < 1 || choice > currentDialogue.getOptions().size()) {
            System.out.print("\nSua escolha: ");
            try {
                choice = Integer.parseInt(scanner.nextLine());
                if (choice < 1 || choice > currentDialogue.getOptions().size()) {
                    System.out.println("Opção inválida. Digite um dos números acima.");
                }
            } catch (NumberFormatException e) {
                System.out.println("Por favor, digite apenas números.");
            }
        }
        return choice - 1;
    }
}