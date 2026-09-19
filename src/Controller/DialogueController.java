package Controller;

import Model.Options;
import Model.Dialogue;
import Model.Chapter;
import Model.Scene;
import Model.Player;
import Service.StoryService;
import Service.AffinityTracker;
import View.DialogueView;

public class DialogueController {
    private InputManager inputManager;
    private Player player;
    private AffinityTracker affinityTracker;
    private DialogueView view; // Nova View adicionada

    public DialogueController(Player player, InputManager inputManager) {
        this.player = player;
        this.inputManager = inputManager;
        this.view = new DialogueView(); // Inicializa a View
    }

    public DialogueController(Player player, InputManager inputManager, AffinityTracker affinityTracker) {
        this.player = player;
        this.inputManager = inputManager;
        this.affinityTracker = affinityTracker;
        this.view = new DialogueView(); // Inicializa a View
    }

    public void advanceDialogue() {
        view.printAdvancePrompt();
        inputManager.waitEnter();
    }

    public void playChapter(Chapter chapter) {
        String currentId = "d1.1"; // primeiro dialogo de todos
        Scene currentScene = null;
        EndingManager endingManager = new EndingManager();

        while (currentId != null && !currentId.isEmpty()) {
            // localiza a cena que tem o dialogo atual
            Scene nextScene = StoryService.findSceneByDialogueId(chapter, currentId);

            if (nextScene == null) {
                view.printDebugWarning("JOGO ENCERRADO POR NÃO TER ACHADO ID DE PRÓXIMA CENA (" + currentId + ")");
                break;
            }

            if (endingManager.isEndingCode(currentId)) {
                endingManager.triggerEnding(currentId);
                break; // Encerra o laço e o jogo
            }

            if (currentScene != nextScene) {
                currentScene = nextScene;
                view.printSceneHeader(currentScene); // Delega para a View
            }

            Dialogue current = currentScene.getDialogueById(currentId);
            view.printDialogue(current); // Delega para a View

            if (current.getOptions().isEmpty()) {
                view.printDebugWarning("JOGO ENCERRADO POR NÃO TER ACHADO NENHUMA OPÇÃO DE DIÁLOGO (" + currentId + ")");
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

    public int processinput(Dialogue currentDialogue) {
        view.printOptionsHeader();
        var options = currentDialogue.getOptions();

        // EXIBIÇÃO DAS OPÇÕES
        for (int i = 0; i < options.size(); i++) {
            Options opt = options.get(i);

            if (opt.isAvailable(player, affinityTracker)) {
                view.printOption(i, opt.getText(), "");
            } else {
                // Controller descobre o motivo lógico do bloqueio
                String motivo = "";

                if (opt.getMoneyChange() < 0 && player.getMoney() < Math.abs(opt.getMoneyChange())) {
                    motivo = "[Trocos insuficientes]";
                }
                else if (opt.getRequiredFlag() != null && !player.getFlag(opt.getRequiredFlag())) {
                    motivo = "[???]";
                }
                else if (opt.getEnergyChange() < 0 && player.getEnergy() < Math.abs(opt.getEnergyChange())) {
                    motivo = "[Energia Insuficiente]";
                }
                else if (opt.getRequiredItemName() != null && !player.getInventory().hasItem(opt.getRequiredItemName())) {
                    motivo = "[Requer: " + opt.getRequiredItemName() + "]";
                }
                else if (opt.getTargetNpc() != null) {
                    if (affinityTracker.getAffinity(opt.getTargetNpc()) < opt.getRequiredAffinity())
                        motivo = "[Afinidade Insuficiente com " + opt.getTargetNpc() + "]";
                    else if (affinityTracker.getSuspicion(opt.getTargetNpc()) > opt.getMaxSuspicionAllowed())
                        motivo = "[Você é suspeita demais para esta opção]";
                }

                view.printOption(i, opt.getText(), motivo); // View imprime o motivo
            }
        }

        // VALIDAÇÃO DA ESCOLHA DA OPÇÃO
        int choice = -1;
        boolean validChoice = false;

        while (!validChoice) {
            choice = inputManager.readValidChoice(1, options.size());
            Options chosenOption = options.get(choice - 1);

            if (!chosenOption.isAvailable(player, affinityTracker)) {
                view.printRequirementWarning(); // Delega para a View
            } else {
                validChoice = true;
            }
        }

        int chosenIndex = choice - 1;
        Options chosenOption = options.get(chosenIndex);

        // CONSEQUÊNCIAS DE ESCOLHA LOGICA:
        if (chosenOption.getEnergyChange() < 0) player.consumeEnergy(Math.abs(chosenOption.getEnergyChange()));
        else if (chosenOption.getEnergyChange() > 0) player.addEnergy(chosenOption.getEnergyChange());

        if (chosenOption.getMoneyChange() < 0) player.spendMoney(Math.abs(chosenOption.getMoneyChange()));
        else if (chosenOption.getMoneyChange() > 0) player.addMoney(chosenOption.getMoneyChange());

        if (chosenOption.getFlagToSet() != null) {
            player.setFlag(chosenOption.getFlagToSet(), true);
        }

        if (chosenOption.getAffinityChange() != 0 && chosenOption.getTargetNpc() != null) {
            affinityTracker.modifyAffinity(chosenOption.getTargetNpc(), chosenOption.getAffinityChange());
        }

        if (chosenOption.getRequiredItemName() != null && chosenOption.isConsumeRequiredItem()) {
            player.getInventory().removeItem(chosenOption.getRequiredItemName());
            view.printConsequence("[ -1 " + chosenOption.getRequiredItemName() + "]"); // View exibe texto
        }

        if (chosenOption.getItemToGive() != null) {
            player.getInventory().addItem(chosenOption.getItemToGive());
            view.printConsequence("[ +1 " + chosenOption.getItemToGive().getName() + "]"); // View exibe texto
        }

        if (chosenOption.getTargetNpc() != null) {
            // (Nota: Esta lógica já estava levemente duplicada no código original e foi mantida como estava)
            if (chosenOption.getAffinityChange() != 0) {
                affinityTracker.modifyAffinity(chosenOption.getTargetNpc(), chosenOption.getAffinityChange());
            }
            if (chosenOption.getSuspicionChange() != 0) {
                affinityTracker.modifySuspicion(chosenOption.getTargetNpc(), chosenOption.getSuspicionChange());
            }
        }

        return chosenIndex;
    }
}