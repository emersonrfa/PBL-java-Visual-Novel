package tests;

import org.junit.jupiter.api.Test;
import ui.effects.MusicDirector;
import ui.effects.MusicPolicy;
import ui.effects.MusicPolicy.Track;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Regras da música: ambiente no jogo todo, tema só nos créditos, e ligar/desligar nas configurações
 * (ao salvar) para ou volta a tocar sem reiniciar a trilha à toa.
 */
public class MusicPolicyTest {

    /** Anota o que foi pedido ao "tocador". */
    private static class FakeBackend implements MusicPolicy.Backend {
        final List<String> calls = new ArrayList<>();

        @Override public void play(Track track) { calls.add("play:" + track); }
        @Override public void stop() { calls.add("stop"); }
    }

    private final FakeBackend backend = new FakeBackend();
    private final MusicPolicy music = new MusicPolicy(backend);

    @Test
    public void startsSilentUntilEnabled() {
        assertNull(music.current());
        assertTrue(backend.calls.isEmpty());
    }

    @Test
    public void enablingPlaysTheAmbientTrack() {
        music.setEnabled(true);

        assertEquals(Track.AMBIENT, music.current());
        assertEquals(List.of("play:AMBIENT"), backend.calls);
    }

    @Test
    public void savingSettingsAgainWithMusicOnDoesNotRestartTheTrack() {
        music.setEnabled(true);
        music.setEnabled(true);

        assertEquals(List.of("play:AMBIENT"), backend.calls);
    }

    @Test
    public void disablingStopsAndEnablingAgainPlaysAgain() {
        music.setEnabled(true);
        music.setEnabled(false);
        assertNull(music.current());

        music.setEnabled(true);

        assertEquals(List.of("play:AMBIENT", "stop", "play:AMBIENT"), backend.calls);
    }

    @Test
    public void disablingWhenAlreadySilentDoesNothing() {
        music.setEnabled(false);

        assertTrue(backend.calls.isEmpty());
    }

    @Test
    public void themePlaysOnlyInTheCreditsAndAmbientReturnsAfterwards() {
        music.setEnabled(true);

        music.enterCredits();
        assertEquals(Track.THEME, music.current());

        music.leaveCredits();
        assertEquals(Track.AMBIENT, music.current());
        assertEquals(List.of("play:AMBIENT", "play:THEME", "play:AMBIENT"), backend.calls);
    }

    @Test
    public void creditsStaySilentWhenMusicIsOff() {
        music.setEnabled(false);

        music.enterCredits();
        music.leaveCredits();

        assertTrue(backend.calls.isEmpty());
    }

    @Test
    public void turningMusicOffInsideTheCreditsStopsTheTheme() {
        music.setEnabled(true);
        music.enterCredits();

        music.setEnabled(false);

        assertNull(music.current());
        assertEquals("stop", backend.calls.get(backend.calls.size() - 1));
    }

    @Test
    public void ambientIsQuieterThanTheThemeAndBothFilesExist() {
        assertTrue(MusicDirector.AMBIENT_VOLUME <= 0.25, "a trilha ambiente deve ser baixa");
        assertTrue(MusicDirector.AMBIENT_VOLUME < MusicDirector.THEME_VOLUME);
        assertNotNull(MusicDirector.class.getResource(MusicDirector.AMBIENT_PATH), "falta " + MusicDirector.AMBIENT_PATH);
        assertNotNull(MusicDirector.class.getResource(MusicDirector.THEME_PATH), "falta " + MusicDirector.THEME_PATH);
    }
}
