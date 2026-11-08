package ui.navigation;

import javafx.scene.Parent;

/** Uma tela do jogo (menu, configurações, jogo...). O ScreenNavigator cria uma nova a cada visita. */
public interface Screen {
    /** O conteúdo visual da tela. */
    Parent view();

    /** Chamado depois que a tela entrou na janela. */
    default void onShow() {}

    /** Chamado quando a tela vai sair; pare timers/animações aqui. */
    default void onHide() {}
}
