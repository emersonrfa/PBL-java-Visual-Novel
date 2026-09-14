import java.util.Scanner;

import Controller.GameManager;
import Model.Player;

public class Menu { 
    String[] options={"Jogar","Creditos","Sair"};
    int selectedoption=0;
    boolean running = true;

    // class used for input handling
    Scanner scanner = new Scanner(System.in);

    public String start() {
        String showmenu_return = "null";
        while (running) {
            showmenu_return = showmenu();
            if (!showmenu_return.equals("null"))
                running=false;
        }
        System.out.format(showmenu_return );
        System.out.println("\n");
        return showmenu_return;

    }

     public String showmenu(){
        System.out.println("\n----MENU INICIAL----");
        System.out.println("W para subir S para Descer e E para confirmar");

        for (int i = 0; i <options.length ; i++) {
            if (i==selectedoption){
                System.out.println("->" + options[i]);
            }
            else {
                System.out.println("  " + options[i]);
            }
        }

        System.out.format("esperando entrada:\n");
        //reads the input and immediately converts to uppercase
        String keyboardinput = scanner.nextLine().toUpperCase();

        if (keyboardinput.equals("W")){
            selectedoption--;
            if(selectedoption<0){
                selectedoption=options.length-1;}//return to the end
        }
        else if (keyboardinput.equals("S")) {
            selectedoption++;
            if (selectedoption>=options.length){
                selectedoption=0;//return to the top
            }
        }
        else if (keyboardinput.equals("E")){
            if(selectedoption==0)
            {
                System.out.printf("iniciando jogo....\n");
                return "jogar";

            }
            else if (selectedoption==1)
            {
                System.out.printf("mostrando creditos...\n");
                System.out.printf("pressione enter para voltar ao menu\n");
                return "creditos";
            }
            else if (selectedoption==2)
            {
                System.out.printf("saindo...\n");
                running=false;
                return "sair";
            }
        }
    return "null";}
}
