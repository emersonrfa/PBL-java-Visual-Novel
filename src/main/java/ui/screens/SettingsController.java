package ui.screens;

import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import main.AppContext;
import model.GameConfig;
import ui.navigation.ScreenNavigator;

import java.util.List;

/** Configurações: velocidade do texto, confirmar escolhas, autosave e música (mesmas opções do console). */
public class SettingsController extends ScreenController {
    private static final List<String> SPEED_LABELS = List.of("Instantânea", "Rápida", "Normal", "Lenta");
    private static final int[] SPEED_MS = {0, 10, 25, 50};

    @FXML private ChoiceBox<String> speedChoice;
    @FXML private CheckBox confirmChoicesBox;
    @FXML private CheckBox autoSaveBox;
    @FXML private CheckBox musicBox;
    @FXML private Label statusLabel;

    private final GameConfig config;

    private SettingsController(ScreenNavigator navigator, AppContext context) {
        super(navigator, context);
        this.config = context.getConfig();
    }

    public static SettingsController create(ScreenNavigator navigator, AppContext context) {
        return load("/fxml/settings.fxml", new SettingsController(navigator, context));
    }

    @FXML
    private void initialize() {
        speedChoice.getItems().addAll(SPEED_LABELS);
        speedChoice.getSelectionModel().select(closestSpeedIndex(config.getTextSpeedMs()));
        confirmChoicesBox.setSelected(config.isConfirmChoices());
        autoSaveBox.setSelected(config.isTurnAutoSave());
        musicBox.setSelected(config.isTurnMusic());
    }

    @FXML
    private void onSave() {
        int index = Math.max(0, speedChoice.getSelectionModel().getSelectedIndex());
        config.setTextSpeedMs(SPEED_MS[index]);
        config.setConfirmChoices(confirmChoicesBox.isSelected());
        config.setTurnAutoSave(autoSaveBox.isSelected());
        config.setTurnMusic(musicBox.isSelected());
        context.getConfigRepository().saveConfig(config);
        statusLabel.setText("Configurações salvas.");
    }

    @FXML
    private void onBack() {
        navigator.back();
    }

    private static int closestSpeedIndex(int ms) {
        int best = 0;
        for (int i = 1; i < SPEED_MS.length; i++) {
            if (Math.abs(SPEED_MS[i] - ms) < Math.abs(SPEED_MS[best] - ms)) best = i;
        }
        return best;
    }
}
