package ui.game;

import model.GameStates;
import model.Player;

/**
 * O que a tela do jogo precisa para começar: quem joga e de onde. Vem do Novo Jogo ou do Carregar Save.
 *
 * @param state estado salvo (afinidades, capítulo, diálogo); nunca nulo
 */
public record GameRequest(Player player, GameStates state) {
    public int chapterNumber() {
        return state.getCurrentChapterId();
    }

    public String dialogueId() {
        return state.getCurrentDialogId();
    }
}
