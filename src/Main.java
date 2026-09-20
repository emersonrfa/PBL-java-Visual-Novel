import Controller.GameManager;
import Controller.InputManager;
import Controller.MenuController;
import Model.Player;
import View.MenuView;

void main() {
    // Inicializa gerenciador de input e as views/controllers do menu
    InputManager inputManager = new InputManager();
    MenuView menuView = new MenuView();
    MenuController menuController = new MenuController(inputManager);

    menuView.printMessage("inicializando jogo...\n");

    boolean systemRunning = true;

    // Loop principal do sistema
    while (systemRunning) {
        String menuResponse = menuController.start();

        if (menuResponse.equals("jogar")) {
            menuView.printBloodTypePrompt();
            String tipoSanguineo = inputManager.readBloodType();

            Player player = new Player(tipoSanguineo);
            menuView.printMessage("\nTipo sanguíneo [" + player.getBloodType() + "] registrado com sucesso.\n"); // AFIM DE TESTES

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