package ui.navigation;

import event.Observer;
import javafx.scene.layout.StackPane;
import ui.navigation.NavigationModel.NavigationEvent;

import java.util.EnumMap;
import java.util.Map;
import java.util.function.Supplier;

/**
 * Troca a tela visível. A janela tem uma única Scene cujo conteúdo é {@link #root()}; cada
 * navegação substitui o conteúdo desse painel pela tela pedida.
 *
 * <pre>
 * ScreenNavigator navigator = new ScreenNavigator(new NavigationModel());
 * navigator.register(ScreenId.MAIN_MENU, () -> new MainMenuScreen(navigator, context));
 * navigator.register(ScreenId.GAME,      () -> new GameScreen(...));
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
    private final Map<ScreenId, Supplier<Screen>> factories = new EnumMap<>(ScreenId.class);
    private final Observer<NavigationEvent> onNavigation = this::show;
    private Screen shown;

    public ScreenNavigator(NavigationModel model) {
        this.model = model;
        model.addObserver(onNavigation);
    }

    public ScreenNavigator register(ScreenId id, Supplier<Screen> factory) {
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
        requireRegistered(id);
        model.goTo(id);
    }

    public void replaceWith(ScreenId id) {
        requireRegistered(id);
        model.replaceWith(id);
    }

    public void resetTo(ScreenId id) {
        requireRegistered(id);
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

    private void show(NavigationEvent event) {
        if (shown != null) {
            shown.onHide();
        }
        shown = factories.get(event.to()).get();
        root.getChildren().setAll(shown.view());
        shown.onShow();
    }
}
