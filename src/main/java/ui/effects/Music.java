package ui.effects;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import java.net.URL;

/** Música de fundo em loop (arquivo mp3 em resources/music). Falha em silêncio, como o Sounds. */
public final class Music {

    // static de propósito: se ninguém guardar o MediaPlayer, o Java o descarta e a música para.
    private static MediaPlayer player;

    private Music() {}

    public static void play(String resourcePath, double volume) {
        stop();
        try {
            URL url = Music.class.getResource(resourcePath);
            if (url == null) {
                System.err.println("[Música] não encontrada: " + resourcePath);
                return;
            }
            MediaPlayer p = new MediaPlayer(new Media(url.toExternalForm()));
            p.setCycleCount(MediaPlayer.INDEFINITE); // loop infinito
            p.setVolume(volume);                     // 0.0 a 1.0
            p.setOnError(() -> System.err.println("[Música] erro: " + p.getError()));
            p.play();
            player = p;
        } catch (RuntimeException e) {
            System.err.println("[Música] não carregada (" + resourcePath + "): " + e.getMessage());
        }
    }

    public static void stop() {
        if (player != null) {
            player.stop();
            player.dispose();
            player = null;
        }
    }
}