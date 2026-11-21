package ui.effects;

import javafx.animation.ScaleTransition;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.ButtonBase;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Font;
import javafx.util.Duration;

import java.net.URL;

/**
 * Identidade visual e sonora compartilhada por todas as telas: carrega as fontes do jogo e instala,
 * uma única vez na {@link Scene}, o som de passar o mouse / clicar e a animação de "crescer" dos
 * botões. Como usa filtros de evento na cena, qualquer botão novo (inclusive os criados em código)
 * ganha os efeitos sem precisar de nenhuma linha extra.
 *
 * Se uma fonte ou um som não puder ser carregado, o jogo continua normalmente (só perde o efeito).
 */
public final class UiEffects {
    private static final String[] FONTS = {
            "/fonts/UnifrakturCook-Bold.ttf",
            "/fonts/Metamorphous-Regular.ttf",
            "/fonts/Almendra-Regular.ttf",
            "/fonts/Almendra-Bold.ttf"
    };
    private static final double HOVER_SCALE = 1.04;

    private UiEffects() {}

    /** Carrega as fontes empacotadas; deve ser chamado antes de criar a primeira tela. */
    public static void loadFonts() {
        for (String path : FONTS) {
            URL url = UiEffects.class.getResource(path);
            if (url == null || Font.loadFont(url.toExternalForm(), 12) == null) {
                System.err.println("[UI] Fonte não carregada: " + path);
            }
        }
    }

    /** Liga som e animação de hover/clique a todos os botões da cena (atuais e futuros). */
    public static void install(Scene scene) {
        Sounds.load();

        scene.addEventFilter(MouseEvent.MOUSE_ENTERED_TARGET, event -> {
            ButtonBase button = buttonOf(event.getTarget());
            if (button != null && !button.isDisabled() && button == event.getTarget()) {
                Sounds.play(Sounds.Effect.HOVER);
                scaleTo(button, HOVER_SCALE);
            }
        });
        scene.addEventFilter(MouseEvent.MOUSE_EXITED_TARGET, event -> {
            ButtonBase button = buttonOf(event.getTarget());
            if (button != null && button == event.getTarget()) {
                scaleTo(button, 1.0);
            }
        });
        scene.addEventFilter(ActionEvent.ACTION, event -> {
            if (event.getTarget() instanceof ButtonBase) {
                Sounds.play(Sounds.Effect.CLICK);
            }
        });
    }

    private static ButtonBase buttonOf(Object target) {
        return target instanceof ButtonBase button ? button : null;
    }

    private static void scaleTo(Node node, double scale) {
        ScaleTransition transition = new ScaleTransition(Duration.millis(110), node);
        transition.setToX(scale);
        transition.setToY(scale);
        transition.play();
    }
}
