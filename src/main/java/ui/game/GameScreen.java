package ui.game;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import model.Ending;
import model.GameConfig;
import repository.StateRepository;
import service.DialogueSnapshot.ChoiceView;
import service.GameSession;
import service.InventoryService.InventoryEntry;
import ui.navigation.Screen;

import java.io.InputStream;
import java.util.List;

/**
 * A tela única do jogo: fundo, sprite, caixa de diálogo com escolhas, HUD, inventário (só leitura)
 * e salvar. Toda a lógica está no {@link GameScreenPresenter}; esta classe só desenha o que ele pede
 * e repassa cliques/teclas. Imagens ausentes simplesmente não aparecem.
 *
 * Entradas: clique na caixa de diálogo, ESPAÇO ou ENTER = continuar (ou completar o texto que
 * está sendo digitado).
 */
public class GameScreen implements Screen, GameScreenView {
    private final StackPane root = new StackPane();
    private final ImageView background = new ImageView();
    private final ImageView sprite = new ImageView();
    private final Label energyLabel = new Label();
    private final Label moneyLabel = new Label();
    private final Label speakerLabel = new Label();
    private final Label textLabel = new Label();
    private final Label continueHint = new Label("▼ continuar");
    private final VBox choicesBox = new VBox(8);
    private final VBox toastBox = new VBox(6);
    private final Label chapterBanner = new Label();
    private final StackPane modalLayer = new StackPane();

    private final GameScreenPresenter presenter;
    private final int textSpeedMs;
    private final String startDialogueId;
    private final Runnable onExitToMenu;

    private Timeline typing;
    private String fullText = "";
    private boolean typingActive;
    private boolean continueActive;
    private Runnable afterText;
    private boolean begun;

    /**
     * @param startDialogueId onde começar (nulo = início do capítulo); usado também ao retomar um save
     * @param onExitToMenu    chamado quando o jogador pede para sair (menu, fim do capítulo/final)
     */
    public GameScreen(GameSession session, StateRepository stateRepository, GameConfig config,
                      String startDialogueId, Runnable onExitToMenu) {
        this.presenter = new GameScreenPresenter(session, this, stateRepository);
        this.textSpeedMs = config == null ? 0 : config.getTextSpeedMs();
        this.startDialogueId = startDialogueId;
        this.onExitToMenu = onExitToMenu;
        buildLayout();
    }

    @Override
    public Parent view() {
        return root;
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

    // ------------------------------------------------------------ montagem

    private void buildLayout() {
        root.setStyle("-fx-background-color: #141420;");

        background.setPreserveRatio(false);
        background.fitWidthProperty().bind(root.widthProperty());
        background.fitHeightProperty().bind(root.heightProperty());
        background.setMouseTransparent(true);

        sprite.setPreserveRatio(true);
        sprite.fitHeightProperty().bind(root.heightProperty());
        sprite.setMouseTransparent(true);
        StackPane.setAlignment(sprite, Pos.BOTTOM_CENTER);

        // HUD (topo)
        energyLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        moneyLabel.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        Region spacer = new Region();
        HBox.setHgrow(spacer, javafx.scene.layout.Priority.ALWAYS);
        Button inventoryButton = new Button("Inventário");
        inventoryButton.setOnAction(e -> presenter.onOpenInventory());
        Button saveButton = new Button("Salvar");
        saveButton.setOnAction(e -> presenter.onOpenSave());
        Button menuButton = new Button("Menu");
        menuButton.setOnAction(e -> presenter.onExitToMenu());
        HBox hud = new HBox(16, energyLabel, moneyLabel, spacer, inventoryButton, saveButton, menuButton);
        hud.setAlignment(Pos.CENTER_LEFT);
        hud.setPadding(new Insets(12, 20, 12, 20));
        hud.setStyle("-fx-background-color: rgba(0,0,0,0.45);");
        StackPane.setAlignment(hud, Pos.TOP_CENTER);
        hud.setMaxHeight(Region.USE_PREF_SIZE);

        // caixa de diálogo (base)
        speakerLabel.setStyle("-fx-text-fill: #ffd479; -fx-font-size: 20px; -fx-font-weight: bold;");
        textLabel.setStyle("-fx-text-fill: white; -fx-font-size: 20px;");
        textLabel.setWrapText(true);
        textLabel.setMinHeight(90);
        continueHint.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 14px;");
        continueHint.setVisible(false);
        choicesBox.setAlignment(Pos.CENTER_LEFT);
        VBox dialogueBox = new VBox(8, speakerLabel, textLabel, choicesBox, continueHint);
        dialogueBox.setPadding(new Insets(20, 40, 24, 40));
        dialogueBox.setStyle("-fx-background-color: rgba(10,10,20,0.85);");
        dialogueBox.setMaxHeight(Region.USE_PREF_SIZE);
        dialogueBox.setOnMouseClicked(e -> advanceInput());
        StackPane.setAlignment(dialogueBox, Pos.BOTTOM_CENTER);

        // avisos (canto superior direito, não bloqueiam cliques)
        toastBox.setAlignment(Pos.TOP_RIGHT);
        toastBox.setMouseTransparent(true);
        toastBox.setMaxWidth(Region.USE_PREF_SIZE);
        toastBox.setMaxHeight(Region.USE_PREF_SIZE);
        StackPane.setAlignment(toastBox, Pos.TOP_RIGHT);
        StackPane.setMargin(toastBox, new Insets(70, 20, 0, 0));

        // título de capítulo (centro)
        chapterBanner.setStyle("-fx-text-fill: white; -fx-font-size: 36px; -fx-font-weight: bold;"
                + " -fx-background-color: rgba(0,0,0,0.6); -fx-padding: 18 36 18 36;");
        chapterBanner.setVisible(false);
        chapterBanner.setMouseTransparent(true);
        chapterBanner.setMaxWidth(Region.USE_PREF_SIZE);
        chapterBanner.setMaxHeight(Region.USE_PREF_SIZE);

        // camada de janelas (inventário, salvar, final...)
        modalLayer.setStyle("-fx-background-color: rgba(0,0,0,0.7);");
        modalLayer.setVisible(false);

        root.getChildren().addAll(background, sprite, hud, dialogueBox, toastBox, chapterBanner, modalLayer);
        root.setFocusTraversable(true);
        root.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.SPACE || e.getCode() == KeyCode.ENTER) {
                advanceInput();
            }
        });
    }

    // ------------------------------------------------------------ entrada

    private void advanceInput() {
        if (modalLayer.isVisible()) return;
        if (typingActive) {
            finishTyping();
        } else if (continueActive) {
            presenter.onContinue();
        }
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
        toast.setWrapText(true);
        toast.setMaxWidth(380);
        toast.setStyle("-fx-text-fill: white; -fx-background-color: rgba(30,30,60,0.92); -fx-padding: 8 14 8 14;");
        toastBox.getChildren().add(toast);
        PauseTransition wait = new PauseTransition(Duration.seconds(3.5));
        wait.setOnFinished(e -> toastBox.getChildren().remove(toast));
        wait.play();
    }

    @Override
    public void showInventory(List<InventoryEntry> entries) {
        VBox list = new VBox(12);
        list.setPadding(new Insets(8));
        if (entries.isEmpty()) {
            list.getChildren().add(whiteLabel("Nenhum item no inventário."));
        }
        for (InventoryEntry entry : entries) {
            ImageView icon = new ImageView(loadImage(entry.imagePath()));
            icon.setFitWidth(64);
            icon.setFitHeight(64);
            icon.setPreserveRatio(true);
            Region placeholder = new Region();
            placeholder.setMinSize(64, 64);
            placeholder.setStyle("-fx-background-color: rgba(255,255,255,0.12);");
            Label name = whiteLabel(entry.name());
            name.setStyle("-fx-text-fill: #ffd479; -fx-font-size: 18px; -fx-font-weight: bold;");
            Label description = whiteLabel(entry.description());
            description.setWrapText(true);
            HBox row = new HBox(14, entry.imagePath() == null ? placeholder : icon, new VBox(4, name, description));
            row.setAlignment(Pos.CENTER_LEFT);
            list.getChildren().add(row);
        }
        ScrollPane scroll = new ScrollPane(list);
        scroll.setFitToWidth(true);
        scroll.setPrefHeight(360);
        Button close = new Button("Fechar");
        close.setOnAction(e -> presenter.onCloseInventory());
        showModal(panel("Inventário", scroll, close));
    }

    @Override
    public void hideInventory() {
        hideModal();
    }

    @Override
    public void showSaveSlots(List<String> slotLabels) {
        VBox slots = new VBox(8);
        for (int i = 0; i < slotLabels.size(); i++) {
            int slot = i + 1;
            Button button = new Button(slotLabels.get(i));
            button.setMaxWidth(Double.MAX_VALUE);
            button.setOnAction(e -> presenter.onSaveToSlot(slot));
            slots.getChildren().add(button);
        }
        Button cancel = new Button("Cancelar");
        cancel.setOnAction(e -> presenter.onCloseSave());
        showModal(panel("Salvar jogo", slots, cancel));
    }

    @Override
    public void hideSaveSlots() {
        hideModal();
    }

    @Override
    public void showEnding(Ending ending) {
        Label description = whiteLabel(ending.getDescription());
        description.setWrapText(true);
        Button back = new Button("Voltar ao menu");
        back.setOnAction(e -> leave());
        showModal(panel("Final: " + ending.getTitle(), description, back));
    }

    @Override
    public void showChapterEnd() {
        Button back = new Button("Voltar ao menu");
        back.setOnAction(e -> leave());
        showModal(panel("Fim do capítulo", whiteLabel("A história continua em breve."), back));
    }

    @Override
    public void showError(String message) {
        Label text = whiteLabel(message);
        text.setWrapText(true);
        Button back = new Button("Voltar ao menu");
        back.setOnAction(e -> leave());
        showModal(panel("Aviso", text, back));
    }

    @Override
    public void exitToMenu() {
        Button stay = new Button("Continuar jogando");
        stay.setOnAction(e -> hideModal());
        Button leave = new Button("Sair para o menu");
        leave.setOnAction(e -> leave());
        showModal(panel("Voltar ao menu?", whiteLabel("O progresso não salvo será perdido."), new HBox(10, stay, leave)));
    }

    // ------------------------------------------------------------ apoio

    private void leave() {
        if (onExitToMenu != null) onExitToMenu.run();
    }

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

    private void showModal(Node content) {
        modalLayer.getChildren().setAll(content);
        modalLayer.setVisible(true);
    }

    private void hideModal() {
        modalLayer.getChildren().clear();
        modalLayer.setVisible(false);
        root.requestFocus();
    }

    private VBox panel(String title, Node body, Node footer) {
        Label heading = whiteLabel(title);
        heading.setStyle("-fx-text-fill: white; -fx-font-size: 24px; -fx-font-weight: bold;");
        VBox panel = new VBox(16, heading, body, footer);
        panel.setPadding(new Insets(24));
        panel.setMaxWidth(640);
        panel.setMaxHeight(Region.USE_PREF_SIZE);
        panel.setStyle("-fx-background-color: #1d1d33;");
        StackPane.setAlignment(panel, Pos.CENTER);
        return panel;
    }

    private static Label whiteLabel(String text) {
        Label label = new Label(text);
        label.setStyle("-fx-text-fill: white; -fx-font-size: 16px;");
        return label;
    }

    /** Carrega imagem do classpath; nulo se o caminho for nulo ou o arquivo não existir. */
    private Image loadImage(String resourcePath) {
        if (resourcePath == null) return null;
        try (InputStream in = GameScreen.class.getResourceAsStream(resourcePath)) {
            return in == null ? null : new Image(in);
        } catch (Exception e) {
            return null;
        }
    }
}
