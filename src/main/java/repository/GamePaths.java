package repository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

/**
 * Onde o jogo grava seus arquivos.
 *
 * <pre>
 * saves/  somente os saves: autosave.json e save_1..4.json
 * data/   progresso e preferências do jogador: config.json, unlocked_achievements.json, unlocked_endings.json
 * </pre>
 *
 * Os dois ficam na pasta de execução do jogo. Cada um é independente: apagar {@code saves/}
 * zera as partidas sem perder conquistas/finais/configurações.
 */
public final class GamePaths {
    public static final String SAVES_DIR = "saves";
    public static final String DATA_DIR = "data";
    public static final String CONFIG_FILE = DATA_DIR + File.separator + "config.json";

    private GamePaths() {}

    /**
     * Move para {@code data/} os arquivos que versões anteriores gravavam em outro lugar
     * ({@code config.json} na raiz e os {@code unlocked_*.json} dentro de {@code saves/}).
     * Não sobrescreve nada que já exista no destino; seguro de chamar a cada inicialização.
     */
    public static void migrateLegacyFiles() {
        migrateLegacyFiles(".");
    }

    public static void migrateLegacyFiles(String baseDir) {
        Path base = Path.of(baseDir);
        move(base.resolve("config.json"), base.resolve(DATA_DIR).resolve("config.json"));
        move(base.resolve(SAVES_DIR).resolve("unlocked_achievements.json"),
                base.resolve(DATA_DIR).resolve("unlocked_achievements.json"));
        move(base.resolve(SAVES_DIR).resolve("unlocked_endings.json"),
                base.resolve(DATA_DIR).resolve("unlocked_endings.json"));
    }

    private static void move(Path from, Path to) {
        if (!Files.isRegularFile(from) || Files.exists(to)) {
            return;
        }
        try {
            Files.createDirectories(to.getParent());
            Files.move(from, to, StandardCopyOption.ATOMIC_MOVE);
        } catch (IOException e) {
            System.err.println("> Não foi possível mover " + from + " para " + to + ": " + e.getMessage());
        }
    }
}
