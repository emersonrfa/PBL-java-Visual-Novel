package Controller;

import Model.Options;
import Model.Dialogue;
import Model.Chapter;
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

    public void playChapter(Chapter chapter) {
        String currentId = "d1.1"; // primeiro dialogo de todos
        Scene currentScene = null;

        while (currentId != null && !currentId.isEmpty()) {
            // localiza a cena que tem o dialogo atual
            Scene nextScene = chapter.getSceneByDialogueId(currentId);

            if (nextScene == null) {
                break; // da break se nn encontrar nenhum
            }

            // exibe sempre as caracteristicas da cena atual:
            currentScene = nextScene;
            System.out.println("\n<--------------------------->");
            System.out.println("  CENA: " + currentScene.getTitle().toUpperCase());
            System.out.println("  DESCRIÇÃO: " + currentScene.getDescription());
            System.out.println("<--------------------------->");


            Dialogue current = currentScene.getDialogueById(currentId);
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

        int chosenIndex = choice - 1;
        Options chosenOption = currentDialogue.getOptions().get(chosenIndex);

        // consequencia de escolha: (tem q ve um jeito de pegar o objeto player e trazer aqui)
        if (chosenOption.getEnergyChange() < 0) { // variando energia
            player.ConsumeEnergy(Math.abs(chosenOption.getEnergyChange())); // math.abs é módulo!!! deixa assim
        } else if (chosenOption.getEnergyChange() > 0) {
            player.addEnergy(chosenOption.getEnergyChange());
        }

        if (chosenOption.getMoneyChange() < 0) { // variando dinheiro
            player.spendMoney(Math.abs(chosenOption.getMoneyChange()));
        } else if (chosenOption.getMoneyChange() > 0) {
            player.addMoney(chosenOption.getMoneyChange());
        }

        return chosenIndex;
    }
}