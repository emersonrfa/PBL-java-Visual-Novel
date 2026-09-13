//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
void main() {
    Menu menu= new Menu();//creates a new inatance
    System.out.printf("inicializando jogo...\n");

    String menuresponse  =menu.start();//calls the method start
    if (menuresponse == "jogar"){
        //metodo para inicializar jogo
    } else if (menuresponse == "creditos") {
        //metodo para inicializar os criditos

    }
    else {
        System.out.printf("finalizando sistema...");
    }


}
