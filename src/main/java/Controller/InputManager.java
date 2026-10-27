package Controller;

import View.InputView;

import java.io.IOException;
import java.util.Scanner;

public class InputManager {
    private Scanner scanner;
    private InputView view;

    public InputManager() {
        this.scanner = new Scanner(System.in);
        this.view = new InputView();
    }

    public static void clear() {
        try {
            String os = System.getProperty("os.name");
            if (os.contains("Windows")) {
                new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor();
            } else {
                Runtime.getRuntime().exec("clear");
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void waitEnter() {
        clear();
        scanner.nextLine();
    }

    public String readString() {
        clear();
        return scanner.nextLine();
    }

    public int readValidChoice(int min, int max) {
        int choice = -1;
        while (choice < min || choice > max) {
            view.printChoicePrompt();
            try {
                choice = Integer.parseInt(scanner.nextLine().trim());
                if (choice < min || choice > max) {
                    view.printInvalidChoice(min, max);
                }
            } catch (NumberFormatException error) {
                view.printNumberFormatError();
            }
        }
        return choice;
    }

    public int readChoiceOrCommand(int min, int max) {
        while (true) {
            System.out.print("\nSua escolha (ou digite 'S' para salvar / 'MENU' para voltar ao menu): ");
            String input = scanner.nextLine().trim();

            if (input.equalsIgnoreCase("MENU")) {
                return -99;
            }
            if (input.equalsIgnoreCase("S")) {
                return -98;
            }

            try {
                int choice = Integer.parseInt(input);
                if (choice >= min && choice <= max) {
                    return choice;
                }
                view.printInvalidChoice(min, max);
            } catch (NumberFormatException error) {
                view.printNumberFormatError();
            }
        }
    }

    public String readBloodType() {
        String[] validTypes = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};

        while (true) {
            String input = scanner.nextLine().trim().toUpperCase();

            for (String type : validTypes) {
                if (type.equals(input)) {
                    return input;
                }
            }
            view.printInvalidBloodType();
        }
    }

    public String waitEnterOrCommand() {
        System.out.println("\n[Pressione ENTER para continuar, 'S' para salvar ou 'MENU' para voltar ao menu]");
        System.out.print("> ");
        String line = scanner.nextLine().trim();
        return line.toUpperCase();
    }

    public void close() {
        scanner.close();
    }
}