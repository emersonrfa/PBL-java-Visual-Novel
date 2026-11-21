package ui.screens;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import main.AppContext;
import service.SaveService;
import service.SaveService.LoadedGame;
import ui.effects.RadialPane;
import ui.game.GameRequest;
import ui.navigation.ScreenId;
import ui.navigation.ScreenNavigator;

import java.util.List;

/**
 * Escolha do tipo sanguíneo de Emily, logo depois do Novo Jogo. Os oito tipos ficam em círculo em
 * volta da tela ({@link RadialPane}); escolher um habilita "Começar", que cria o jogo no slot
 * escolhido e abre o Capítulo 1.
 */
public class BloodTypeController extends ScreenController {
    private static final List<String> BLOOD_TYPES = List.of("A+", "B+", "AB+", "O+", "O-", "AB-", "B-", "A-");

    @FXML private RadialPane ring;
    @FXML private Label selectedLabel;
    @FXML private Button startButton;

    private final SaveService saveService;
    private final BloodTypeRequest request;
    private final ToggleGroup group = new ToggleGroup();

    private BloodTypeController(ScreenNavigator navigator, AppContext context, BloodTypeRequest request) {
        super(navigator, context);
        this.saveService = context.getSaveService();
        this.request = request;
    }

    public static BloodTypeController create(ScreenNavigator navigator, AppContext context, BloodTypeRequest request) {
        return load("/fxml/blood_type.fxml", new BloodTypeController(navigator, context, request));
    }

    @FXML
    private void initialize() {
        for (String type : BLOOD_TYPES) {
            ToggleButton button = new ToggleButton(type);
            button.setUserData(type);
            button.setToggleGroup(group);
            button.getStyleClass().add("blood-button");
            ring.getChildren().add(button);
        }
        group.selectedToggleProperty().addListener((obs, old, now) -> {
            if (now == null && old != null) {
                old.setSelected(true); // sempre mantém um tipo escolhido depois do primeiro clique
                return;
            }
            updateSelection(now);
        });
        updateSelection(null);
    }

    private void updateSelection(Toggle selected) {
        boolean has = selected != null;
        startButton.setDisable(!has);
        selectedLabel.setText(has ? "Tipo " + selected.getUserData() : "Nenhum tipo escolhido");
    }

    @FXML
    private void onStart() {
        Toggle selected = group.getSelectedToggle();
        if (selected == null) return;
        LoadedGame game = saveService.newGame((String) selected.getUserData(), request.slot());
        navigator.replaceWith(ScreenId.GAME, new GameRequest(game.player(), game.state()));
    }

    @FXML
    private void onBack() {
        navigator.back();
    }
}
