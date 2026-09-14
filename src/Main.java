import Controller.GameManager;
import Model.NPC;
import Model.Player;
import java.util.Scanner;

void main() {
    Menu menu= new Menu();//creates a new inatance
    GameManager gameManager = new GameManager();
    Scanner scanner = new Scanner(System.in);

    System.out.printf("inicializando jogo...\n");

    String menuresponse  =menu.start();//calls the method start
    if (menuresponse == "jogar"){
        // create the player object
        Player player = new Player(scanner);
        NPC arthur = new NPC("Arthur vizinho de Emily","local inicial","Arthur","Cabelo cacheado, bigode fino, moreno. Meio desajeitado, já é um amigo de Emily de longa data","Masculino",22);
        NPC santoPedro = new NPC("coordenador geral da organização","area inicial","Santo Pedro ","homem grande, forte, mais velho e que dá uma sensação de caçador","Masculino",54);



    }
    else if (menuresponse == "creditos") {
        //metodo para inicializar os criditos

    }
    else {
        System.out.printf("finalizando sistema...");
    }


}
