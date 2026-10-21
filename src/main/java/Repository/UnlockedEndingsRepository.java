package Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class UnlockedEndingsRepository {
    private static final String SAVES_DIR = "saves";
    private static final String FILE_PATH = SAVES_DIR + File.separator + "unlocked_endings.json";

    private ObjectMapper mapper;
    private Set<String> unlockedKeys;

    public UnlockedEndingsRepository() {
        this.mapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);
        this.unlockedKeys = new HashSet<>();
        ensureDirectoryExists();
        load();
    }

    private void ensureDirectoryExists() {
        File dir = new File(SAVES_DIR);
        if (!dir.exists()) {
            dir.mkdirs();
        }
    }

    public synchronized void load() {
        File file = new File(FILE_PATH);
        if (file.exists() && file.length() > 0) {
            try {
                Set<String> loaded = mapper.readValue(file, new TypeReference<Set<String>>() {});
                if (loaded != null) {
                    this.unlockedKeys = loaded;
                }
            } catch (IOException e) {
                System.err.println("Erro ao carregar finais desbloqueados: " + e.getMessage());
            }
        }
    }

    public synchronized boolean unlockEnding(String endingKey) {
        if (endingKey == null || endingKey.trim().isEmpty()) {
            return false;
        }

        // Se o final ainda não havia sido registrado
        if (unlockedKeys.add(endingKey)) {
            save();
            return true;
        }
        return false;
    }

    private synchronized void save() {
        ensureDirectoryExists();
        File file = new File(FILE_PATH);
        try {
            mapper.writeValue(file, unlockedKeys);
        } catch (IOException e) {
            System.err.println("Erro ao salvar final desbloqueado: " + e.getMessage());
        }
    }

    public boolean isUnlocked(String endingKey) {
        return endingKey != null && unlockedKeys.contains(endingKey);
    }

    public Set<String> getUnlockedKeys() {
        return new HashSet<>(unlockedKeys);
    }
}