package ui.game;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import main.AppContext;
import model.Ending;
import model.GameConfig;
import service.DialogueSnapshot.ChoiceView;
import service.GameSession;
import service.InventoryService.InventoryEntry;
import ui.navigation.ScreenId;
import ui.navigation.ScreenNavigator;
import ui.screens.ScreenController;

import java.io.InputStream;
import java.util.List;

/**
 * A tela única do jogo ({@code game.fxml}). Não decide nada sobre a história: o
 * {@link GameScreenPresenter} manda o que mostrar (via {@link GameScreenView}) e esta classe só
 * preenche os elementos do FXML e repassa cliques/teclas ao presenter.
 *
 * Entradas: clique na caixa de diálogo, ESPAÇO ou ENTER = continuar (ou completar o texto que
 * está sendo digitado). Imagens ausentes simplesmente não aparecem.
 */
public class GameController extends ScreenController implements GameScreenView {

    @FXML private StackPane root;
    @FXML private ImageView background;
    @FXML private ImageView sprite;
    @FXML private Label energyLabel;
    @FXML private Label moneyLabel;
    @FXML private Label speakerLabel;
    @FXML private Label textLabel;
    @FXML private Label continueHint;
    @FXML private VBox choicesBox;
    @FXML private VBox toastBox;
    @FXML private Label chapterBanner;
    @FXML private StackPane inventoryOverlay;
    @FXML private VBox inventoryList;
    @FXML private StackPane saveOverlay;
    @FXML private VBox saveSlotsBox;
    @FXML private StackPane endOverlay;
    @FXML private Label endTitle;
    @FXML private Label endText;
    @FXML private StackPane confirmOverlay;

    private final String startDialogueId;
    private final int textSpeedMs;
    private GameScreenPresenter presenter;

    private Timeline typing;
    private String fullText = "";
    private boolean typingActive;
    private boolean continueActive;
    private Runnable afterText;
    private boolean begun;

    private GameController(ScreenNavigator navigator, AppContext context, String startDialogueId) {
        super(navigator, context);
        this.startDialogueId = startDialogueId;
        GameConfig config = context.getConfig();
        this.textSpeedMs = config == null ? 0 : config.getTextSpeedMs();
    }

    /** Cria a tela já com a partida pronta (nova ou carregada). */
    public static GameController create(ScreenNavigator navigator, AppContext context, GameRequest request) {
        // save apontando para um capítulo que não existe (mais) no catálogo: recomeça do Capítulo 1
        boolean known = context.hasChapter(request.chapterNumber());
        int chapter = known ? request.chapterNumber() : 1;
        GameController controller = new GameController(navigator, context, known ? request.dialogueId() : null);
        GameSession session = context.createGameSession(request.player(), chapter, request.state());
        controller.presenter = new GameScreenPresenter(session, controller, context.getStateRepository());
        return load("/fxml/game.fxml", controller);
    }

    /** Chamado pelo FXML depois de criar os campos: ajustes que o FXML não faz sozinho. */
    @FXML
    private void initialize() {
        background.fitWidthProperty().bind(root.widthProperty());
        background.fitHeightProperty().bind(root.heightProperty());
        sprite.fitHeightProperty().bind(root.heightProperty());

        continueHint.setVisible(false);
        root.setFocusTraversable(true);
        root.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE || e.getCode() == KeyCode.ENTER) {
                advanceInput();
            }
        });
    }

    @Override
    public void onShow() {
        if (!begun) {
            begun = true;
            presenter.begin(startDialogueId);
        }
        Platform.runLater(root::requestFocus);
    }

    @Override
    public void onHide() {
        stopTyping();
    }

    // ------------------------------------------------------------ chamados pelo FXML

    @FXML
    private void onDialogueClicked() {
        advanceInput();
    }

    @FXML
    private void onInventory() {
        presenter.onOpenInventory();
    }

    @FXML
    private void onCloseInventory() {
        presenter.onCloseInventory();
    }

    @FXML
    private void onSave() {
        presenter.onOpenSave();
    }

    @FXML
    private void onCloseSave() {
        presenter.onCloseSave();
    }

    @FXML
    private void onMenu() {
        presenter.onExitToMenu();
    }

    @FXML
    private void onStay() {
        confirmOverlay.setVisible(false);
        root.requestFocus();
    }

    @FXML
    private void onLeave() {
        navigator.resetTo(ScreenId.MAIN_MENU);
    }

    private void advanceInput() {
        if (anyOverlayVisible()) return;
        if (typingActive) {
            finishTyping();
        } else if (continueActive) {
            presenter.onContinue();
        }
    }

    private boolean anyOverlayVisible() {
        return inventoryOverlay.isVisible() || saveOverlay.isVisible()
                || endOverlay.isVisible() || confirmOverlay.isVisible();
    }

    // ------------------------------------------------------------ GameScreenView

    @Override
    public void showChapterTitle(int number, String title) {
        chapterBanner.setText("Capítulo " + number + "\n" + title);
        chapterBanner.setVisible(true);
        FadeTransition fade = new FadeTransition(Duration.seconds(3), chapterBanner);
        fade.setFromValue(1);
        fade.setToValue(0);
        fade.setOnFinished(e -> chapterBanner.setVisible(false));
        fade.play();
    }

    @Override
    public void setBackground(String resourcePath) {
        background.setImage(loadImage(resourcePath));
    }

    @Override
    public void setSprite(String resourcePath) {
        sprite.setImage(loadImage(resourcePath));
    }

    @Override
    public void showDialogue(String speaker, String text) {
        choicesBox.getChildren().clear();
        continueHint.setVisible(false);
        continueActive = false;
        afterText = null;
        speakerLabel.setText(speaker == null ? "" : speaker);
        speakerLabel.setVisible(speaker != null);
        speakerLabel.setManaged(speaker != null);
        startTyping(text == null ? "" : text);
    }

    @Override
    public void showContinuePrompt() {
        afterText(() -> {
            continueActive = true;
            continueHint.setVisible(true);
        });
    }

    @Override
    public void showChoices(List<ChoiceView> choices) {
        afterText(() -> {
            choicesBox.getChildren().clear();
            for (ChoiceView choice : choices) {
                String label = choice.available() ? choice.text()
                        : choice.text() + " " + (choice.blockReason() == null ? "" : choice.blockReason());
                Button button = new Button(label);
                button.getStyleClass().add("choice-button");
                button.setMaxWidth(Double.MAX_VALUE);
                button.setWrapText(true);
                button.setDisable(!choice.available());
                int index = choice.index();
                button.setOnAction(e -> presenter.onChoice(index));
                choicesBox.getChildren().add(button);
            }
        });
    }

    @Override
    public void showHud(int energy, double money) {
        energyLabel.setText("Energia: " + energy + "%");
        moneyLabel.setText(String.format("R$ %.2f", money));
    }

    @Override
    public void showToast(String message) {
        Label toast = new Label(message);
        toast.getStyleClass().add("toast");
        toast.setWrapText(true);
        toast.setMaxWidth(380);
        toastBox.getChildren().add(toast);
        PauseTransition wait = new PauseTransition(Duration.seconds(3.5));
        wait.setOnFinished(e -> toastBox.getChildren().remove(toast));
        wait.play();
    }

    @Override
    public void showInventory(List<InventoryEntry> entries) {
        inventoryList.getChildren().clear();
        if (entries.isEmpty()) {
            Label empty = new Label("Nenhum item no inventário.");
            empty.getStyleClass().add("text");
            inventoryList.getChildren().add(empty);
        }
        for (InventoryEntry entry : entries) {
            Node icon;
            Image image = loadImage(entry.imagePath());
            if (image != null) {
                ImageView view = new ImageView(image);
                view.setFitWidth(64);
                view.setFitHeight(64);
                view.setPreserveRatio(true);
                icon = view;
            } else {
                Region placeholder = new Region();
                placeholder.setMinSize(64, 64);
                placeholder.getStyleClass().add("item-placeholder");
                icon = placeholder;
            }
            Label name = new Label(entry.name());
            name.getStyleClass().add("row-title");
            Label description = new Label(entry.description());
            description.getStyleClass().add("text");
            description.setWrapText(true);
            HBox row = new HBox(14, icon, new VBox(4, name, description));
            row.setAlignment(Pos.CENTER_LEFT);
            inventoryList.getChildren().add(row);
        }
        inventoryOverlay.setVisible(true);
    }

    @Override
    public void hideInventory() {
        inventoryOverlay.setVisible(false);
        root.requestFocus();
    }

    @Override
    public void showSaveSlots(List<String> slotLabels) {
        saveSlotsBox.getChildren().clear();
        for (int i = 0; i < slotLabels.size(); i++) {
            int slot = i + 1;
            Button button = new Button(slotLabels.get(i));
            button.getStyleClass().add("menu-button");
            button.setMaxWidth(Double.MAX_VALUE);
            button.setOnAction(e -> presenter.onSaveToSlot(slot));
            saveSlotsBox.getChildren().add(button);
        }
        saveOverlay.setVisible(true);
    }

    @Override
    public void hideSaveSlots() {
        saveOverlay.setVisible(false);
        root.requestFocus();
    }

    @Override
    public void showEnding(Ending ending) {
        endTitle.setText("Final: " + ending.getTitle());
        endText.setText(ending.getDescription());
        endOverlay.setVisible(true);
    }

    @Override
    public void showChapterEnd() {
        endTitle.setText("Fim do capítulo");
        endText.setText("A história continua em breve.");
        endOverlay.setVisible(true);
    }

    @Override
    public void showError(String message) {
        endTitle.setText("Aviso");
        endText.setText(message);
        endOverlay.setVisible(true);
    }

    @Override
    public void exitToMenu() {
        confirmOverlay.setVisible(true);
    }

    // ------------------------------------------------------------ apoio

    private void afterText(Runnable action) {
        if (typingActive) {
            afterText = action;
        } else {
            action.run();
        }
    }

    private void startTyping(String text) {
        stopTyping();
        fullText = text;
        if (textSpeedMs <= 0 || text.isEmpty()) {
            textLabel.setText(text);
            typingActive = false;
            return;
        }
        textLabel.setText("");
        typingActive = true;
        int[] shown = {0};
        typing = new Timeline(new KeyFrame(Duration.millis(textSpeedMs), e -> {
            shown[0]++;
            textLabel.setText(fullText.substring(0, shown[0]));
        }));
        typing.setCycleCount(text.length());
        typing.setOnFinished(e -> finishTyping());
        typing.play();
    }

    private void finishTyping() {
        stopTyping();
        textLabel.setText(fullText);
        typingActive = false;
        if (afterText != null) {
            Runnable action = afterText;
            afterText = null;
            action.run();
        }
    }

    private void stopTyping() {
        if (typing != null) {
            typing.stop();
            typing = null;
        }
    }

    /** Carrega imagem do classpath; nulo se o caminho for nulo ou o arquivo não existir. */
    private Image loadImage(String resourcePath) {
        if (resourcePath == null) return null;
        try (InputStream in = GameController.class.getResourceAsStream(resourcePath)) {
            return in == null ? null : new Image(in);
        } catch (Exception e) {
            return null;
        }
    }
}
