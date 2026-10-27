package Repository;

import Model.GameConfig;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;

public class ConfigRepository {
    private static final String FILE_PATH = "config.json";
    private final ObjectMapper objectMapper;
    private GameConfig currentConfig;

    public ConfigRepository() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
        this.currentConfig = loadConfig();
    }

    public GameConfig loadConfig() {
        File file = new File(FILE_PATH);

        if (!file.exists()) {
            GameConfig defaultConfig = new GameConfig();
            saveConfig(defaultConfig);
            this.currentConfig = defaultConfig;
            return defaultConfig;
        }

        try {
            this.currentConfig = objectMapper.readValue(file, GameConfig.class);
            return this.currentConfig;
        }
        catch (IOException e) {
            System.err.println("> Erro ao carregar arquivo de configurações:" + e.getMessage());
            this.currentConfig = new GameConfig();
            return this.currentConfig;
        }
    }

    public void saveConfig(GameConfig config) {
        File file = new File(FILE_PATH);
        try {
            objectMapper.writeValue(file, config);
            this.currentConfig = config;
        }
        catch (IOException e) {
            System.err.println("> Erro ao salvar configurações:" + e.getMessage());
        }
    }

    public GameConfig getCurrentConfig() {
        return currentConfig;
    }
}
