package tests;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import repository.GamePaths;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class GamePathsTest {
    @TempDir
    Path base;

    private void write(String relative, String content) throws IOException {
        Path file = base.resolve(relative);
        Files.createDirectories(file.getParent());
        Files.writeString(file, content);
    }

    @Test
    public void legacyFilesMoveToDataAndSavesKeepOnlySaves() throws IOException {
        write("config.json", "cfg");
        write("saves/unlocked_achievements.json", "ach");
        write("saves/unlocked_endings.json", "end");
        write("saves/save_1.json", "slot");

        GamePaths.migrateLegacyFiles(base.toString());

        assertEquals("cfg", Files.readString(base.resolve("data/config.json")));
        assertEquals("ach", Files.readString(base.resolve("data/unlocked_achievements.json")));
        assertEquals("end", Files.readString(base.resolve("data/unlocked_endings.json")));
        assertFalse(Files.exists(base.resolve("config.json")));
        assertFalse(Files.exists(base.resolve("saves/unlocked_endings.json")));
        assertTrue(Files.exists(base.resolve("saves/save_1.json")));
    }

    @Test
    public void migrationNeverOverwritesAndIsRepeatable() throws IOException {
        write("config.json", "velho");
        write("data/config.json", "novo");

        GamePaths.migrateLegacyFiles(base.toString());
        GamePaths.migrateLegacyFiles(base.toString());

        assertEquals("novo", Files.readString(base.resolve("data/config.json")));
        assertTrue(Files.exists(base.resolve("config.json")));
    }

    @Test
    public void migrationWithNothingToMoveIsHarmless() {
        GamePaths.migrateLegacyFiles(base.toString());

        assertFalse(Files.exists(base.resolve("data")));
    }
}
