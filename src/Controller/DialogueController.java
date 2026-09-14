package Controller;

import Model.Dialogue;
import Model.Options;
import Model.Player;
import Model.Scene;
import java.util.Scanner;

public class DialogueController {
    private Scanner scanner = new Scanner(System.in);

    public Scene startScene(Scene scene) {
        // capitulos começam em 1.1
        String currentId = "d1.1";

        while (currentId != null && !currentId.isEmpty()) {
            Dialogue current = scene.getDialogueById(currentId);

            //fim da cena se a ID não for encontrada
            if (current == null) {
                break;
            }

            displayDialogue(current);

            // se não houver opções cadastradas, encerra por precaucao
            if (current.getOptions().isEmpty()) {
                break;
            }

            // verifica se é apenas um avanço linear (opção única "...")
            if (current.getOptions().size() == 1 && current.getOptions().get(0).getText().equals("...")) {
                advanceDialogue();
                currentId = current.getOptions().get(0).getNextDialogueId();
            }
            // se for um menu de varias escolhas
            else {
                int choiceIndex = processinput(current);
                currentId = current.getOptions().get(choiceIndex).getNextDialogueId();
            }
        }
        return scene;
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

    // Adaptado para receber o Dialogue atual e retornar o índice escolhido
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
        return choice - 1; // Retorna o índice correto da List (0-based)
    }
}