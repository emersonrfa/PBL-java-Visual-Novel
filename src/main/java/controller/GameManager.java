package controller;

import model.Chapter;
import model.Ending;
import model.GameStates;
import repository.ChapterCatalog;
import view.MenuView;

import java.util.ArrayList;
import java.util.List;

public class GameManager {
    private static final int FIRST_CHAPTER = 1;

    private boolean isRunning;
    private final DialogueController dialogueController;
    private final MenuController menuController;
    private final ChapterCatalog chapterCatalog;
    private final EndingManager endingManager;
    private final GameStates currentGameState;
    private final MenuView menuView = new MenuView();

    /** Criado por main.AppContext, que injeta as instâncias compartilhadas. */
    public GameManager(DialogueController dialogueController,
                       MenuController menuController,
                       ChapterCatalog chapterCatalog,
                       EndingManager endingManager,
                       GameStates currentGameState) {
        this.dialogueController = dialogueController;
        this.menuController = menuController;
        this.chapterCatalog = chapterCatalog;
        this.endingManager = endingManager;
        this.currentGameState = currentGameState;
    }

    /**
     * Novo Jogo: vai apenas para o Capítulo 1 inicial.
     */
    public void startGame() {
        this.isRunning = true;
        menuView.printMessage("\nCarregando Capítulo " + FIRST_CHAPTER + "...");
        dialogueController.playChapter(chapterCatalog.get(FIRST_CHAPTER));
        this.isRunning = false;
    }

    /**
     * Carrega o jogo a partir do capítulo e diálogo salvos, sem cair na seleção de capítulos ao sair.
     */
    public void loadAndStartGame() {
        this.isRunning = true;

        if (this.currentGameState == null) {
            menuView.printMessage("\nNenhum save encontrado. Iniciando novo jogo...");
            startGame();
            return;
        }

        int savedChapter = this.currentGameState.getCurrentChapterId();
        String savedDialogId = this.currentGameState.getCurrentDialogId();

        menuView.printMessage("\nRetomando a partida no Capítulo " + savedChapter + " [Diálogo: " + savedDialogId + "]...");

        Chapter chapter = chapterCatalog.get(savedChapter);
        if (chapter != null) {
            dialogueController.playChapter(chapter, savedDialogId);
        } else {
            menuView.printMessage("\nCapítulo desconhecido no save (" + savedChapter + ").");
        }

        this.isRunning = false;
    }

    /**
     * Menu de seleção de capítulos (acessível apenas ao digitar ADMIN no Menu Inicial).
     */
    public void gameLoop() {
        this.isRunning = true;
        while (isRunning) {
            int choice = menuController.chapterSelection(chapterCatalog.titles());

            if (choice == 0) {
                menuView.printMessage("\nVoltando para o Menu Principal...");
                isRunning = false;
            } else if (choice == MenuController.ENDINGS_GALLERY_OPTION) {
                endingGalleryLoop();
            } else {
                Chapter chapter = chapterCatalog.get(choice);
                if (chapter != null) {
                    menuView.printMessage("\nCarregando capítulo " + choice + "...");
                    dialogueController.playChapter(chapter);
                }
            }
        }
    }

    private void endingGalleryLoop() {
        boolean inGallery = true;
        List<Ending> allEndings = endingManager.getAllEndings();

        List<String> titles = new ArrayList<>();
        for (Ending e : allEndings) {
            titles.add(e.getTitle());
        }

        while (inGallery) {
            int choice = menuController.endingSelection(titles);

            if (choice == 0) {
                inGallery = false;
            } else {
                Ending selected = allEndings.get(choice - 1);
                endingManager.triggerEnding(selected.getId());

                menuView.printMessage("\n[Pressione ENTER para voltar à galeria]");
                dialogueController.getInputManager().waitEnter();
            }
        }
    }
}
