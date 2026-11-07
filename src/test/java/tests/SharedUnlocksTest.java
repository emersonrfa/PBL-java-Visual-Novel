package tests;

import controller.EndingManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import repository.AchievementRepository;
import repository.EndingRepository;
import repository.UnlockedAchievementsRepository;
import repository.UnlockedEndingsRepository;
import service.AchievementService;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regressão do B1: quando o EndingManager e o resto do jogo usam o MESMO AchievementService,
 * uma conquista não apaga a outra no arquivo.
 */
public class SharedUnlocksTest {
    @TempDir
    Path tempDir;

    @Test
    public void achievementsUnlockedByDialogueAndByEndingAreBothPersisted() {
        String dir = tempDir.toString();
        UnlockedAchievementsRepository unlocked = new UnlockedAchievementsRepository(dir);
        AchievementService achievements = new AchievementService(new AchievementRepository(), unlocked);
        EndingManager endings = new EndingManager(
                new EndingRepository(), new UnlockedEndingsRepository(dir), achievements);

        assertNotNull(achievements.evaluateFlagTrigger("JANELAARTHUR")); // ACH_CURIOSA
        endings.triggerEnding("FINAL_VAMPIRA");                          // ACH_PRIMEIRO_FIM

        UnlockedAchievementsRepository reloaded = new UnlockedAchievementsRepository(dir);
        assertTrue(reloaded.isUnlocked("ACH_CURIOSA"));
        assertTrue(reloaded.isUnlocked("ACH_PRIMEIRO_FIM"));
        assertTrue(new UnlockedEndingsRepository(dir).getUnlockedKeys().contains("FINAL_VAMPIRA"));
    }
}
