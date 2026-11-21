package ui.effects;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;

import java.net.URL;

/**
 * Janela de confirmação para ações sem volta (excluir slot, sobrescrever save, sair do jogo).
 * Usa o tema do jogo (css/style.css) e devolve {@code true} só se o jogador confirmar.
 */
public final class ConfirmDialog {
    private ConfirmDialog() {}

    /**
     * @param owner       qualquer nó da tela atual (a janela abre por cima dela); pode ser nulo
     * @param title       pergunta curta, ex.: "Excluir o Slot 2?"
     * @param message     consequências da ação
     * @param confirmText texto do botão que confirma, ex.: "Excluir"
     */
    public static boolean ask(Node owner, String title, String message, String confirmText) {
        ButtonType yes = new ButtonType(confirmText, ButtonBar.ButtonData.OK_DONE);
        ButtonType no = new ButtonType("Cancelar", ButtonBar.ButtonData.CANCEL_CLOSE);
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION, message, yes, no);
        alert.setTitle(title);
        alert.setHeaderText(title);
        alert.setGraphic(null);
        if (owner != null && owner.getScene() != null && owner.getScene().getWindow() != null) {
            alert.initOwner(owner.getScene().getWindow());
        }
        DialogPane pane = alert.getDialogPane();
        URL css = ConfirmDialog.class.getResource("/css/style.css");
        if (css != null) {
            pane.getStylesheets().add(css.toExternalForm());
        }
        pane.getStyleClass().add("confirm-dialog");
        return alert.showAndWait().filter(button -> button == yes).isPresent();
    }
}
