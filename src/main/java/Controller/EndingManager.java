package Controller;

import Model.Ending;
import Repository.EndingRepository;
import Repository.UnlockedEndingsRepository;
import View.EndingView;

import java.util.List;

public class EndingManager {
    private EndingRepository endingRepo;
    private UnlockedEndingsRepository unlockedEndingsRepo;
    private Service.AchievementService achievementService = new Service.AchievementService();
    private EndingView view;

    public EndingManager() {
        this.endingRepo = new EndingRepository();
        this.unlockedEndingsRepo = new UnlockedEndingsRepository();
        this.view = new EndingView();
    }

    public EndingManager(EndingRepository endingRepo, UnlockedEndingsRepository unlockedEndingsRepo, EndingView view) {
        this.endingRepo = endingRepo;
        this.unlockedEndingsRepo = unlockedEndingsRepo;
        this.view = view;
    }

    /**
     * Verifica se o identificador recebido representa o gatilho de um final.
     */
    public boolean isEndingCode(String dialogueId) {
        return dialogueId != null && dialogueId.startsWith("FINAL_");
    }

    /**
     * Aciona o final correspondente, persiste o desbloqueio no arquivo JSON global e exibe a tela de desfecho.
     */
    public void triggerEnding(String endingId) {
        Ending ending = endingRepo.getEnding(endingId);

        if (ending != null) {
            unlockedEndingsRepo.unlockEnding(endingId);
            view.printEnding(ending);

            // Disparo das conquistas de término
            achievementService.checkAndUnlock("ACH_PRIMEIRO_FIM");
            if (unlockedEndingsRepo.getUnlockedKeys().size() >= endingRepo.getAllEndings().size()) {
                achievementService.checkAndUnlock("ACH_MESTRE_FALA");
            }
        } else {
            view.printEndingNotFoundWarning(endingId);
        }
    }

    public List<Ending> getAllEndings() {
        return endingRepo.getAllEndings();
    }

    public EndingRepository getEndingRepo() {
        return endingRepo;
    }

    public UnlockedEndingsRepository getUnlockedEndingsRepo() {
        return unlockedEndingsRepo;
    }
}