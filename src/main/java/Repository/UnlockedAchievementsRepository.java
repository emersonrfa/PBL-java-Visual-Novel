package Repository;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class UnlockedAchievementsRepository {
    private static final String SAVES_DIR = "saves";
    private static final String FILE_PATH = SAVES_DIR + File.separator + "unlocked_achievements.json";

    private final ObjectMapper mapper;
    private Set<String> unlockedKeys;

    public UnlockedAchievementsRepository() {
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
                System.err.println("Erro ao carregar conquistas desbloqueadas: " + e.getMessage());
            }
        }
    }

    public synchronized boolean unlock(String achievementKey) {
        if (achievementKey == null || achievementKey.trim().isEmpty()) {
            return false;
        }
        if (unlockedKeys.add(achievementKey)) {
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
            System.err.println("Erro ao salvar conquistas desbloqueadas: " + e.getMessage());
        }
    }

    public boolean isUnlocked(String achievementKey) {
        return achievementKey != null && unlockedKeys.contains(achievementKey);
    }

    public Set<String> getUnlockedKeys() {
        return new HashSet<>(unlockedKeys);
    }
}
