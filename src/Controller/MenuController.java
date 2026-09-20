package Controller;

import View.MenuView;
import java.util.List;

//agora em vem de usar o Scanner ele usa o inputMagager
public class MenuController {
    private String[] options = {"Jogar", "Creditos", "Sair"};
    private int selectedOption = 0;
    private MenuView view;
    private InputManager inputManager;

    public MenuController(InputManager inputManager) {
        this.inputManager = inputManager;
        this.view = new MenuView();
    }

    public String start() {
        boolean running = true;

        while (running) {
            // 1. O Controller pede pra View desenhar o estado atual
            view.printMenu(options, selectedOption);

            // 2. O Controller pega a entrada do usuário
            String input = inputManager.readString().toUpperCase();

            // 3. O Controller processa a lógica
            if (input.equals("W")) {
                selectedOption--;
                if(selectedOption < 0) {
                    selectedOption = options.length - 1; // volta pro fim
                }
            }
            else if (input.equals("S")) {
                selectedOption++;
                if (selectedOption >= options.length) {
                    selectedOption = 0; // volta pro topo
                }
            }
            else if (input.equals("E")) {
                if (selectedOption == 0) {
                    view.printMessage("iniciando jogo....\n");
                    return "jogar";
                }
                else if (selectedOption == 1) {
                    view.printCredits();
                    inputManager.waitEnter(); // Pausa até dar Enter
                    // não encerra o loop, assim ele redesenha o menu
                }
                else if (selectedOption == 2) {
                    view.printMessage("saindo...\n");
                    running = false;
                    return "sair";
                }
            }
        }
        return "sair";
    }

    public int chapterSelection() {
        view.displayChapterSelection();
        return inputManager.readValidChoice(0, 11);
    }

    public int endingSelection(List<String> endingTitles) {
        view.displayEndingSelection(endingTitles);
        return inputManager.readValidChoice(0, endingTitles.size());
    }
}