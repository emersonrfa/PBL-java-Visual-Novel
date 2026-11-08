package ui.screens;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import main.AppContext;
import model.Ending;
import service.EndingService;
import ui.navigation.ScreenNavigator;

import java.util.List;

/** Galeria de finais: os já alcançados mostram título e descrição; os outros ficam como "???". */
public class EndingsController extends ScreenController {

    @FXML private VBox listBox;
    @FXML private Label progressLabel;

    private EndingsController(ScreenNavigator navigator, AppContext context) {
        super(navigator, context);
    }

    public static EndingsController create(ScreenNavigator navigator, AppContext context) {
        return load("/fxml/endings.fxml", new EndingsController(navigator, context));
    }

    @FXML
    private void initialize() {
        EndingService service = context.getEndingService();
        List<Ending> all = service.getAllEndings();
        int unlocked = 0;

        for (Ending ending : all) {
            boolean isUnlocked = service.isUnlocked(ending);
            if (isUnlocked) unlocked++;

            Label name = new Label(isUnlocked ? ending.getTitle() : "???");
            name.getStyleClass().add(isUnlocked ? "row-title" : "row-title-locked");
            Label text = new Label(isUnlocked ? ending.getDescription() : "Final ainda não descoberto.");
            text.getStyleClass().add("text");
            text.setWrapText(true);
            VBox row = new VBox(4, name, text);
            row.getStyleClass().add("row");
            listBox.getChildren().add(row);
        }
        progressLabel.setText(unlocked + " de " + all.size() + " finais descobertos");
    }

    @FXML
    private void onBack() {
        navigator.back();
    }
}
