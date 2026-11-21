package ui.screens;

import javafx.fxml.FXML;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import main.AppContext;
import service.SaveService;
import service.SaveService.SlotSummary;
import ui.navigation.ScreenId;
import ui.navigation.ScreenNavigator;

import java.util.List;

/** Novo Jogo: escolhe o slot (confirmando sobrescrita) e segue para a tela de tipo sanguíneo. */
public class NewGameController extends ScreenController {
    @FXML private ChoiceBox<String> slotChoice;
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
    }

    @FXML
    private void onStart() {
        int index = slotChoice.getSelectionModel().getSelectedIndex();
        if (index < 0) {
            statusLabel.setText("Escolha um slot.");
            return;
        }
        SlotSummary slot = slots.get(index);
        if (!slot.empty() && !overwriteArmed) {
            overwriteArmed = true;
            statusLabel.setText("Esse slot já tem um save. Clique em Continuar de novo para sobrescrevê-lo.");
            return;
        }
        navigator.goTo(ScreenId.BLOOD_TYPE, new BloodTypeRequest(slot.slot()));
    }

    @FXML
    private void onBack() {
        navigator.back();
    }
}
