package View;

public class InputView {

    public void printInvalidChoice(int min, int max) {
        System.out.println("Opção inválida, escolha uma opção entre " + min + " e " + max + ".");
    }

    public void printNumberFormatError() {
        System.out.println("Digite apenas números.");
    }

    public void printInvalidBloodType() {
        System.out.println("Não, diga-me um sangue que realmente exista. (Ex: A+, O-, AB+, B-):");
        System.out.print("> ");
    }

    public void printChoicePrompt() {
        System.out.print("\nSua escolha: ");
    }
}