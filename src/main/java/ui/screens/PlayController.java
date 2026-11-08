package ui.screens;

import javafx.fxml.FXML;
import main.AppContext;
import ui.navigation.ScreenId;
import ui.navigation.ScreenNavigator;

/** "Jogar": escolhe entre Novo Jogo e Carregar Jogo. */
public class PlayController extends ScreenController {

    private PlayController(ScreenNavigator navigator, AppContext context) {
        super(navigator, context);
    }

    public static PlayController create(ScreenNavigator navigator, AppContext context) {
        return load("/fxml/play.fxml", new PlayController(navigator, context));
    }

    @FXML
    private void onNewGame() {
        navigator.goTo(ScreenId.NEW_GAME);
    }

    @FXML
    private void onLoadGame() {
        navigator.goTo(ScreenId.LOAD_SAVE);
    }

    @FXML
    private void onBack() {
        navigator.back();
    }
}
