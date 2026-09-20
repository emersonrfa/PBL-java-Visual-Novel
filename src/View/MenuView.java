package View;

import java.util.List;

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

    public void displayChapterSelection() {
        System.out.println("\n==================================================");
        System.out.println("               SELEÇÃO DE CAPÍTULO                ");
        System.out.println("==================================================");
        System.out.println(" 1 - Capítulo 1: O Prólogo");
        System.out.println(" 2 - Capítulo 2: A entrevista de emprego");
        System.out.println(" 11 - Galeria de Finais");
        System.out.println(" 0 - Voltar / Sair");
        System.out.println("==================================================");
    }

    public void displayEndingSelection(List<String> endingTitles) {
        System.out.println("\n==================================================");
        System.out.println("                 GALERIA DE FINAIS                ");
        System.out.println("==================================================");
        for (int i = 0; i < endingTitles.size(); i++) {
            System.out.println(" " + (i + 1) + " - " + endingTitles.get(i));
        }
        System.out.println(" 0 - Voltar para Seleção de Capítulos");
        System.out.println("==================================================");
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
        System.out.println("\nFeito por: \nEmerson Rodrigues Farias de Araújo\nDaniel Fontes Aguiar\n\nProjeto" +
                " PBL da matéria de MI - Algoritmos de Programação II.");
        System.out.println("pressione ENTER para voltar ao menu");
    }
}