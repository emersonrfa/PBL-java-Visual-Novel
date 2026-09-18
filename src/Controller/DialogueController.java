package Controller;

import Model.Options;
import Model.Dialogue;
import Model.Chapter;
import Model.Scene;
import Model.Player;
import Service.StoryService;
import Service.AffinityTracker;

import java.util.List;

public class DialogueController {
    private InputManager inputManager;
    private Player player;
    private AffinityTracker affinityTracker;

    public DialogueController(Player player, InputManager inputManager) {
        this.player = player;
        this.inputManager = inputManager; // Atribui a instância recebida
    }

    public DialogueController(Player player, InputManager inputManager, AffinityTracker affinityTracker) {
        this.player = player;
        this.inputManager = inputManager;
        this.affinityTracker = affinityTracker;
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
        var options = currentDialogue.getOptions();

        // EXIBIÇÃO DAS OPÇÕES
        for (int i = 0; i < options.size(); i++) {
            Options opt = options.get(i);

            if (opt.isAvailable(player)) {
                System.out.println((i + 1) + " - " + opt.getText());
            } else {
                // caso a opção esteja bloqueada
                String motivo = "";

                if (opt.getMoneyChange() < 0 && player.getMoney() < Math.abs(opt.getMoneyChange())) {
                    motivo = " [Trocos insuficientes]";
                }

                else if (opt.getRequiredFlag() != null && !player.getFlag(opt.getRequiredFlag())) {
                    motivo = " [???]";
                }

                else if (opt.getEnergyChange() < 0 && player.getEnergy() < Math.abs(opt.getEnergyChange())) {
                    motivo = " [Energia Insuficiente]";
                }

                System.out.println((i + 1) + " - [BLOQUEADA] " + opt.getText() + motivo);
            }
        }

        // VALIDAÇÃO DA ESCOLHA DA OPÇÃO
        int choice = -1;
        boolean validChoice = false;

        while (!validChoice) {
            choice = inputManager.readValidChoice(1, options.size());
            Options chosenOption = options.get(choice - 1);

            if (!chosenOption.isAvailable(player)) {
                System.out.println("Você não cumpre os requisitos para essa opção. Escolha outra.");
            } else {
                validChoice = true;
            }
        }

        int chosenIndex = choice - 1;
        Options chosenOption = options.get(chosenIndex);

        // consequencia de escolha: (tem q ve um jeito de pegar o objeto player e trazer aqui)
        if (chosenOption.getEnergyChange() < 0) player.consumeEnergy(Math.abs(chosenOption.getEnergyChange()));
        else if (chosenOption.getEnergyChange() > 0) player.addEnergy(chosenOption.getEnergyChange());

        if (chosenOption.getMoneyChange() < 0) player.spendMoney(Math.abs(chosenOption.getMoneyChange()));
        else if (chosenOption.getMoneyChange() > 0) player.addMoney(chosenOption.getMoneyChange());

        // Ativação de Flags
        if (chosenOption.getFlagToSet() != null) {
            player.setFlag(chosenOption.getFlagToSet(), true);
        }

        // Alteração de Afinidade
        if (chosenOption.getAffinityChange() != 0 && chosenOption.getTargetNpc() != null) {
            affinityTracker.modifyAffinity(chosenOption.getTargetNpc(), chosenOption.getAffinityChange());
        }

        return chosenIndex;
    }
}