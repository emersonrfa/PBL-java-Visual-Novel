package View;

import Model.Ending;

public class EndingView {

    public void printEnding(Ending ending) {
        System.out.println("\n==================================================");
        System.out.println("                   FIM DE JOGO                    ");
        System.out.println("==================================================");
        System.out.println(" [" + ending.getTitle() + "] ");
        System.out.println("\n " + ending.getDescription());
        System.out.println("==================================================\n");
    }

    public void printEndingNotFoundWarning(String endingId) {
        System.out.println("\n> AVISO DE DEBUG: id de final não encontrado: " + endingId);
    }
}