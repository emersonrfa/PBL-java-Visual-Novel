package View;

//classe responsavel por fazer os prints do menu
public class MenuView {

    public void printMenu(String[] options, int selectedOption) {
        System.out.println("\n----MENU INICIAL----");
        System.out.println("W para subir, S para descer e E para confirmar");

        for (int i = 0; i < options.length; i++) {
            if (i == selectedOption) {
                System.out.println("->" + options[i]);
            } else {
                System.out.println("  " + options[i]);
            }
        }
        System.out.print("Esperando entrada:\n> ");
    }

    public void printMessage(String message) {
        System.out.println(message);
    }

    public void printBloodTypePrompt() {
        System.out.println("\nQual seu tipo sanguíneo, Emily? (Ex: A+, O-, AB+):");
        System.out.print("> ");
    }

    public void printCredits() {
        System.out.println("\nmostrando creditos...");
        System.out.println("Desenvolvido para Algoritmos e Programação II.");
        System.out.println("pressione ENTER para voltar ao menu");
    }
}