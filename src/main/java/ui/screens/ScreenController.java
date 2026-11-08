package ui.screens;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import main.AppContext;
import ui.navigation.Screen;
import ui.navigation.ScreenNavigator;

import java.io.IOException;
import java.net.URL;

/**
 * Base das telas feitas em FXML. Cada tela é um par: um arquivo {@code .fxml} (o visual, que você
 * edita à mão ou no Scene Builder) e uma subclasse desta (o comportamento: o que cada botão faz).
 *
 * Para criar uma tela nova: copie um {@code .fxml} existente, crie a subclasse com os campos
 * {@code @FXML} e os métodos que o FXML chama, e registre no {@code App}. O passo a passo está em
 * {@code docs/GUIA_FXML.md}.
 */
public abstract class ScreenController implements Screen {
    protected final ScreenNavigator navigator;
    protected final AppContext context;
    private Parent root;

    protected ScreenController(ScreenNavigator navigator, AppContext context) {
        this.navigator = navigator;
        this.context = context;
    }

    @Override
    public Parent view() {
        return root;
    }

    /**
     * Carrega o FXML e liga os campos {@code @FXML} e os métodos {@code onAction="#..."} ao
     * {@code controller} informado.
     *
     * @param fxmlPath caminho dentro de src/main/resources, ex.: {@code /fxml/main_menu.fxml}
     */
    protected static <C extends ScreenController> C load(String fxmlPath, C controller) {
        URL url = ScreenController.class.getResource(fxmlPath);
        if (url == null) {
            throw new IllegalStateException("Arquivo FXML não encontrado: " + fxmlPath
                    + " (deve estar em src/main/resources" + fxmlPath + ")");
        }
        FXMLLoader loader = new FXMLLoader(url);
        loader.setControllerFactory(type -> controller); // usa o objeto já criado, com navigator/context
        try {
            ((ScreenController) controller).root = loader.load();
        } catch (IOException e) {
            throw new IllegalStateException("Erro ao carregar " + fxmlPath + ": " + e.getMessage(), e);
        }
        return controller;
    }
}
