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
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Polygon;
import javafx.util.Duration;
import main.AppContext;
import model.Ending;
import model.GameConfig;
import service.DialogueSnapshot.ChoiceView;
import service.GameSession;
import service.InventoryService.InventoryEntry;
import service.StatusService.MissionStatus;
import service.StatusService.NpcStatus;
import service.StatusService.PlayerStatus;
import ui.effects.ConfirmDialog;
import ui.effects.Sounds;
import ui.navigation.ScreenId;
import ui.navigation.ScreenNavigator;
import ui.screens.ScreenController;

import java.io.InputStream;
import java.util.List;
import java.util.Locale;

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
    @FXML private VBox achievementBox;
    @FXML private Label chapterBanner;
    @FXML private StackPane inventoryOverlay;
    @FXML private VBox inventoryList;
    @FXML private StackPane statusOverlay;
    @FXML private VBox statusContent;
    @FXML private StackPane saveOverlay;
    @FXML private VBox saveSlotsBox;
    @FXML private StackPane endOverlay;
    @FXML private Label endTitle;
    @FXML private Label endText;
    @FXML private StackPane confirmOverlay;

    private final String startDialogueId;
    private final int textSpeedMs;
    private GameScreenPresenter presenter;
    private boolean leaveAfterSave;   // "Salvar e sair": depois de salvar com sucesso, volta ao menu

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
            // Depois do primeiro layout: a tela já aparece inteira (HUD, caixa de diálogo) e, se algo falhar
            // ao montar o diálogo, o erro vira uma mensagem em vez de deixar a janela travada.
            Platform.runLater(this::beginGame);
        }
        Platform.runLater(root::requestFocus);
    }

    private void beginGame() {
        try {
            presenter.begin(startDialogueId);
        } catch (RuntimeException e) {
            e.printStackTrace();
            showError("Não foi possível abrir esta partida: " + e);
        }
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
    private void onStatus() {
        presenter.onOpenStatus();
    }

    @FXML
    private void onCloseStatus() {
        presenter.onCloseStatus();
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
    private void onSaveAndLeave() {
        confirmOverlay.setVisible(false);
        leaveAfterSave = true;
        presenter.onOpenSave();
    }

    @FXML
    private void onCloseSave() {
        leaveAfterSave = false;
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
        return inventoryOverlay.isVisible() || statusOverlay.isVisible() || saveOverlay.isVisible()
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
    public void showAchievement(String title, String description) {
        Polygon star = new Polygon();
        for (int i = 0; i < 10; i++) { // estrela de 5 pontas desenhada em código (sem depender de fonte)
            double r = i % 2 == 0 ? 17 : 7.5;
            double a = Math.toRadians(-90 + i * 36);
            star.getPoints().addAll(20 + r * Math.cos(a), 20 + r * Math.sin(a));
        }
        star.getStyleClass().add("achievement-star");
        StackPane badge = new StackPane(star);
        badge.setMinSize(46, 46);
        badge.setMaxSize(46, 46);
        badge.getStyleClass().add("achievement-badge");

        Label kicker = new Label("CONQUISTA DESBLOQUEADA");
        kicker.getStyleClass().add("achievement-kicker");
        Label name = new Label(title);
        name.getStyleClass().add("achievement-title");
        Label detail = new Label(description == null ? "" : description);
        detail.getStyleClass().add("achievement-text");
        detail.setWrapText(true);
        detail.setMaxWidth(340);

        HBox banner = new HBox(14, badge, new VBox(2, kicker, name, detail));
        banner.setAlignment(Pos.CENTER_LEFT);
        banner.getStyleClass().add("achievement-banner");
        banner.setMaxWidth(Region.USE_PREF_SIZE);
        banner.setOpacity(0);
        banner.setTranslateY(-30);
        achievementBox.getChildren().add(banner);
        Sounds.play(Sounds.Effect.ACHIEVEMENT);

        // entra deslizando, fica um tempo e some
        FadeTransition in = new FadeTransition(Duration.millis(350), banner);
        in.setToValue(1);
        javafx.animation.TranslateTransition slide = new javafx.animation.TranslateTransition(Duration.millis(350), banner);
        slide.setToY(0);
        PauseTransition hold = new PauseTransition(Duration.seconds(4.2));
        FadeTransition out = new FadeTransition(Duration.millis(600), banner);
        out.setToValue(0);
        out.setOnFinished(e -> achievementBox.getChildren().remove(banner));
        new javafx.animation.SequentialTransition(new javafx.animation.ParallelTransition(in, slide), hold, out).play();
    }

    @Override
    public void showInventory(List<InventoryEntry> entries) {
        if (!inventoryOverlay.isVisible()) {
            Sounds.play(Sounds.Effect.BAG_OPEN); // só ao abrir (não toca quando a lista é atualizada)
        }
        inventoryList.getChildren().clear();
        if (entries.isEmpty()) {
            Label empty = new Label("A mochila está vazia.");
            empty.getStyleClass().add("bag-item-text");
            inventoryList.getChildren().add(empty);
        }
        for (InventoryEntry entry : entries) {
            Node icon;
            Image image = loadImage(entry.imagePath());
            if (image != null) {
                ImageView view = new ImageView(image);
                view.setFitWidth(60);
                view.setFitHeight(60);
                view.setPreserveRatio(true);
                view.setSmooth(true);
                icon = new StackPane(view);
                icon.getStyleClass().add("item-slot");
            } else {
                Region placeholder = new Region();
                placeholder.setMinSize(64, 64);
                placeholder.getStyleClass().add("item-placeholder");
                icon = placeholder;
            }
            Label name = new Label(entry.name());
            name.getStyleClass().add("bag-item-name");
            Label description = new Label(entry.description());
            description.getStyleClass().add("bag-item-text");
            description.setWrapText(true);
            HBox row = new HBox(14, icon, new VBox(4, name, description));
            row.getStyleClass().add("bag-row");
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
    public void showStatus(PlayerStatus status) {
        statusContent.getChildren().clear();

        Label attrs = new Label(String.format(Locale.forLanguageTag("pt-BR"),
                "Capítulo %d   •   Energia %d/100   •   R$ %.2f   •   Sangue %s",
                status.chapter(), status.energy(), status.money(), status.bloodType()));
        attrs.getStyleClass().add("text");
        statusContent.getChildren().add(attrs);

        addStatusSection("Relações");
        for (NpcStatus npc : status.npcs()) {
            statusContent.getChildren().add(npcRow(npc));
        }

        addStatusSection("Missões");
        for (MissionStatus mission : status.missions()) {
            Label label = new Label((mission.done() ? "✔  " : "○  ") + mission.title());
            label.getStyleClass().add(mission.done() ? "status-done" : "text");
            statusContent.getChildren().add(label);
        }

        addStatusSection("Decisões");
        if (status.decisions().isEmpty()) {
            Label none = new Label("Nenhuma decisão marcante ainda.");
            none.getStyleClass().add("text-muted");
            statusContent.getChildren().add(none);
        }
        for (String decision : status.decisions()) {
            Label label = new Label("•  " + decision);
            label.getStyleClass().add("text");
            statusContent.getChildren().add(label);
        }

        if (!statusOverlay.isVisible()) {
            Sounds.play(Sounds.Effect.CLICK);
        }
        statusOverlay.setVisible(true);
    }

    private void addStatusSection(String title) {
        Label label = new Label(title);
        label.getStyleClass().add("status-section");
        statusContent.getChildren().add(label);
    }

    /** Uma linha de NPC: nome + papel, barra de afinidade (−20..100) e, nos vampiros, barra de suspeita. */
    private VBox npcRow(NpcStatus npc) {
        Label name = new Label(npc.name() + "  —  " + npc.role());
        name.getStyleClass().add(npc.known() ? "text" : "text-muted");
        VBox box = new VBox(4, name);
        if (npc.known()) {
            box.getChildren().add(statBar("Afinidade", (npc.affinity() + 20) / 120.0,
                    String.format("%+d", npc.affinity()), "affinity-bar"));
            if (npc.vampire()) {
                box.getChildren().add(statBar("Suspeita", npc.suspicion() / (double) Math.max(1, npc.suspicionLimit()),
                        npc.suspicion() + "/" + npc.suspicionLimit(), "suspicion-bar"));
            }
        }
        box.getStyleClass().add("status-npc");
        return box;
    }

    private HBox statBar(String title, double fraction, String value, String styleClass) {
        Label label = new Label(title);
        label.getStyleClass().add("status-stat");
        label.setMinWidth(80);
        ProgressBar bar = new ProgressBar(Math.max(0, Math.min(1, fraction)));
        bar.getStyleClass().add(styleClass);
        bar.setPrefWidth(300);
        Label number = new Label(value);
        number.getStyleClass().add("status-stat");
        HBox row = new HBox(10, label, bar, number);
        row.setAlignment(Pos.CENTER_LEFT);
        return row;
    }

    @Override
    public void hideStatus() {
        statusOverlay.setVisible(false);
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
            boolean occupied = !slotLabels.get(i).endsWith("vazio");
            button.setOnAction(e -> {
                if (occupied && !ConfirmDialog.ask(root, "Sobrescrever o Slot " + slot + "?",
                        "O save atual desse slot será substituído.", "Sobrescrever")) {
                    return;
                }
                boolean saved = presenter.onSaveToSlot(slot);
                if (saved && leaveAfterSave) {
                    leaveAfterSave = false;
                    onLeave();
                }
            });
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
            if (!Character.isWhitespace(fullText.charAt(shown[0] - 1))) {
                Sounds.typeTick(55); // tique grave e baixo, no máximo ~18 por segundo
            }
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
