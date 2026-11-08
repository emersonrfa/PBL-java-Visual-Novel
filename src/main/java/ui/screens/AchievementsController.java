package ui.screens;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import main.AppContext;
import model.Achievement;
import service.AchievementService;
import ui.navigation.ScreenNavigator;

import java.util.List;

/** Galeria de conquistas: desbloqueadas aparecem completas; as secretas bloqueadas ficam ocultas. */
public class AchievementsController extends ScreenController {

    @FXML private VBox listBox;
    @FXML private Label progressLabel;

    private AchievementsController(ScreenNavigator navigator, AppContext context) {
        super(navigator, context);
    }

    public static AchievementsController create(ScreenNavigator navigator, AppContext context) {
        return load("/fxml/achievements.fxml", new AchievementsController(navigator, context));
    }

    @FXML
    private void initialize() {
        AchievementService service = context.getAchievementService();
        List<Achievement> all = service.getAllAchievements();
        int unlocked = 0;

        for (Achievement achievement : all) {
            boolean isUnlocked = service.getUnlockedRepo().isUnlocked(achievement.getId());
            if (isUnlocked) unlocked++;

            String title = isUnlocked || !achievement.isSecret() ? achievement.getTitle() : "?? (Conquista Secreta) ??";
            String description = isUnlocked || !achievement.isSecret() ? achievement.getDescription()
                    : "Jogue para descobrir os critérios desta façanha.";
            listBox.getChildren().add(row(title, description, isUnlocked));
        }
        progressLabel.setText(unlocked + " de " + all.size() + " conquistas desbloqueadas");
    }

    private static VBox row(String title, String description, boolean unlocked) {
        Label name = new Label((unlocked ? "[DESBLOQUEADA] " : "[BLOQUEADA] ") + title);
        name.getStyleClass().add(unlocked ? "row-title" : "row-title-locked");
        Label text = new Label(description);
        text.getStyleClass().add("text");
        text.setWrapText(true);
        VBox row = new VBox(4, name, text);
        row.getStyleClass().add("row");
        return row;
    }

    @FXML
    private void onBack() {
        navigator.back();
    }
}
