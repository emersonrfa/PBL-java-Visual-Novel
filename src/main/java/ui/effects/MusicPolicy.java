package ui.effects;

/**
 * Regra de qual música toca em cada momento, separada do JavaFX para poder ser testada.
 *
 * <ul>
 *   <li>Música desligada nas configurações: silêncio em qualquer tela.</li>
 *   <li>Ligada: toca a trilha ambiente em todo o jogo, exceto na tela de créditos,
 *       onde toca o tema.</li>
 * </ul>
 * O {@link Backend} só é chamado quando a música realmente muda: salvar as configurações de novo com o
 * mesmo valor não reinicia a trilha.
 */
public final class MusicPolicy {

    public enum Track { AMBIENT, THEME }

    /** Quem realmente toca o som (no jogo: {@link MusicDirector}; nos testes: um falso). */
    public interface Backend {
        void play(Track track);

        void stop();
    }

    private final Backend backend;
    private boolean enabled;
    private boolean inCredits;
    private Track current;   // nulo = silêncio

    public MusicPolicy(Backend backend) {
        this.backend = backend;
    }

    /** Preferência "música" das configurações (ao abrir o app e ao salvar). */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
        update();
    }

    public void enterCredits() {
        inCredits = true;
        update();
    }

    public void leaveCredits() {
        inCredits = false;
        update();
    }

    /** Trilha tocando agora; nulo se está em silêncio. */
    public Track current() {
        return current;
    }

    private void update() {
        Track wanted = !enabled ? null : (inCredits ? Track.THEME : Track.AMBIENT);
        if (wanted == current) {
            return;
        }
        current = wanted;
        if (wanted == null) {
            backend.stop();
        } else {
            backend.play(wanted);
        }
    }
}
