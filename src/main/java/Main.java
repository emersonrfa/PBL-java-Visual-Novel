import Controller.GameManager;
import Controller.InputManager;
import Controller.MenuController;
import Model.Player;
import View.MenuView;

public class Main {

    public static void main(String[] args) {
        InputManager inputManager = new InputManager();
        MenuView menuView = new MenuView();
        MenuController menuController = new MenuController(inputManager);

        menuView.printMessage("inicializando jogo...\n");

        boolean systemRunning = true;

        while (systemRunning) {
            String menuResponse = menuController.start();

            if (menuResponse.equals("jogar")) {
                menuView.printBloodTypePrompt();
                String tipoSanguineo = inputManager.readBloodType();

                Player player = new Player(tipoSanguineo);
                menuView.printMessage("\nTipo sanguíneo [" + player.getBloodType() + "] registrado com sucesso.\n");

                GameManager gameManager = new GameManager(player, inputManager);
                gameManager.startGame();
            }
            else if (menuResponse.equals("sair")) {
                menuView.printMessage("finalizando sistema...");
                systemRunning = false;
            }
        }

        inputManager.close();
    }
}