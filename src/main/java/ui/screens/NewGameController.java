package ui.screens;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import main.AppContext;
import service.SaveService;
import service.SaveService.LoadedGame;
import service.SaveService.SlotSummary;
import ui.game.GameRequest;
import ui.navigation.ScreenId;
import ui.navigation.ScreenNavigator;

import java.util.List;

/** Novo Jogo: escolhe o slot e o tipo sanguíneo e abre a tela do jogo no Capítulo 1. */
public class NewGameController extends ScreenController {
    private static final List<String> BLOOD_TYPES = List.of("A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-");

    @FXML private ChoiceBox<String> slotChoice;
    @FXML private ChoiceBox<String> bloodChoice;
    @FXML private Label statusLabel;

    private final SaveService saveService;
    private List<SlotSummary> slots;
    private boolean overwriteArmed;

    private NewGameController(ScreenNavigator navigator, AppContext context) {
        super(navigator, context);
        this.saveService = context.getSaveService();
    }

    public static NewGameController create(ScreenNavigator navigator, AppContext context) {
        return load("/fxml/new_game.fxml", new NewGameController(navigator, context));
    }

    /** Chamado automaticamente pelo FXML logo depois de criar os campos @FXML. */
    @FXML
    private void initialize() {
        slots = saveService.summaries(false);
        for (SlotSummary slot : slots) {
            slotChoice.getItems().add(slot.text());
        }
        slotChoice.getSelectionModel().selectFirst();
        slotChoice.getSelectionModel().selectedIndexProperty().addListener((obs, old, now) -> {
            overwriteArmed = false;
            statusLabel.setText("");
        });

        bloodChoice.getItems().addAll(BLOOD_TYPES);
        bloodChoice.getSelectionModel().selectFirst();
    }

    @FXML
    private void onStart() {
        int index = slotChoice.getSelectionModel().getSelectedIndex();
        String blood = bloodChoice.getValue();
        if (index < 0 || blood == null) {
            statusLabel.setText("Escolha um slot e um tipo sanguíneo.");
            return;
        }
        SlotSummary slot = slots.get(index);
        if (!slot.empty() && !overwriteArmed) {
            overwriteArmed = true;
            statusLabel.setText("Esse slot já tem um save. Clique em Iniciar de novo para sobrescrevê-lo.");
            return;
        }
        LoadedGame game = saveService.newGame(blood, slot.slot());
        navigator.replaceWith(ScreenId.GAME, new GameRequest(game.player(), game.state()));
    }

    @FXML
    private void onBack() {
        navigator.back();
    }
}
