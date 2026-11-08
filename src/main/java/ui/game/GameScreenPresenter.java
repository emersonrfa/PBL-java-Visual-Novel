package ui.game;

import event.Observer;
import exception.GamePersistenceException;
import model.GameStates;
import model.Protagonist;
import repository.StateRepository;
import service.DialogueSnapshot;
import service.GameEvent;
import service.GameSession;
import service.InventoryService;
import service.StepResult;

import java.util.ArrayList;
import java.util.List;

/**
 * Cérebro da tela única do jogo (padrão MVP): traduz cliques em chamadas ao {@link GameSession} e
 * o {@link StepResult} em chamadas à {@link GameScreenView}. Não conhece JavaFX.
 *
 * Também é um Observer do GameSession: o HUD e os avisos reagem aos eventos (status mudou,
 * conquista, jogo salvo) sem que o fluxo principal precise lembrar de atualizá-los.
 */
public class GameScreenPresenter implements Observer<GameEvent> {
    private static final int SLOTS = 4;

    private final GameSession session;
    private final GameScreenView view;
    private final StateRepository stateRepository;
    private boolean inventoryOpen;
    private boolean finished;

    public GameScreenPresenter(GameSession session, GameScreenView view, StateRepository stateRepository) {
        this.session = session;
        this.view = view;
        this.stateRepository = stateRepository;
        session.addObserver(this);
    }

    /** Começa a partida (ou retoma o save) no diálogo informado; nulo = início do capítulo. */
    public void begin(String dialogueId) {
        finished = false;
        showHud();
        render(session.start(dialogueId));
    }

    /** Clique/ENTER num diálogo linear. */
    public void onContinue() {
        if (finished || session.isOver() || inventoryOpen) return;
        render(session.advance());
    }

    /** Clique numa opção (índice base 0). */
    public void onChoice(int index) {
        if (finished || session.isOver() || inventoryOpen) return;
        render(session.choose(index));
    }

    public void onOpenInventory() {
        if (finished) return;
        inventoryOpen = true;
        view.showInventory(InventoryService.entries(session.getPlayer(), session.getAssets()));
    }

    public void onCloseInventory() {
        inventoryOpen = false;
        view.hideInventory();
    }

    public void onOpenSave() {
        if (finished || session.isOver()) return;
        List<String> labels = new ArrayList<>();
        for (int slot = 1; slot <= SLOTS; slot++) {
            labels.add(describeSlot(slot));
        }
        view.showSaveSlots(labels);
    }

    public void onCloseSave() {
        view.hideSaveSlots();
    }

    /** Salva no slot (1–4); o aviso de sucesso vem pelo evento GameSaved. */
    public void onSaveToSlot(int slot) {
        try {
            session.saveToSlot(slot);
        } catch (GamePersistenceException e) {
            view.showToast("Erro ao salvar: " + e.getMessage());
        }
        view.hideSaveSlots();
    }

    public void onExitToMenu() {
        view.exitToMenu();
    }

    // ------------------------------------------------------------ eventos do GameSession

    @Override
    public void onEvent(GameEvent event) {
        if (event instanceof GameEvent.StatsChanged stats) {
            view.showHud(stats.energy(), stats.money());
        } else if (event instanceof GameEvent.AchievementUnlocked unlocked) {
            view.showToast("Conquista desbloqueada: " + unlocked.achievement().getTitle());
        } else if (event instanceof GameEvent.ChoiceApplied applied) {
            applied.report().messages().forEach(view::showToast);
        } else if (event instanceof GameEvent.GameSaved saved) {
            view.showToast("Jogo salvo no Slot " + saved.slot() + "!");
        } else if (event instanceof GameEvent.InventoryChanged && inventoryOpen) {
            view.showInventory(InventoryService.entries(session.getPlayer(), session.getAssets()));
        }
    }

    // ------------------------------------------------------------ desenho

    private void render(StepResult step) {
        DialogueSnapshot snapshot = step.snapshot();
        if (snapshot != null && step.status() != StepResult.Status.BLOCKED) { // BLOCKED: a tela já mostra esse diálogo
            if (snapshot.chapterChanged()) {
                view.showChapterTitle(snapshot.chapterNumber(), snapshot.chapterTitle());
            }
            if (snapshot.sceneChanged()) {
                view.setBackground(snapshot.background());
            }
            view.setSprite(snapshot.sprite());
            view.showDialogue(snapshot.narration() ? null : Protagonist.canonicalName(snapshot.speaker()), snapshot.text());
            if (snapshot.linear()) {
                view.showContinuePrompt();
            } else {
                view.showChoices(snapshot.choices());
            }
        }

        switch (step.status()) {
            case BLOCKED -> view.showToast(blockedMessage(step.message()));
            case ENDING -> {
                finished = true;
                if (step.ending().found()) {
                    view.showEnding(step.ending().ending());
                } else {
                    view.showError("Final não encontrado: " + step.ending().endingId());
                }
            }
            case CHAPTER_END -> {
                finished = true;
                view.showChapterEnd();
            }
            case ERROR -> {
                finished = true;
                view.showError(step.message());
            }
            default -> { }
        }
    }

    private static String blockedMessage(String reason) {
        String base = "Você não cumpre os requisitos para essa opção.";
        return (reason == null || reason.isBlank()) ? base : base + " " + reason;
    }

    private void showHud() {
        view.showHud(session.getPlayer().getEnergy(), session.getPlayer().getMoney());
    }

    private String describeSlot(int slot) {
        GameStates state = stateRepository.loadStateFromSlot(slot);
        if (state == null) return "Slot " + slot + " — vazio";
        return "Slot " + slot + " — Cap. " + state.getCurrentChapterId() + " (" + state.getCurrentSceneId() + ")"
                + (state.getSaveTimestamp() == null ? "" : " — " + state.getSaveTimestamp());
    }
}
