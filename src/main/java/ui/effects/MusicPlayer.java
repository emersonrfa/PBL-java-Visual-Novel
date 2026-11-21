package ui.effects;

import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

import java.net.URL;

/**
 * Música de fundo em laço (resources/music/ambiente.wav), controlada pela preferência
 * {@code turnMusic} da configuração: {@link #apply(boolean)} liga ou desliga na hora, com um
 * pequeno fade, e é chamado ao abrir o app e ao salvar as configurações.
 *
 * Falha em silêncio (sem áudio no sistema ou arquivo ausente), como {@link Sounds}.
 */
public final class MusicPlayer {
    /** Volume da trilha: baixo, para não competir com os efeitos nem com a leitura. */
    public static final double VOLUME = 0.18;
    private static final String TRACK = "/music/ambiente.wav";
    private static final Duration FADE = Duration.millis(600);

    private static MediaPlayer player;
    private static boolean failed;

    private MusicPlayer() {}

    /** Liga (toca em laço) ou desliga (fade-out e para) a música. */
    public static void apply(boolean enabled) {
        if (enabled) {
            start();
        } else {
            stop();
        }
    }

    public static boolean isPlaying() {
        return player != null && player.getStatus() == MediaPlayer.Status.PLAYING;
    }

    private static void start() {
        MediaPlayer current = ensurePlayer();
        if (current == null || isPlaying()) {
            return;
        }
        current.setVolume(0);
        current.play();
        fadeTo(current, VOLUME, null);
    }

    private static void stop() {
        MediaPlayer current = player;
        if (current == null || current.getStatus() != MediaPlayer.Status.PLAYING) {
            return;
        }
        fadeTo(current, 0, current::pause);   // pause mantém o ponto: ao religar, continua daí
    }

    private static MediaPlayer ensurePlayer() {
        if (player != null || failed) {
            return player;
        }
        try {
            URL url = MusicPlayer.class.getResource(TRACK);
            if (url == null) {
                failed = true;
                return null;
            }
            player = new MediaPlayer(new Media(url.toExternalForm()));
            player.setCycleCount(MediaPlayer.INDEFINITE);
            player.setVolume(VOLUME);
        } catch (RuntimeException e) {   // sem áudio no sistema, formato recusado etc.
            failed = true;
            System.err.println("[Música] Não carregada: " + e.getMessage());
        }
        return player;
    }

    private static void fadeTo(MediaPlayer target, double volume, Runnable then) {
        Timeline fade = new Timeline(new KeyFrame(FADE, new KeyValue(target.volumeProperty(), volume)));
        if (then != null) {
            fade.setOnFinished(e -> then.run());
        }
        fade.play();
    }
}
