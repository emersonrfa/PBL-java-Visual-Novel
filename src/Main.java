import Controller.GameManager;
import Model.NPC;
import Model.Player;
import java.util.Scanner;

void main() {
    Menu menu = new Menu();
    GameManager gameManager = new GameManager();
    Scanner scanner = new Scanner(System.in);

    System.out.printf("inicializando jogo...\n");

    String menuresponse = menu.start();

    if (menuresponse.equals("jogar")) {

        System.out.println("\nQual seu tipo sanguíneo, Emily? (Ex: A+, O-, AB+):");
        System.out.print("> ");
        String tipoSanguineo = scanner.nextLine();

        //  Criando o Player usando o novo construtor limpo

        Player player = new Player("Emily", "Feminino", 22, "aaa", 100);
        player.setBloodType(tipoSanguineo);

        System.out.println("\nTipo sanguíneo [" + player.getBloodType() + "] registrado com sucesso.\n"); // AFIM DE TESTES


        NPC arthur = new NPC("Vizinho e amigo de Emily","Casa de Arthur","Arthur",
                "Cabelo cacheado, bigode fino, moreno. Meio desajeitado, já é um amigo de Emily de longa data",
                "Masculino",22);

        NPC santoPedro = new NPC("Coordenador geral da organização","Sede da Organização",
                "Santo Pedro ","homem grande, forte, mais velho e que dá uma sensação de caçador",
                "Masculino",54);

        // Passa o player recém-criado para o GameManager iniciar o fluxo
        gameManager.startGame(player);
    }
    else if (menuresponse.equals("créditos")) {
        //metodo para inicializar os créditos

    }
    else {
        System.out.printf("finalizando sistema...");
    }
}