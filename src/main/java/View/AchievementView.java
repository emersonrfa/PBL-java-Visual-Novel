package View;

import Model.Achievement;
import Repository.UnlockedAchievementsRepository;

import java.util.List;

public class AchievementView {
    public void printAchievementBanner(Achievement achievement) {
        System.out.println("\n+======================================================+");
        System.out.println("|              CONQUISTA DESBLOQUEADA!                 |");
        System.out.printf("|  » %-49s |\n", achievement.getTitle());
        System.out.println("+======================================================+");
        System.out.printf("  %s\n\n", achievement.getDescription());
    }

    public void displayAchievementsGallery(List<Achievement> allAchievements, UnlockedAchievementsRepository unlockedRepo) {
        System.out.println("\n==========================================================================================================");
        System.out.println("                                          GALERIA DE CONQUISTAS                                           ");
        System.out.println("==========================================================================================================");

        int unlockedCount = 0;
        for (int i = 0; i < allAchievements.size(); i++) {
            Achievement ach = allAchievements.get(i);
            boolean isUnlocked = unlockedRepo.isUnlocked(ach.getId());

            if (isUnlocked) {
                unlockedCount++;
                System.out.printf(" [%2d] %-30s | [DESBLOQUEADA] %s\n", (i + 1), ach.getTitle(), ach.getDescription());
            } else {
                String title = ach.isSecret() ? "?? (Conquista Secreta) ??" : ach.getTitle();
                String desc = ach.isSecret() ? "Jogue para descobrir os critérios desta façanha." : ach.getDescription();
                System.out.printf(" [%2d] %-30s | [BLOQUEADA]    %s\n", (i + 1), title, desc);
            }
        }

        System.out.println("----------------------------------------------------------------------------------------------------------");
        System.out.printf(" Progresso Total: %d de %d conquistas desbloqueadas.\n", unlockedCount, allAchievements.size());
        System.out.println("==========================================================================================================");
        System.out.println(" [Pressione ENTER para voltar ao Menu Principal]");
    }
}
