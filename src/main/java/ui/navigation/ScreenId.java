package ui.navigation;

/** Telas do jogo. Cada uma é registrada no ScreenNavigator com a sua fábrica. */
public enum ScreenId {
    MAIN_MENU,
    PLAY,          // escolha entre Novo Jogo e Carregar Save
    NEW_GAME,      // slot + tipo sanguíneo
    LOAD_SAVE,
    SETTINGS,
    ACHIEVEMENTS,
    ENDINGS,
    CREDITS,
    GAME           // a tela única de diálogo/capítulos
}
