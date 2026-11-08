package main;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

/**
 * Ponto de entrada da interface gráfica (JavaFX). Por enquanto só abre uma janela vazia para
 * confirmar que o JavaFX está configurado; as telas reais (menu, jogo, ...) entram a partir daqui,
 * usando o {@link AppContext} e o {@link service.GameSession}.
 *
 * Rodar: {@code mvn javafx:run}. O jogo em console continua em {@link Main}.
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        Label hello = new Label("Visual Novel — JavaFX configurado");
        StackPane root = new StackPane(hello);
        root.setAlignment(Pos.CENTER);

        stage.setTitle("Visual Novel");
        stage.setScene(new Scene(root, 1280, 720));
        stage.show();
    }
}
