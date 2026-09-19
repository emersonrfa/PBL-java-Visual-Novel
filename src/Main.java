import Controller.GameManager;
import Controller.InputManager;
import Controller.MenuController;
import Model.NPC;
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

            // Criação dos NPCs (pode ser movido para o GameManager no futuro)
            NPC arthur = new NPC("Vizinho e amigo de Emily","Casa de Arthur","Arthur",
                    "Cabelo cacheado, bigode fino, moreno. Meio desajeitado, já é um amigo de Emily de longa data",
                    "Masculino",22);

            NPC santoPedro = new NPC("Coordenador geral da organização","Sede da Organização",
                    "Santo Pedro ","homem grande, forte, mais velho e que dá uma sensação de caçador",
                    "Masculino",54);

            gameManager.startGame();

            // Quando o startGame acabar (jogador morrer ou zerar), ele encerra o sistema
            // (Se quiser que volte pro menu principal após o fim de jogo, remova a linha abaixo)
            systemRunning = false;
        }
        else if (menuResponse.equals("sair")) {
            menuView.printMessage("finalizando sistema...");
            systemRunning = false;
        }
    }

    inputManager.close();
}