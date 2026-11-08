package ui.game;

import model.Ending;
import service.DialogueSnapshot.ChoiceView;
import service.InventoryService.InventoryEntry;

import java.util.List;

/**
 * O que a tela do jogo sabe desenhar. O {@link GameScreenPresenter} decide O QUE mostrar e esta
 * interface é COMO: a implementação real usa JavaFX ({@code GameScreen}); os testes usam uma falsa.
 * Imagens chegam como caminhos de recurso do classpath (nulo = sem imagem).
 */
public interface GameScreenView {
    void showChapterTitle(int number, String title);

    void setBackground(String resourcePath);

    /** Sprite de quem fala; {@code resourcePath} nulo esconde o sprite. */
    void setSprite(String resourcePath);

    /** {@code speaker} é nulo para narração. */
    void showDialogue(String speaker, String text);

    /** Diálogo linear: só um "continuar" (clique/ENTER). */
    void showContinuePrompt();

    void showChoices(List<ChoiceView> choices);

    void showHud(int energy, double money);

    /** Aviso rápido (conquista, consequência, escolha bloqueada, jogo salvo...). */
    void showToast(String message);

    void showInventory(List<InventoryEntry> entries);

    void hideInventory();

    void showSaveSlots(List<String> slotLabels);

    void hideSaveSlots();

    void showEnding(Ending ending);

    void showChapterEnd();

    void showError(String message);

    void exitToMenu();
}
