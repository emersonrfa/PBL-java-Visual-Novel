import Exception.CorruptedSaveException;
import Exception.GamePersistenceException;
import Exception.SaveSlotNotFoundException;
import Model.GameStates;
import Model.Item;
import Model.Player;
import Repository.ItemRepository;
import Repository.StateRepository;
import Service.AffinityTracker;
import Service.PlayerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

public class PersistenceTest {

    @TempDir
    Path tempDir; // Diretório temporário isolado gerido pelo JUnit 5

    private StateRepository stateRepo;
    private PlayerService playerService;
    private ItemRepository itemRepo;

    @BeforeEach
    void setUp() {
        stateRepo = new StateRepository(tempDir.toString());
        playerService = new PlayerService();
        itemRepo = new ItemRepository();
    }

    @Test
    void testSaveAndLoadCompleteStateWithAffinitiesAndInventory() throws GamePersistenceException {
        Player player = new Player("O-");
        player.setEnergy(75);
        player.setMoney(55.50);
        player.setFlag("JANELAARTHUR", true);
        player.getInventory().addItem(itemRepo.getItem("Chave de Casa"));

        AffinityTracker tracker = new AffinityTracker();
        tracker.modifyAffinity("Arthur", 25);
        tracker.modifySuspicion("Miguel", 40);

        GameStates stateToSave = new GameStates();
        stateToSave.setSaveName("Partida Teste");
        stateToSave.setCurrentChapterId(2);
        stateToSave.setCurrentSceneId("No Mural da Rua");
        stateToSave.setCurrentDialogId("d4.3");
        playerService.populateStateFromPlayer(stateToSave, player, tracker);

        // Salva no Slot 1
        stateRepo.saveStateToSlotChecked(stateToSave, 1);

        // Carrega do Slot 1
        GameStates loadedState = stateRepo.loadStateFromSlotChecked(1);
        Player restoredPlayer = playerService.createPlayerFromState(loadedState, itemRepo);
        AffinityTracker restoredTracker = new AffinityTracker();
        playerService.restoreAffinityFromState(loadedState, restoredTracker);

        assertEquals(2, loadedState.getCurrentChapterId());
        assertEquals("d4.3", loadedState.getCurrentDialogId());
        assertEquals("O-", restoredPlayer.getBloodType());
        assertEquals(75, restoredPlayer.getEnergy());
        assertEquals(55.50, restoredPlayer.getMoney());
        assertTrue(restoredPlayer.getFlag("JANELAARTHUR"));
        assertTrue(restoredPlayer.getInventory().hasItem("Chave de Casa"));
        assertEquals(25, restoredTracker.getAffinity("Arthur"));
        assertEquals(40, restoredTracker.getSuspicion("Miguel"));
    }

    @Test
    void testSlotIndependence() throws GamePersistenceException {
        GameStates slot1State = new GameStates();
        slot1State.setCurrentChapterId(1);
        slot1State.setPlayerBloodType("A+");

        GameStates slot2State = new GameStates();
        slot2State.setCurrentChapterId(2);
        slot2State.setPlayerBloodType("AB-");

        stateRepo.saveStateToSlotChecked(slot1State, 1);
        stateRepo.saveStateToSlotChecked(slot2State, 2);

        GameStates loaded1 = stateRepo.loadStateFromSlotChecked(1);
        GameStates loaded2 = stateRepo.loadStateFromSlotChecked(2);

        assertEquals(1, loaded1.getCurrentChapterId());
        assertEquals("A+", loaded1.getPlayerBloodType());
        assertEquals(2, loaded2.getCurrentChapterId());
        assertEquals("AB-", loaded2.getPlayerBloodType());
    }

    @Test
    void testLoadMissingSlotThrowsSaveSlotNotFoundException() {
        assertThrows(SaveSlotNotFoundException.class, () -> {
            stateRepo.loadStateFromSlotChecked(3);
        });
    }

    @Test
    void testLoadCorruptedJsonThrowsCorruptedSaveException() throws IOException {
        File corruptedFile = tempDir.resolve("save_1.json").toFile();
        try (FileWriter writer = new FileWriter(corruptedFile)) {
            writer.write("{ json_invalido_corrompido: [[[ ");
        }

        assertThrows(CorruptedSaveException.class, () -> {
            stateRepo.loadStateFromSlotChecked(1);
        });
    }

    @Test
    void testInvalidDataThrowsCorruptedSaveExceptionAndPreservesExistingValidSave() throws GamePersistenceException {
        // 1. Grava um save válido no Slot 2
        GameStates validState = new GameStates();
        validState.setPlayerEnergy(80);
        stateRepo.saveStateToSlotChecked(validState, 2);

        // 2. Tenta sobrescrever o Slot 2 com dados inválidos (energia 999)
        GameStates invalidState = new GameStates();
        invalidState.setPlayerEnergy(999);

        assertThrows(CorruptedSaveException.class, () -> {
            stateRepo.saveStateToSlotChecked(invalidState, 2);
        });

        // 3. Comprova que o save válido anterior foi preservado intacto
        GameStates preservedState = stateRepo.loadStateFromSlotChecked(2);
        assertEquals(80, preservedState.getPlayerEnergy());
    }
}