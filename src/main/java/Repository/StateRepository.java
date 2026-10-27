package Repository;

import Exception.CorruptedSaveException;
import Exception.GamePersistenceException;
import Exception.SaveSlotNotFoundException;
import Model.GameStates;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

public class StateRepository {
    private static final List<String> VALID_BLOOD_TYPES = Arrays.asList(
            "A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"
    );

    private final String savesDir;
    private final ObjectMapper mapper;

    public StateRepository() {
        this("saves");
    }

    public StateRepository(String savesDir) {
        this.savesDir = savesDir;
        this.mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        ensureDirectoryExists();
    }

    private void ensureDirectoryExists() {
        File dir = new File(savesDir);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private String getFilePathForSlot(int slot) {
        if (slot == 0) {
            return savesDir + File.separator + "autosave.json";
        }
        return savesDir + File.separator + "save_" + slot + ".json";
    }

    public boolean slotExists(int slot) {
        File file = new File(getFilePathForSlot(slot));
        return file.exists() && file.isFile() && file.length() > 0;
    }

    public void validateState(GameStates state) throws CorruptedSaveException {
        if (state == null) {
            throw new CorruptedSaveException("Os dados do save estão nulos ou vazios.");
        }
        if (state.getCurrentChapterId() <= 0) {
            throw new CorruptedSaveException("ID de capítulo inválido no save: " + state.getCurrentChapterId());
        }
        if (state.getCurrentDialogId() == null || state.getCurrentDialogId().trim().isEmpty()) {
            throw new CorruptedSaveException("ID de diálogo ausente ou inválido no save.");
        }
        if (state.getPlayerEnergy() < 0 || state.getPlayerEnergy() > 100) {
            throw new CorruptedSaveException("Valor de energia corrompido fora do intervalo [0-100]: " + state.getPlayerEnergy());
        }
        if (state.getPlayerMoney() < 0) {
            throw new CorruptedSaveException("Saldo financeiro negativo inválido no save: " + state.getPlayerMoney());
        }
        if (state.getPlayerBloodType() == null || !VALID_BLOOD_TYPES.contains(state.getPlayerBloodType().toUpperCase())) {
            throw new CorruptedSaveException("Tipo sanguíneo inválido ou corrompido no save: " + state.getPlayerBloodType());
        }
    }

    public GameStates loadStateFromSlotChecked(int slot) throws GamePersistenceException {
        File file = new File(getFilePathForSlot(slot));
        if (!file.exists() || file.length() == 0) {
            throw new SaveSlotNotFoundException(slot);
        }

        try {
            GameStates state = mapper.readValue(file, GameStates.class);
            validateState(state);
            return state;
        } catch (JsonProcessingException e) {
            throw new CorruptedSaveException("O arquivo do Slot " + slot + " está com formato JSON corrompido.", e);
        } catch (IOException e) {
            throw new GamePersistenceException("Falha de E/S ao ler o arquivo do Slot " + slot + ".", e);
        }
    }

    public GameStates loadStateFromSlot(int slot) {
        try {
            return loadStateFromSlotChecked(slot);
        } catch (GamePersistenceException e) {
            return null;
        }
    }

    public boolean isSlotCorrupted(int slot) {
        if (!slotExists(slot)) {
            return false;
        }
        try {
            loadStateFromSlotChecked(slot);
            return false;
        } catch (GamePersistenceException e) {
            return true;
        }
    }

    public void saveStateToSlotChecked(GameStates state, int slot) throws GamePersistenceException {
        if (slot < 0 || slot > 4) {
            throw new GamePersistenceException("Número de slot inválido: " + slot + ". Use slots de 0 a 4.");
        }
        validateState(state);
        ensureDirectoryExists();

        File targetFile = new File(getFilePathForSlot(slot));
        File tempFile = new File(getFilePathForSlot(slot) + ".tmp");

        try {
            mapper.writeValue(tempFile, state);
            Files.move(tempFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            if (tempFile.exists()) {
                tempFile.delete();
            }
            throw new GamePersistenceException("Erro ao gravar o save no Slot " + slot + ". O save anterior foi preservado.", e);
        }
    }

    public boolean saveStateToSlot(GameStates state, int slot) {
        try {
            saveStateToSlotChecked(state, slot);
            return true;
        } catch (GamePersistenceException e) {
            System.err.println("[Erro de Persistência] " + e.getMessage());
            return false;
        }
    }

    public void deleteSlotChecked(int slot) throws GamePersistenceException {
        File file = new File(getFilePathForSlot(slot));
        if (!file.exists() || !file.isFile()) {
            throw new SaveSlotNotFoundException(slot);
        }
        try {
            Files.delete(file.toPath());
        } catch (IOException e) {
            throw new GamePersistenceException("Não foi possível excluir o arquivo do Slot " + slot + ".", e);
        }
    }

    public boolean deleteSlot(int slot) {
        try {
            deleteSlotChecked(slot);
            return true;
        } catch (GamePersistenceException e) {
            return false;
        }
    }

    public int findFirstAvailableManualSlot() {
        for (int slot = 1; slot <= 4; slot++) {
            if (!slotExists(slot)) {
                return slot;
            }
        }
        return -1;
    }

    public int archiveAutoSaveToEmptySlot() {
        if (!slotExists(0) || isSlotCorrupted(0)) {
            return -1;
        }

        int targetSlot = findFirstAvailableManualSlot();
        if (targetSlot != -1) {
            GameStates autoSaveState = loadStateFromSlot(0);
            if (autoSaveState != null && saveStateToSlot(autoSaveState, targetSlot)) {
                return targetSlot;
            }
        }
        return -1;
    }

    public GameStates getCurrentStates() {
        GameStates state = loadStateFromSlot(0);
        if (state == null) {
            state = new GameStates();
        }
        return state;
    }

    public void saveState(GameStates state) {
        saveStateToSlot(state, 0);
    }
}