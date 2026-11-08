package main;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import ui.game.GameController;
import ui.game.GameRequest;
import ui.navigation.NavigationModel;
import ui.navigation.ScreenId;
import ui.navigation.ScreenNavigator;
import ui.screens.AchievementsController;
import ui.screens.CreditsController;
import ui.screens.EndingsController;
import ui.screens.LoadSaveController;
import ui.screens.MainMenuController;
import ui.screens.NewGameController;
import ui.screens.PlayController;
import ui.screens.SettingsController;

import java.net.URL;

/**
 * Ponto de entrada da interface gráfica (JavaFX). Cria o contexto da aplicação, registra todas as
 * telas no {@link ScreenNavigator} e abre o Menu Inicial. O jogo em console continua em {@link Main}.
 *
 * Para acrescentar uma tela: crie o .fxml e o controller (veja docs/GUIA_FXML.md), adicione um
 * valor em {@link ScreenId} e uma linha de {@code register} abaixo.
 *
 * Rodar: {@code mvn javafx:run}.
 */
public class App extends Application {
    private AppContext context;

    @Override
    public void start(Stage stage) {
        context = new AppContext();
        ScreenNavigator navigator = new ScreenNavigator(new NavigationModel());

        navigator.register(ScreenId.MAIN_MENU, () -> MainMenuController.create(navigator, context))
                .register(ScreenId.PLAY, () -> PlayController.create(navigator, context))
                .register(ScreenId.NEW_GAME, () -> NewGameController.create(navigator, context))
                .register(ScreenId.LOAD_SAVE, () -> LoadSaveController.create(navigator, context))
                .register(ScreenId.SETTINGS, () -> SettingsController.create(navigator, context))
                .register(ScreenId.ACHIEVEMENTS, () -> AchievementsController.create(navigator, context))
                .register(ScreenId.ENDINGS, () -> EndingsController.create(navigator, context))
                .register(ScreenId.CREDITS, () -> CreditsController.create(navigator, context))
                .registerWithArgument(ScreenId.GAME,
                        request -> GameController.create(navigator, context, (GameRequest) request));

        Scene scene = new Scene(navigator.root(), 1280, 720);
        URL stylesheet = App.class.getResource("/css/style.css");
        if (stylesheet != null) {
            scene.getStylesheets().add(stylesheet.toExternalForm());
        }

        stage.setTitle("Visual Novel");
        stage.setScene(scene);
        stage.show();
        navigator.goTo(ScreenId.MAIN_MENU);
    }

    /** Ao fechar a janela, arquiva o autosave num slot vazio (igual ao "Sair" do console). */
    @Override
    public void stop() {
        if (context != null) {
            int slot = context.getStateRepository().archiveAutoSaveToEmptySlot();
            if (slot != -1) {
                System.out.println("[Backup] Progresso recente arquivado no Slot " + slot + ".");
            }
        }
    }
}
