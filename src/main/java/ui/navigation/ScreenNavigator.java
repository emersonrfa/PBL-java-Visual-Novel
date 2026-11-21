package ui.navigation;

import event.Observer;
import javafx.animation.FadeTransition;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import ui.navigation.NavigationModel.NavigationEvent;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Troca a tela visível. A janela tem uma única Scene cujo conteúdo é {@link #root()}; cada
 * navegação substitui o conteúdo desse painel pela tela pedida.
 *
 * <pre>
 * ScreenNavigator navigator = new ScreenNavigator(new NavigationModel());
 * navigator.register(ScreenId.MAIN_MENU, () -> new MainMenuScreen(navigator, context));
 * navigator.registerWithArgument(ScreenId.GAME, arg -> GameController.create(navigator, context, (GameRequest) arg));
 * stage.setScene(new Scene(navigator.root(), 1280, 720));
 * navigator.goTo(ScreenId.MAIN_MENU);
 * </pre>
 *
 * Quem decide para onde ir é o {@link NavigationModel} (sem JavaFX, testado); este navegador só o
 * observa e desenha. As telas recebem o navigator no construtor para pedir {@code goTo/back}.
 */
public class ScreenNavigator {
    private final StackPane root = new StackPane();
    private final NavigationModel model;
    private final Map<ScreenId, Function<Object, Screen>> factories = new EnumMap<>(ScreenId.class);
    private final Observer<NavigationEvent> onNavigation = this::show;
    private Screen shown;
    private Object pendingArgument;

    // fade entre telas: a tela antiga some, a nova aparece
    private static final Duration FADE_OUT = Duration.millis(140);
    private static final Duration FADE_IN = Duration.millis(260);
    private FadeTransition fadeOut;
    private NavigationEvent waitingEvent;   // destino mais recente pedido durante o fade
    private Object waitingArgument;

    public ScreenNavigator(NavigationModel model) {
        this.model = model;
        model.addObserver(onNavigation);
    }

    /** Registra uma tela que não recebe nada ao abrir. */
    public ScreenNavigator register(ScreenId id, Supplier<Screen> factory) {
        factories.put(id, argument -> factory.get());
        return this;
    }

    /** Registra uma tela que recebe o argumento passado em {@code goTo(id, argumento)} (nulo se não houve). */
    public ScreenNavigator registerWithArgument(ScreenId id, Function<Object, Screen> factory) {
        factories.put(id, factory);
        return this;
    }

    public StackPane root() {
        return root;
    }

    public NavigationModel model() {
        return model;
    }

    public void goTo(ScreenId id) {
        goTo(id, null);
    }

    public void goTo(ScreenId id, Object argument) {
        requireRegistered(id);
        pendingArgument = argument;
        model.goTo(id);
    }

    public void replaceWith(ScreenId id) {
        replaceWith(id, null);
    }

    public void replaceWith(ScreenId id, Object argument) {
        requireRegistered(id);
        pendingArgument = argument;
        model.replaceWith(id);
    }

    public void resetTo(ScreenId id) {
        requireRegistered(id);
        pendingArgument = null;
        model.resetTo(id);
    }

    public boolean back() {
        return model.back();
    }

    private void requireRegistered(ScreenId id) {
        if (!factories.containsKey(id)) {
            throw new IllegalStateException("Tela não registrada no ScreenNavigator: " + id);
        }
    }

    /**
     * Mostra a tela pedida com fade: a atual some rapidamente e a nova surge suavemente. Se outra
     * navegação chegar no meio do fade, vale só a mais recente (a tela do meio nunca é criada).
     */
    private void show(NavigationEvent event) {
        waitingEvent = event;
        waitingArgument = pendingArgument;
        pendingArgument = null;

        if (fadeOut != null) {
            return; // já está sumindo; ao terminar mostra o destino mais recente
        }
        Node old = root.getChildren().isEmpty() ? null : root.getChildren().get(0);
        if (old == null) {
            swapToWaiting();
            return;
        }
        old.setMouseTransparent(true); // sem cliques na tela que está saindo
        fadeOut = new FadeTransition(FADE_OUT, old);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> {
            fadeOut = null;
            swapToWaiting();
        });
        fadeOut.play();
    }

    private void swapToWaiting() {
        NavigationEvent event = waitingEvent;
        Object argument = waitingArgument;
        waitingEvent = null;
        waitingArgument = null;
        if (event == null) return;

        if (shown != null) {
            shown.onHide();
        }
        shown = factories.get(event.to()).apply(argument);
        Node view = shown.view();
        view.setOpacity(0);
        root.getChildren().setAll(view);
        shown.onShow();
        FadeTransition fadeIn = new FadeTransition(FADE_IN, view);
        fadeIn.setToValue(1);
        fadeIn.play();
    }
}
