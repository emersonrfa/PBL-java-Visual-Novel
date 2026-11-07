package controller;

import model.Achievement;
import model.Ending;
import repository.EndingRepository;
import repository.UnlockedEndingsRepository;
import service.AchievementService;
import view.AchievementView;
import view.EndingView;

import java.util.List;

public class EndingManager {
    private final EndingRepository endingRepo;
    private final UnlockedEndingsRepository unlockedEndingsRepo;
    private final AchievementService achievementService;
    private final EndingView view = new EndingView();
    private final AchievementView achievementView = new AchievementView();

    /** Recebe as instâncias compartilhadas da aplicação (ver main.AppContext). */
    public EndingManager(EndingRepository endingRepo,
                         UnlockedEndingsRepository unlockedEndingsRepo,
                         AchievementService achievementService) {
        this.endingRepo = endingRepo;
        this.unlockedEndingsRepo = unlockedEndingsRepo;
        this.achievementService = achievementService;
    }

    public boolean isEndingCode(String dialogueId) {
        return dialogueId != null && dialogueId.startsWith(Ending.CODE_PREFIX);
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