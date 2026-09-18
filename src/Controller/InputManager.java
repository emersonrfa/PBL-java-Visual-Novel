package Controller;

import java.util.Scanner;

public class InputManager {
    private Scanner scanner;

    public InputManager() {
        this.scanner = new Scanner(System.in);
    }

    // pra avançar dialogo
    public void waitEnter() {
        scanner.nextLine();
    }

    // pra ler textos
    public String readString() {
        return scanner.nextLine();
    }

    // pra ler a escolha em um dialogo
    public int readValidChoice(int min, int max) {
        int choice = -1;
        while (choice < min || choice > max) {
            System.out.print("\nSua escolha: ");
            try {
                choice = Integer.parseInt(scanner.nextLine()); // parseInt transforma texto em numero
                if (choice < min || choice > max) {
                    System.out.println("Opção inválida, escolha uma opção entre " + min + " e " + max + ".");
                    }
                } catch (NumberFormatException error) {
                System.out.println("Digite apenas números");
            }
        }
        return choice;
    }

    public void clode() {
        scanner.close();
    }

}
