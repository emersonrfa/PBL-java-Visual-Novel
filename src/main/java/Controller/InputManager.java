package Controller;

import View.InputView;
import java.util.Scanner;

public class InputManager {
    private Scanner scanner;
    private InputView view; // Adicionamos a View aqui

    public InputManager() {
        this.scanner = new Scanner(System.in);
        this.view = new InputView(); // Inicializa a View
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
            view.printChoicePrompt(); // Delega para a View
            try {
                choice = Integer.parseInt(scanner.nextLine());
                if (choice < min || choice > max) {
                    view.printInvalidChoice(min, max); // Delega para a View
                }
            } catch (NumberFormatException error) {
                view.printNumberFormatError(); // Delega para a View
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
            view.printInvalidBloodType(); // Delega o erro para a View
        }
    }

    public String waitEnterOrCommand() {
        System.out.println("\n[Pressione ENTER para continuar ou 'S' para salvar]");
        System.out.print("> ");
        String line = scanner.nextLine().trim();
        return line.toUpperCase();
    }

    public void close() {
        scanner.close();
    }
}