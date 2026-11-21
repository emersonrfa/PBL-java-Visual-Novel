package ui.effects;

import javafx.scene.media.AudioClip;

import java.net.URL;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

/**
 * Efeitos sonoros curtos do jogo (arquivos .wav em resources/sounds). Tudo falha em silêncio: se o
 * áudio do sistema ou um arquivo não estiver disponível, o jogo segue sem aquele som.
 *
 * Uso: {@code Sounds.play(Sounds.Effect.CLICK)}. Cada efeito tem o seu volume-padrão aqui.
 */
public final class Sounds {

    public enum Effect {
        HOVER("/sounds/hover.wav", 0.35),
        CLICK("/sounds/click.wav", 0.6),
        TYPE("/sounds/type.wav", 0.18),          // tique grave e baixo da digitação
        BAG_OPEN("/sounds/bag_open.wav", 0.7),   // farfalhar de couro + fivela
        ACHIEVEMENT("/sounds/achievement.wav", 0.65);

        final String path;
        final double volume;

        Effect(String path, double volume) {
            this.path = path;
            this.volume = volume;
        }
    }

    private static final Map<Effect, AudioClip> CLIPS = new EnumMap<>(Effect.class);
    private static final Random RANDOM = new Random();
    private static long lastTypeTickNanos;

    private Sounds() {}

    /** Carrega todos os efeitos (chame uma vez na abertura do app). */
    public static void load() {
        for (Effect effect : Effect.values()) {
            try {
                URL url = Sounds.class.getResource(effect.path);
                if (url != null) {
                    AudioClip clip = new AudioClip(url.toExternalForm());
                    clip.setVolume(effect.volume);
                    CLIPS.put(effect, clip);
                }
            } catch (RuntimeException e) { // sem áudio no sistema, formato recusado etc.
                System.err.println("[Som] Não carregado (" + effect.path + "): " + e.getMessage());
            }
        }
    }

    public static void play(Effect effect) {
        AudioClip clip = CLIPS.get(effect);
        if (clip != null) {
            clip.play();
        }
    }

    /**
     * Tique da digitação: toca no máximo a cada {@code minIntervalMs} e com a velocidade levemente
     * variada, para o texto "soar" de forma gradual e orgânica em vez de uma metralhadora.
     */
    public static void typeTick(long minIntervalMs) {
        AudioClip clip = CLIPS.get(Effect.TYPE);
        long now = System.nanoTime();
        if (clip == null || now - lastTypeTickNanos < minIntervalMs * 1_000_000L) {
            return;
        }
        lastTypeTickNanos = now;
        double rate = 0.9 + RANDOM.nextDouble() * 0.25;  // 0,90 a 1,15
        clip.play(Effect.TYPE.volume, 0, rate, 0, 0);
    }
}
