import Controller.GameManager;
import Controller.InputManager;
import Model.NPC;
import Model.Player;

void main() {
    Menu menu = new Menu();
    InputManager inputManager = new InputManager();

    System.out.printf("inicializando jogo...\n");

    String menuresponse = menu.start();

    if (menuresponse.equals("jogar")) {

        System.out.println("\nQual seu tipo sanguíneo, Emily? (Ex: A+, O-, AB+):");
        System.out.print("> ");
        String tipoSanguineo = inputManager.readBloodType();

        Player player = new Player(tipoSanguineo);

        System.out.println("\nTipo sanguíneo [" + player.getBloodType() + "] registrado com sucesso.\n"); // AFIM DE TESTES

        GameManager gameManager = new GameManager(player, inputManager);

        NPC arthur = new NPC("Vizinho e amigo de Emily","Casa de Arthur","Arthur",
                "Cabelo cacheado, bigode fino, moreno. Meio desajeitado, já é um amigo de Emily de longa data",
                "Masculino",22);

        NPC santoPedro = new NPC("Coordenador geral da organização","Sede da Organização",
                "Santo Pedro ","homem grande, forte, mais velho e que dá uma sensação de caçador",
                "Masculino",54);

        gameManager.startGame();
    }
    else if (menuresponse.equals("créditos")) {
        //metodo para inicializar os créditos

    }
    else {
        System.out.printf("finalizando sistema...");
    }
}