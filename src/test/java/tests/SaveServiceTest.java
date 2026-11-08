package tests;

import model.GameStates;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import repository.ItemRepository;
import repository.StateRepository;
import service.PlayerService;
import service.SaveService;
import service.SaveService.LoadedGame;
import service.SaveService.SlotSummary;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SaveServiceTest {
    @TempDir
    Path tempDir;

    private StateRepository repository;
    private SaveService service;

    @BeforeEach
    public void setUp() {
        repository = new StateRepository(tempDir.resolve("saves").toString());
        service = new SaveService(repository, new PlayerService(), new ItemRepository());
    }

    @Test
    public void summariesListSlotsWithOrWithoutAutosave() {
        assertEquals(4, service.summaries(false).size());
        List<SlotSummary> all = service.summaries(true);
        assertEquals(5, all.size());
        assertEquals(0, all.get(0).slot());
        assertTrue(all.stream().allMatch(SlotSummary::empty));
        assertTrue(all.get(1).text().contains("vazio"));
    }

    @Test
    public void newGameWritesSlotAndAutosaveAtChapterOne() {
        LoadedGame game = service.newGame("B-", 2);

        assertEquals("B-", game.player().getBloodType());
        assertEquals(1, game.state().getCurrentChapterId());
        assertEquals("d1.1", game.state().getCurrentDialogId());
        assertFalse(service.summary(2).empty());
        assertTrue(service.summary(2).loadable());
        assertTrue(service.summary(2).text().contains("Cap. 1"));
        assertFalse(service.summary(SaveService.AUTOSAVE_SLOT).empty());
        assertTrue(service.summary(1).empty());
    }

    @Test
    public void newGameRejectsInvalidSlot() {
        assertThrows(IllegalArgumentException.class, () -> service.newGame("A+", 0));
        assertThrows(IllegalArgumentException.class, () -> service.newGame("A+", 5));
    }

    @Test
    public void loadRestoresPlayerAndCopiesToAutosave() {
        service.newGame("O-", 3);
        GameStates slot = repository.loadStateFromSlot(3);
        slot.setCurrentDialogId("d1.5");
        slot.setPlayerEnergy(40);
        repository.saveStateToSlot(slot, 3);

        LoadedGame loaded = service.load(3);

        assertEquals("O-", loaded.player().getBloodType());
        assertEquals(40, loaded.player().getEnergy());
        assertEquals("d1.5", loaded.state().getCurrentDialogId());
        assertEquals("d1.5", repository.getCurrentStates().getCurrentDialogId());
    }

    @Test
    public void loadingAnEmptySlotFails() {
        assertThrows(IllegalStateException.class, () -> service.load(4));
    }

    @Test
    public void deleteOnlyAppliesToManualSlots() {
        service.newGame("A+", 1);

        assertFalse(service.delete(SaveService.AUTOSAVE_SLOT));
        assertTrue(service.delete(1));
        assertTrue(service.summary(1).empty());
    }
}
