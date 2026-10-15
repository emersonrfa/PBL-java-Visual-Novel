package Repository;

import Model.GameStates;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;

public class StateRepository {
    private static final String FILE_PATH = "state.json";
    private final ObjectMapper objectMapper;
    private GameStates currentStates;

    public StateRepository() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.currentStates = loadState();
    }

    public GameStates loadState() {
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            GameStates defaultStates = new GameStates();
            saveState(defaultStates);
            this.currentStates = defaultStates;
            return defaultStates;
        }

        try {
            this.currentStates = objectMapper.readValue(file, GameStates.class);
            return this.currentStates;
        } catch (IOException e) {
            System.err.println("> Erro ao carregar arquivo de save: " + e.getMessage());
            this.currentStates = new GameStates();
            return this.currentStates;
        }
    }

    public void saveState(GameStates state) {
        File file = new File(FILE_PATH);
        try {
            objectMapper.writeValue(file, state);
            this.currentStates = state;
            System.out.println(">>> ARQUIVO SALVO COM SUCESSO EM: " + file.getAbsolutePath());
        } catch (IOException e) {
            System.err.println("> Erro ao salvar o progresso: " + e.getMessage());
        }
    }

    public GameStates getCurrentStates() {
        return currentStates;
    }
}