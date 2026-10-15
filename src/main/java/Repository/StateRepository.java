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

    // Garante que a pasta "saves/" exista na raiz do projeto
    private void ensureDirectoryExists() {
        File dir = new File(SAVES_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    // Converte o índice de slot no nome de arquivo correto
    // 0 = autosave | 1 a 4 = save_1.json até save_4.json
    private String getFilePathForSlot(int slot) {
        if (slot == 0) {
            return AUTOSAVE_FILE;
        }
        return SAVES_DIR + File.separator + "save_" + slot + ".json";
    }

    // Verifica se um slot específico já possui arquivo salvo
    public boolean slotExists(int slot) {
        File file = new File(getFilePathForSlot(slot));
        return file.exists() && file.length() > 0;
    }

    // Carrega o estado de um slot específico (retorna null se estiver vazio ou com erro)
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

    // Salva o estado em um slot específico (0 para autosave, 1 a 4 para manuais)
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

    // Deleta o save de um slot específico
    public boolean deleteSlot(int slot) {
        File file = new File(getFilePathForSlot(slot));
        if (file.exists()) {
            return file.delete();
        }
        return false;
    }

    // --- MÉTODOS DE COMPATIBILIDADE COM O CÓDIGO ANTERIOR ---

    // O DialogueController e os auto-saves continuam usando esses métodos padrão,
    // que agora direcionam automaticamente para o Slot 0 (autosave.json)
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