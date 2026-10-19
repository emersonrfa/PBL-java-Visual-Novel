package Repository;

import Model.GameStates;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;

public class StateRepository {
    private static final String SAVES_DIR = "saves";
    private static final String AUTOSAVE_FILE = SAVES_DIR + File.separator + "autosave.json";

    private ObjectMapper mapper;

    public StateRepository() {
        this.mapper = new ObjectMapper();
        this.mapper.enable(SerializationFeature.INDENT_OUTPUT);
        ensureDirectoryExists();
    }

    private void ensureDirectoryExists() {
        File dir = new File(SAVES_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    private String getFilePathForSlot(int slot) {
        if (slot == 0) {
            return AUTOSAVE_FILE;
        }
        return SAVES_DIR + File.separator + "save_" + slot + ".json";
    }

    public boolean slotExists(int slot) {
        File file = new File(getFilePathForSlot(slot));
        return file.exists() && file.length() > 0;
    }

    public GameStates loadStateFromSlot(int slot) {
        File file = new File(getFilePathForSlot(slot));
        if (!file.exists()) {
            return null;
        }

        try {
            return mapper.readValue(file, GameStates.class);
        } catch (IOException e) {
            System.err.println("Erro ao carregar save do slot " + slot + ": " + e.getMessage());
            return null;
        }
    }

    public boolean saveStateToSlot(GameStates state, int slot) {
        ensureDirectoryExists();
        File file = new File(getFilePathForSlot(slot));
        try {
            mapper.writeValue(file, state);
            return true;
        } catch (IOException e) {
            System.err.println("Erro ao salvar no slot " + slot + ": " + e.getMessage());
            return false;
        }
    }

    /**
     * Exclui fisicamente o arquivo de save correspondente ao slot informado.
     *
     * @param slot Número do slot (0 para autosave, 1 a 4 para manuais).
     * @return true se o arquivo existia e foi deletado com sucesso, false caso contrário.
     */
    public boolean deleteSlot(int slot) {
        File file = new File(getFilePathForSlot(slot));
        if (file.exists() && file.isFile()) {
            return file.delete();
        }
        return false;
    }

    /**
     * Procura o primeiro slot manual disponível (de 1 a 4).
     *
     * @return O número do slot vago (1 a 4) ou -1 se todos estiverem ocupados.
     */
    public int findFirstAvailableManualSlot() {
        for (int slot = 1; slot <= 4; slot++) {
            if (!slotExists(slot)) {
                return slot;
            }
        }
        return -1;
    }

    /**
     * Copia o conteúdo do autosave (Slot 0) para o primeiro slot manual disponível (1 a 4).
     *
     * @return O número do slot de destino onde o backup foi gravado, ou -1 se não houver autosave ou vaga.
     */
    public int archiveAutoSaveToEmptySlot() {
        if (!slotExists(0)) {
            return -1;
        }

        int targetSlot = findFirstAvailableManualSlot();
        if (targetSlot != -1) {
            GameStates autoSaveState = loadStateFromSlot(0);
            if (autoSaveState != null) {
                saveStateToSlot(autoSaveState, targetSlot);
                return targetSlot;
            }
        }
        return -1;
    }

    // --- COMPATIBILIDADE ---
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