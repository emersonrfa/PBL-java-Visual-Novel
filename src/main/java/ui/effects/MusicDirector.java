package ui.effects;

/**
 * Ponto único de controle da música do jogo. Usa {@link MusicPolicy} para decidir e {@link Music}
 * (MediaPlayer em loop) para tocar. A trilha ambiente toca no jogo todo, em volume baixo;
 * o tema (tema.mp3) só toca na tela de créditos.
 */
public final class MusicDirector {
    public static final String AMBIENT_PATH = "/music/ambiente.wav";
    public static final String THEME_PATH = "/music/tema.mp3";
    /** Ambiente baixa, para não competir com a leitura e os efeitos. */
    public static final double AMBIENT_VOLUME = 0.18;
    public static final double THEME_VOLUME = 0.4;

    private static final MusicPolicy POLICY = new MusicPolicy(new MusicPolicy.Backend() {
        @Override
        public void play(MusicPolicy.Track track) {
            if (track == MusicPolicy.Track.THEME) {
                Music.play(THEME_PATH, THEME_VOLUME);
            } else {
                Music.play(AMBIENT_PATH, AMBIENT_VOLUME);
            }
        }

        @Override
        public void stop() {
            Music.stop();
        }
    });

    private MusicDirector() {}

    /** Liga ou desliga a música (preferência salva nas configurações). */
    public static void setEnabled(boolean enabled) {
        POLICY.setEnabled(enabled);
    }

    public static void enterCredits() {
        POLICY.enterCredits();
    }

    public static void leaveCredits() {
        POLICY.leaveCredits();
    }
}
