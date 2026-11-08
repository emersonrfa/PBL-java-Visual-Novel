package ui.screens;

import javafx.fxml.FXML;
import main.AppContext;
import ui.navigation.ScreenNavigator;

/** Créditos: o texto fica no próprio credits.fxml; aqui só o botão Voltar. */
public class CreditsController extends ScreenController {

    private CreditsController(ScreenNavigator navigator, AppContext context) {
        super(navigator, context);
    }

    public static CreditsController create(ScreenNavigator navigator, AppContext context) {
        return load("/fxml/credits.fxml", new CreditsController(navigator, context));
    }

    @FXML
    private void onBack() {
        navigator.back();
    }
}
