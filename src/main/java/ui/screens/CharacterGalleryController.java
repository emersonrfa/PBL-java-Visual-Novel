package ui.screens;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import main.AppContext;
import repository.NpcRepository;
import service.AssetResolver;
import service.CharacterGalleryService;
import service.CharacterGalleryService.GalleryEntry;
import service.CharacterGalleryService.SpriteEntry;
import ui.navigation.ScreenNavigator;

import java.io.InputStream;
import java.util.List;

/**
 * Galeria de Personagens (menu principal): passa de um personagem para outro com as setas ou com as
 * teclas ← e →, mostra a descrição e deixa escolher entre os sprites disponíveis dele.
 * Os dados vêm do {@link CharacterGalleryService}; aqui só o desenho.
 */
public class CharacterGalleryController extends ScreenController {
    private static final String CHIP = "sprite-chip";
    private static final String CHIP_SELECTED = "sprite-chip-selected";
    /** Os sprites são grandes (2000x1116): carrega já reduzido para caber no retrato. */
    private static final double PORTRAIT_LOAD_HEIGHT = 940;

    @FXML private BorderPane root;
    @FXML private Label counterLabel;
    @FXML private Button previousButton;
    @FXML private Button nextButton;
    @FXML private ImageView portrait;
    @FXML private Label noSpriteLabel;
    @FXML private Label nameLabel;
    @FXML private Label roleLabel;
    @FXML private Label metaLabel;
    @FXML private Label descriptionLabel;
    @FXML private FlowPane spriteBox;

    private final List<GalleryEntry> entries;
    private int index;

    private CharacterGalleryController(ScreenNavigator navigator, AppContext context) {
        super(navigator, context);
        this.entries = new CharacterGalleryService(new NpcRepository(), new AssetResolver()).entries();
    }

    public static CharacterGalleryController create(ScreenNavigator navigator, AppContext context) {
        return load("/fxml/characters.fxml", new CharacterGalleryController(navigator, context));
    }

    @FXML
    private void initialize() {
        boolean several = entries.size() > 1;
        previousButton.setDisable(!several);
        nextButton.setDisable(!several);
        root.setFocusTraversable(true);
        root.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.LEFT) {
                onPrevious();
            } else if (e.getCode() == KeyCode.RIGHT) {
                onNext();
            }
        });
        show(0);
    }

    @Override
    public void onShow() {
        Platform.runLater(root::requestFocus);   // para as setas do teclado funcionarem de cara
    }

    @FXML
    private void onPrevious() {
        show((index - 1 + entries.size()) % entries.size());
    }

    @FXML
    private void onNext() {
        show((index + 1) % entries.size());
    }

    @FXML
    private void onBack() {
        navigator.back();
    }

    private void show(int newIndex) {
        if (entries.isEmpty()) {
            return;
        }
        index = newIndex;
        GalleryEntry entry = entries.get(index);

        counterLabel.setText((index + 1) + " / " + entries.size());
        nameLabel.setText(entry.name());
        roleLabel.setText(entry.role());
        metaLabel.setText(entry.age() + " anos" + (entry.vampire() ? "  •  Vampiro" : ""));
        descriptionLabel.setText(entry.description());

        spriteBox.getChildren().clear();
        for (int i = 0; i < entry.sprites().size(); i++) {
            SpriteEntry sprite = entry.sprites().get(i);
            Button chip = new Button(sprite.label());
            chip.getStyleClass().add(CHIP);
            chip.setFocusTraversable(false);   // as setas ← → continuam trocando de personagem
            chip.setOnAction(e -> showSprite(sprite, chip));
            spriteBox.getChildren().add(chip);
        }
        if (entry.hasSprites()) {
            showSprite(entry.sprites().get(0), (Button) spriteBox.getChildren().get(0));
        } else {
            portrait.setImage(null);
        }
        noSpriteLabel.setVisible(!entry.hasSprites());
        if (!entry.hasSprites()) {
            Label none = new Label("Nenhum sprite disponível.");
            none.getStyleClass().add("text-muted");
            spriteBox.getChildren().add(none);
        }
    }

    private void showSprite(SpriteEntry sprite, Button selectedChip) {
        portrait.setImage(loadImage(sprite.path()));
        spriteBox.getChildren().forEach(node -> node.getStyleClass().remove(CHIP_SELECTED));
        selectedChip.getStyleClass().add(CHIP_SELECTED);
    }

    private Image loadImage(String resourcePath) {
        try (InputStream in = CharacterGalleryController.class.getResourceAsStream(resourcePath)) {
            return in == null ? null : new Image(in, 0, PORTRAIT_LOAD_HEIGHT, true, true);
        } catch (Exception e) {
            return null;
        }
    }
}
