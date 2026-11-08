package service;

import model.Achievement;
import model.Ending;
import repository.EndingRepository;
import repository.UnlockedEndingsRepository;

import java.util.ArrayList;
import java.util.List;

/** Regras de finais, sem nenhuma saída de tela: desbloqueia o final e as conquistas ligadas a ele. */
public class EndingService {

    /** Resultado de chegar a um código de final; {@code ending} é nulo se o código não existe. */
    public record EndingOutcome(String endingId, Ending ending, List<Achievement> newAchievements) {
        public boolean found() {
            return ending != null;
        }
    }

    private final EndingRepository endingRepo;
    private final UnlockedEndingsRepository unlockedEndingsRepo;
    private final AchievementService achievementService;

    public EndingService(EndingRepository endingRepo,
                         UnlockedEndingsRepository unlockedEndingsRepo,
                         AchievementService achievementService) {
        this.endingRepo = endingRepo;
        this.unlockedEndingsRepo = unlockedEndingsRepo;
        this.achievementService = achievementService;
    }

    public boolean isEndingCode(String dialogueId) {
        return dialogueId != null && dialogueId.startsWith(Ending.CODE_PREFIX);
    }

    public EndingOutcome reach(String endingId) {
        Ending ending = endingRepo.getEnding(endingId);
        List<Achievement> unlocked = new ArrayList<>();
        if (ending == null) {
            return new EndingOutcome(endingId, null, unlocked);
        }

        unlockedEndingsRepo.unlockEnding(endingId);

        Achievement firstEnd = achievementService.checkAndUnlock("ACH_PRIMEIRO_FIM");
        if (firstEnd != null) unlocked.add(firstEnd);

        if (unlockedEndingsRepo.getUnlockedKeys().size() >= endingRepo.getAllEndings().size()) {
            Achievement allEnds = achievementService.checkAndUnlock("ACH_MESTRE_FALA");
            if (allEnds != null) unlocked.add(allEnds);
        }
        return new EndingOutcome(endingId, ending, unlocked);
    }

    public boolean isUnlocked(Ending ending) {
        return unlockedEndingsRepo.isUnlocked(ending.getId());
    }

    public List<Ending> getAllEndings() {
        return endingRepo.getAllEndings();
    }
}
