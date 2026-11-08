package ui.screens;

import javafx.application.Platform;
import javafx.fxml.FXML;
import main.AppContext;
import ui.navigation.ScreenId;
import ui.navigation.ScreenNavigator;

/** Menu inicial. Cada método abaixo é chamado por um botão de main_menu.fxml (onAction="#nome"). */
public class MainMenuController extends ScreenController {

    private MainMenuController(ScreenNavigator navigator, AppContext context) {
        super(navigator, context);
    }

    public static MainMenuController create(ScreenNavigator navigator, AppContext context) {
        return load("/fxml/main_menu.fxml", new MainMenuController(navigator, context));
    }

    @FXML
    private void onPlay() {
        navigator.goTo(ScreenId.PLAY);
    }

    @FXML
    private void onEndings() {
        navigator.goTo(ScreenId.ENDINGS);
    }

    @FXML
    private void onAchievements() {
        navigator.goTo(ScreenId.ACHIEVEMENTS);
    }

    @FXML
    private void onSettings() {
        navigator.goTo(ScreenId.SETTINGS);
    }

    @FXML
    private void onCredits() {
        navigator.goTo(ScreenId.CREDITS);
    }

    @FXML
    private void onQuit() {
        Platform.exit(); // o arquivamento do autosave acontece em App.stop()
    }
}
