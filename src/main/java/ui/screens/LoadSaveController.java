package ui.screens;

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import main.AppContext;
import service.SaveService;
import service.SaveService.LoadedGame;
import service.SaveService.SlotSummary;
import ui.game.GameRequest;
import ui.navigation.ScreenId;
import ui.navigation.ScreenNavigator;

/** Carregar Jogo: lista o autosave e os 4 slots, com Carregar e Excluir. */
public class LoadSaveController extends ScreenController {

    @FXML private VBox slotsBox;
    @FXML private Label statusLabel;

    private final SaveService saveService;

    private LoadSaveController(ScreenNavigator navigator, AppContext context) {
        super(navigator, context);
        this.saveService = context.getSaveService();
    }

    public static LoadSaveController create(ScreenNavigator navigator, AppContext context) {
        return load("/fxml/load_save.fxml", new LoadSaveController(navigator, context));
    }

    @FXML
    private void initialize() {
        refresh();
    }

    /** Reconstrói as linhas (cada slot é um HBox: texto + botões). */
    private void refresh() {
        slotsBox.getChildren().clear();
        for (SlotSummary summary : saveService.summaries(true)) {
            Label text = new Label(summary.text());
            text.getStyleClass().add(summary.loadable() ? "text" : "text-muted");
            text.setWrapText(true);
            Region spacer = new Region();
            HBox.setHgrow(spacer, Priority.ALWAYS);

            HBox row = new HBox(10, text, spacer);
            row.setAlignment(Pos.CENTER_LEFT);
            row.getStyleClass().add("row");

            if (summary.loadable()) {
                Button load = new Button("Carregar");
                load.getStyleClass().add("menu-button");
                load.setOnAction(e -> loadSlot(summary.slot()));
                row.getChildren().add(load);
            }
            if (!summary.empty() && summary.slot() != SaveService.AUTOSAVE_SLOT) {
                Button delete = new Button("Excluir");
                delete.getStyleClass().add("menu-button");
                delete.setOnAction(e -> deleteSlot(summary.slot()));
                row.getChildren().add(delete);
            }
            slotsBox.getChildren().add(row);
        }
    }

    private void loadSlot(int slot) {
        try {
            LoadedGame game = saveService.load(slot);
            navigator.replaceWith(ScreenId.GAME, new GameRequest(game.player(), game.state()));
        } catch (IllegalStateException e) {
            statusLabel.setText("Não foi possível carregar: " + e.getMessage());
        }
    }

    private void deleteSlot(int slot) {
        statusLabel.setText(saveService.delete(slot) ? "Slot " + slot + " excluído." : "Não foi possível excluir o slot " + slot + ".");
        refresh();
    }

    @FXML
    private void onBack() {
        navigator.back();
    }
}
