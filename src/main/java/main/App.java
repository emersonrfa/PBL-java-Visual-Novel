package main;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import model.Player;
import ui.game.GameScreen;
import ui.navigation.NavigationModel;
import ui.navigation.ScreenId;
import ui.navigation.ScreenNavigator;

/**
 * Ponto de entrada da interface gráfica (JavaFX). Monta o {@link ScreenNavigator} e registra as telas.
 *
 * Por enquanto só existe a tela do jogo, então a janela já abre num Novo Jogo (Capítulo 1) para
 * testar tudo de ponta a ponta; as telas de menu serão registradas aqui e a abertura passará a ser
 * {@code ScreenId.MAIN_MENU}. O jogo em console continua em {@link Main}.
 *
 * Rodar: {@code mvn javafx:run}.
 */
public class App extends Application {

    @Override
    public void start(Stage stage) {
        AppContext context = new AppContext();
        ScreenNavigator navigator = new ScreenNavigator(new NavigationModel());

        navigator.register(ScreenId.GAME, () -> new GameScreen(
                context.createGameSession(new Player("O+"), 1, null),
                context.getStateRepository(), context.getConfig(),
                null, Platform::exit));

        stage.setTitle("Visual Novel");
        stage.setScene(new Scene(navigator.root(), 1280, 720));
        stage.show();
        navigator.goTo(ScreenId.GAME);
    }
}
