package Service;

import Model.Achievement;
import Repository.AchievementRepository;
import Repository.UnlockedAchievementsRepository;

import java.util.List;

public class AchievementService {
    private final AchievementRepository achievementRepo;
    private final UnlockedAchievementsRepository unlockedRepo;

    public AchievementService() {
        this.achievementRepo = new AchievementRepository();
        this.unlockedRepo = new UnlockedAchievementsRepository();
    }

    public AchievementService(AchievementRepository achievementRepo,
                              UnlockedAchievementsRepository unlockedRepo) {
        this.achievementRepo = achievementRepo;
        this.unlockedRepo = unlockedRepo;
    }

    /**
     * Tenta desbloquear uma conquista pelo ID.
     * Retorna o objeto Achievement caso seja um desbloqueio inédito, ou null se já estava desbloqueada.
     */
    public synchronized Achievement checkAndUnlock(String achievementId) {
        if (!unlockedRepo.isUnlocked(achievementId)) {
            Achievement achievement = achievementRepo.getAchievement(achievementId);
            if (achievement != null && unlockedRepo.unlock(achievementId)) {
                return achievement;
            }
        }
        return null;
    }

    public Achievement evaluateFlagTrigger(String flagName) {
        if ("JANELAARTHUR".equalsIgnoreCase(flagName)) {
            return checkAndUnlock("ACH_CURIOSA");
        }
        return null;
    }

    public Achievement evaluateDialogueTrigger(String dialogueId) {
        if ("dFIM.2".equals(dialogueId)) {
            return checkAndUnlock("ACH_PRIMEIROS_PASSOS");
        } else if ("d2.8".equals(dialogueId)) {
            return checkAndUnlock("ACH_BOM_DIA");
        }
        return null;
    }

    public Achievement evaluateAffinityTrigger(String npcName, int currentAffinity) {
        if ("Arthur".equalsIgnoreCase(npcName) && currentAffinity >= 10) {
            return checkAndUnlock("ACH_AMIGO_VELHO");
        }
        return null;
    }

    public List<Achievement> getAllAchievements() {
        return achievementRepo.getAllAchievements();
    }

    public UnlockedAchievementsRepository getUnlockedRepo() {
        return unlockedRepo;
    }
}