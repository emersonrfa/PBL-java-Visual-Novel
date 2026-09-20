package Controller;

import Model.Ending;
import Repository.EndingRepository;
import View.EndingView;
import java.util.List;

public class EndingManager {
    private EndingRepository endingRepo;
    private EndingView view; // Adicionamos a View

    public EndingManager() {
        this.endingRepo = new EndingRepository();
        this.view = new EndingView(); // Inicializa a View
    }

    // se o jogo for identificar se é o momento de ir para um final pela opção, ele usa esse metodo
    public boolean isEndingCode(String dialogueId) {
        return dialogueId != null && dialogueId.startsWith("FINAL_");
    }

    public void triggerEnding(String endingId) {
        Ending ending = endingRepo.getEnding(endingId);

        if (ending != null) {
            view.printEnding(ending); // Delega para a View
        } else {
            view.printEndingNotFoundWarning(endingId); // Delega para a View
        }
    }

    public List<Ending> getAllEndings() {
        return endingRepo.getAllEndings();
    }
}