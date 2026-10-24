package Service;

import Model.Achievement;
import Repository.AchievementRepository;
import Repository.UnlockedAchievementsRepository;
import View.AchievementView;

import java.util.List;

public class AchievementService {
    private final AchievementRepository achievementRepo;
    private final UnlockedAchievementsRepository unlockedRepo;
    private final AchievementView view;

    public AchievementService() {
        this.achievementRepo = new AchievementRepository();
        this.unlockedRepo = new UnlockedAchievementsRepository();
        this.view = new AchievementView();
    }

    public AchievementService(AchievementRepository achievementRepo,
                              UnlockedAchievementsRepository unlockedRepo,
                              AchievementView view) {
        this.achievementRepo = achievementRepo;
        this.unlockedRepo = unlockedRepo;
        this.view = view;
    }

    /**
     * Tenta desbloquear uma conquista pelo ID. Exibe o aviso em consola apenas na primeira vez.
     */
    public synchronized boolean checkAndUnlock(String achievementId) {
        if (!unlockedRepo.isUnlocked(achievementId)) {
            Achievement achievement = achievementRepo.getAchievement(achievementId);
            if (achievement != null && unlockedRepo.unlock(achievementId)) {
                view.printAchievementBanner(achievement);
                return true;
            }
        }
        return false;
    }

    /**
     * Valida gatilhos baseados em flags registradas no jogador.
     */
    public void evaluateFlagTrigger(String flagName) {
        if ("JANELAARTHUR".equalsIgnoreCase(flagName)) {
            checkAndUnlock("ACH_CURIOSA");
        }
    }

    /**
     * Valida gatilhos baseados em avanço de capítulos ou diálogos.
     */
    public void evaluateDialogueTrigger(String dialogueId) {
        if ("dFIM.2".equals(dialogueId)) {
            checkAndUnlock("ACH_PRIMEIROS_PASSOS");
        } else if ("d2.8".equals(dialogueId)) {
            checkAndUnlock("ACH_BOM_DIA");
        }
    }

    /**
     * Valida gatilhos disparados em respostas com alteração de afinidade.
     */
    public void evaluateAffinityTrigger(String npcName, int currentAffinity) {
        if ("Arthur".equalsIgnoreCase(npcName) && currentAffinity >= 10) {
            checkAndUnlock("ACH_AMIGO_VELHO");
        }
    }

    public List<Achievement> getAllAchievements() {
        return achievementRepo.getAllAchievements();
    }

    public UnlockedAchievementsRepository getUnlockedRepo() {
        return unlockedRepo;
    }

    public AchievementView getView() {
        return view;
    }
}
