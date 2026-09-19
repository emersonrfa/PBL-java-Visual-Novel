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

    // para verificar se o tipo de sangue de emily existe

    public String readBloodType() {
        String[] validTypes = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

        while (true) {
            String input = scanner.nextLine().trim().toUpperCase();

            for (String type : validTypes) {
                if (type.equals(input)) {
                    return input;
                }
            }
            System.out.println("Não, diga-me um sangue que realmente exista. (Ex: A+, O-, AB+, B-):");
            System.out.print("> ");
        }
    }

    public void close() {
        scanner.close();
    }

}
