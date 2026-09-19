package Controller;

import Model.Ending;
import Repository.EndingRepository;
import java.util.HashMap;
import java.util.Map;

public class EndingManager {
    private EndingRepository endingRepo;

    public EndingManager() {
        this.endingRepo = new EndingRepository();
    }

    // se o jogo for identificar se é o momento de ir para um final pela opção, ele usa esse metodo

    public boolean isEndingCode(String dialogueId) {
        return dialogueId != null && dialogueId.startsWith("FINAL_");
    }

    public void triggerEnding(String endingId) {
        Ending ending = endingRepo.getEnding(endingId);

        if (ending != null) {
            System.out.println("\n==================================================");
            System.out.println("                   FIM DE JOGO                    ");
            System.out.println("==================================================");
            System.out.println(" [" + ending.getTitle() + "] ");
            System.out.println("\n " + ending.getDescription());
            System.out.println("==================================================\n");
        } else {
            System.out.println("\n> AVISO DE DEBUG: id de final não encontrado: " + endingId);
        }
    }
}
