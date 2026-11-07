package tests;

import exception.GamePersistenceException;
import model.GameStates;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import repository.StateRepository;

import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/** Regressão do B2: arquivar o autosave várias vezes não pode encher os slots com cópias. */
public class AutoSaveArchiveTest {
    @TempDir
    Path tempDir;

    private GameStates stateAt(String dialogId, String timestamp) {
        GameStates state = new GameStates();
        state.setCurrentChapterId(1);
        state.setCurrentDialogId(dialogId);
        state.setSaveTimestamp(timestamp);
        return state;
    }

    @Test
    public void archivingTheSameAutoSaveTwiceDoesNotDuplicateIt() throws GamePersistenceException {
        StateRepository repo = new StateRepository(tempDir.toString());
        repo.saveStateToSlotChecked(stateAt("d1.3", "07/11/2026 10:00:00"), 0);

        assertEquals(1, repo.archiveAutoSaveToEmptySlot());
        assertEquals(-1, repo.archiveAutoSaveToEmptySlot());
        assertEquals(-1, repo.archiveAutoSaveToEmptySlot());

        assertTrue(repo.slotExists(1));
        assertFalse(repo.slotExists(2));
    }

    @Test
    public void newProgressIsArchivedInTheNextFreeSlot() throws GamePersistenceException {
        StateRepository repo = new StateRepository(tempDir.toString());
        repo.saveStateToSlotChecked(stateAt("d1.3", "07/11/2026 10:00:00"), 0);
        assertEquals(1, repo.archiveAutoSaveToEmptySlot());

        repo.saveStateToSlotChecked(stateAt("d2.1", "07/11/2026 10:30:00"), 0);

        assertEquals(2, repo.archiveAutoSaveToEmptySlot());
        assertEquals(-1, repo.archiveAutoSaveToEmptySlot());
        assertFalse(repo.slotExists(3));
    }

    @Test
    public void withoutAutoSaveThereIsNothingToArchive() {
        StateRepository repo = new StateRepository(tempDir.toString());

        assertEquals(-1, repo.archiveAutoSaveToEmptySlot());
        assertFalse(repo.slotExists(1));
    }
}
