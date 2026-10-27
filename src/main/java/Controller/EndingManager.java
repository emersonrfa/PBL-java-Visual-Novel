package Controller;

import Model.Achievement;
import Model.Ending;
import Repository.EndingRepository;
import Repository.UnlockedEndingsRepository;
import Service.AchievementService;
import View.AchievementView;
import View.EndingView;

import java.util.List;

public class EndingManager {
    private EndingRepository endingRepo;
    private UnlockedEndingsRepository unlockedEndingsRepo;
    private EndingView view;
    private AchievementService achievementService;
    private AchievementView achievementView;

    public EndingManager() {
        this.endingRepo = new EndingRepository();
        this.unlockedEndingsRepo = new UnlockedEndingsRepository();
        this.view = new EndingView();
        this.achievementService = new AchievementService();
        this.achievementView = new AchievementView();
    }

    public boolean isEndingCode(String dialogueId) {
        return dialogueId != null && dialogueId.startsWith("FINAL_");
    }

    public void triggerEnding(String endingId) {
        Ending ending = endingRepo.getEnding(endingId);

        if (ending != null) {
            unlockedEndingsRepo.unlockEnding(endingId);
            view.printEnding(ending);

            Achievement firstEnd = achievementService.checkAndUnlock("ACH_PRIMEIRO_FIM");
            if (firstEnd != null) {
                achievementView.printAchievementBanner(firstEnd);
            }

            if (unlockedEndingsRepo.getUnlockedKeys().size() >= endingRepo.getAllEndings().size()) {
                Achievement allEnds = achievementService.checkAndUnlock("ACH_MESTRE_FALA");
                if (allEnds != null) {
                    achievementView.printAchievementBanner(allEnds);
                }
            }
        } else {
            view.printEndingNotFoundWarning(endingId);
        }
    }

    public List<Ending> getAllEndings() {
        return endingRepo.getAllEndings();
    }
}