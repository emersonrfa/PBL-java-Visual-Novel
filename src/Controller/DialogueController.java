package Controller;

import Model.Options;
import Model.Dialogue;
import Model.Chapter;
import Model.Scene;
import Model.Player;
import Service.StoryService;

public class DialogueController {
    private InputManager inputManager;
    private Player player;

    public DialogueController(Player player, InputManager inputManager) {
        this.player = player;
        this.inputManager = inputManager; // Atribui a instância recebida
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
        inputManager.waitEnter();
    }

    public void playChapter(Chapter chapter) {
        String currentId = "d1.1"; // primeiro dialogo de todos
        Scene currentScene = null;

        while (currentId != null && !currentId.isEmpty()) {
            // localiza a cena que tem o dialogo atual
            Scene nextScene = StoryService.findSceneByDialogueId(chapter, currentId);

            if (nextScene == null) {
                System.out.println("> AVISO DEBUG: JOGO ENCERRADO POR NÃO TER ACHADO ID DE PRÓXIMA CENA (" + currentId + ")");
                break; // da break se nn encontrar nenhum
            }

            if (currentScene != nextScene) {
                currentScene = nextScene;
                System.out.println("\n<--------------------------->");
                System.out.println("  CENA: " + currentScene.getTitle().toUpperCase());
                System.out.println("  DESCRIÇÃO: " + currentScene.getDescription());
                System.out.println("<--------------------------->");
            }

            Dialogue current = currentScene.getDialogueById(currentId);
            displayDialogue(current);

            if (current.getOptions().isEmpty()) {
                System.out.println("> AVISO DEBUG: JOGO ENCERRADO POR NÃO TER ACHADO NENHUMA OPÇÃO DE DIÁLOGO (" + currentId + ")");
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

    // mostra as opções do diálogo, utiliza o inputManager para identificar qual foi escolhida
    // e depois processa alteração de valores de player (pode ser que troque de local que faz isso no futuro)
    public int processinput(Dialogue currentDialogue) {
        System.out.println();

        for (int i = 0; i < currentDialogue.getOptions().size(); i++) {
            System.out.println((i + 1) + " - " + currentDialogue.getOptions().get(i).getText());
        }

        int choice = inputManager.readValidChoice(1, currentDialogue.getOptions().size());

        int chosenIndex = choice - 1;
        Options chosenOption = currentDialogue.getOptions().get(chosenIndex);

        // consequencia de escolha: (tem q ve um jeito de pegar o objeto player e trazer aqui)
        if (chosenOption.getEnergyChange() < 0) { // variando energia
            player.consumeEnergy(Math.abs(chosenOption.getEnergyChange())); // math.abs é módulo!!! deixa assim
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