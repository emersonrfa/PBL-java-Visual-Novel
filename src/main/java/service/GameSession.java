package service;

import event.Observer;
import event.Subject;
import exception.GamePersistenceException;
import model.Achievement;
import model.Chapter;
import model.Choice;
import model.Dialogue;
import model.GameConfig;
import model.GameStates;
import model.Player;
import model.StoryScene;
import repository.ChapterCatalog;
import repository.StateRepository;
import service.ChoiceEvaluator.ChoiceAvailability;
import service.DialogueSnapshot.ChoiceView;
import service.EndingService.EndingOutcome;
import service.GameEvent.*;
import service.StepResult.Status;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Uma partida em andamento, conduzida por eventos em vez de por um laço bloqueante. No modo
 * "campanha" ela segue sozinha para o capítulo seguinte do catálogo quando um termina.
 *
 * A tela (console ou JavaFX) chama {@link #start}, depois {@link #advance()} para diálogos lineares
 * ou {@link #choose(int)} para escolhas; cada chamada devolve um {@link StepResult} com o que
 * desenhar. A sessão cuida de regras, consequências, conquistas, finais e autosave; não imprime
 * nem lê nada, então roda igual em testes, no console e numa única tela JavaFX.
 *
 * Também é o "Subject" do padrão Observer: publica {@link GameEvent}s (diálogo mostrado, status
 * alterado, conquista, final, ...) para HUD, avisos e o que mais precisar reagir.
 *
 * Não é thread-safe: use sempre da mesma thread (na JavaFX, a Application Thread).
 */
public class GameSession {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
    private static final List<Achievement> NONE = List.of();

    private final ChapterCatalog catalog;
    private final boolean continueToNextChapter;
    private final Subject<GameEvent> events = new Subject<>();
    private Chapter chapter;
    private boolean chapterChanged;
    private final Player player;
    private final AffinityTracker affinityTracker;
    private final GameConfig config;
    private final StateRepository stateRepository;
    private final AchievementService achievementService;
    private final EndingService endingService;
    private final PlayerService playerService;
    private final ChoiceEvaluator choiceEvaluator;
    private final AssetResolver assets;

    private StoryScene currentScene;
    private Dialogue currentDialogue;
    private boolean over;

    /**
     * @param continueToNextChapter true = campanha (ao fim do capítulo abre o próximo registrado);
     *                              false = joga só este capítulo (ex.: seleção de capítulo do admin)
     */
    public GameSession(ChapterCatalog catalog, int chapterNumber, boolean continueToNextChapter, Player player, AffinityTracker affinityTracker, GameConfig config,
                       StateRepository stateRepository, AchievementService achievementService,
                       EndingService endingService, PlayerService playerService, AssetResolver assets) {
        this.catalog = catalog;
        this.chapter = catalog.get(chapterNumber);
        if (this.chapter == null) {
            throw new IllegalArgumentException("Capítulo não registrado: " + chapterNumber);
        }
        this.continueToNextChapter = continueToNextChapter;
        this.player = player;
        this.affinityTracker = affinityTracker;
        this.config = config;
        this.stateRepository = stateRepository;
        this.achievementService = achievementService;
        this.endingService = endingService;
        this.playerService = playerService;
        this.choiceEvaluator = new ChoiceEvaluator(playerService, affinityTracker);
        this.assets = assets;
    }

    // ------------------------------------------------------------ eventos

    /** Começa (ou retoma um save) no diálogo informado; nulo/vazio = primeiro diálogo do capítulo. */
    public StepResult start(String dialogueId) {
        over = false;
        chapterChanged = true;
        events.publish(new ChapterStarted(chapter.getChapterNumber(), chapter.getTitle()));
        currentScene = null;
        currentDialogue = null;
        String id = (dialogueId == null || dialogueId.isBlank()) ? firstDialogueId() : dialogueId;
        return moveTo(id, null, NONE);
    }

    /** Avança um diálogo linear (botão/ENTER "continuar"). */
    public StepResult advance() {
        requireActive();
        if (!currentDialogue.isLinear()) {
            throw new IllegalStateException("O diálogo '" + currentDialogue.getId() + "' tem escolhas; use choose(int).");
        }
        return moveTo(currentDialogue.getChoices().get(0).getNextDialogueId(), null, NONE);
    }

    /** Escolhe a opção {@code index} (base 0) do diálogo atual. */
    public StepResult choose(int index) {
        requireActive();
        List<Choice> choices = currentDialogue.getChoices();
        if (index < 0 || index >= choices.size()) {
            throw new IllegalArgumentException("Escolha inexistente: " + index);
        }
        Choice chosen = choices.get(index);

        ChoiceAvailability availability = choiceEvaluator.evaluate(player, chosen);
        if (!availability.available()) {
            return new StepResult(Status.BLOCKED, snapshot(false), null, NONE, NONE, null, availability.blockReason());
        }

        ConsequenceReport report = playerService.applyChoiceConsequences(player, chosen, affinityTracker);
        publishConsequences(report);
        return moveTo(chosen.getNextDialogueId(), report, achievementsFor(chosen));
    }

    /** Estado atual para redesenhar a tela (ex.: depois de fechar o inventário ou salvar). */
    public DialogueSnapshot currentSnapshot() {
        requireActive();
        return snapshot(false);
    }

    // ------------------------------------------------------------ salvar

    /** Salva a posição atual no slot manual (1–4). */
    public void saveToSlot(int slot) throws GamePersistenceException {
        requireActive();
        GameStates state = new GameStates();
        fillState(state);
        stateRepository.saveStateToSlotChecked(state, slot);
        events.publish(new GameSaved(slot));
    }

    // ------------------------------------------------------------ observers

    public void addObserver(Observer<GameEvent> observer) {
        events.addObserver(observer);
    }

    public void removeObserver(Observer<GameEvent> observer) {
        events.removeObserver(observer);
    }

    // ------------------------------------------------------------ consulta

    public Player getPlayer() {
        return player;
    }

    public AffinityTracker getAffinityTracker() {
        return affinityTracker;
    }

    public Chapter getChapter() {
        return chapter;
    }

    public AssetResolver getAssets() {
        return assets;
    }

    public boolean isOver() {
        return over;
    }

    // ------------------------------------------------------------ internos

    private StepResult moveTo(String id, ConsequenceReport report, List<Achievement> choiceAchievements) {
        if (id == null || id.isBlank()) {
            events.publish(new ChapterFinished(chapter.getChapterNumber()));
            Chapter next = continueToNextChapter ? nextChapter() : null;
            if (next == null || next.getScenes().isEmpty()) {
                return finish(Status.CHAPTER_END, report, choiceAchievements, null, null);
            }
            chapter = next;
            chapterChanged = true;
            currentScene = null;
            events.publish(new ChapterStarted(chapter.getChapterNumber(), chapter.getTitle()));
            return moveTo(firstDialogueId(), report, choiceAchievements);
        }
        if (endingService.isEndingCode(id)) {
            EndingOutcome outcome = endingService.reach(id);
            outcome.newAchievements().forEach(a -> events.publish(new AchievementUnlocked(a)));
            events.publish(new EndingReached(outcome));
            return finish(Status.ENDING, report, choiceAchievements, outcome, null);
        }

        StoryScene scene = StoryService.findSceneByDialogueId(chapter, id);
        if (scene == null) {
            return finish(Status.ERROR, report, choiceAchievements, null,
                    "JOGO ENCERRADO POR NÃO TER ACHADO ID DE PRÓXIMA CENA (" + id + ")");
        }

        boolean sceneChanged = scene != currentScene;
        boolean newChapter = chapterChanged;
        chapterChanged = false;
        currentScene = scene;
        currentDialogue = scene.getDialogueById(id);

        autoSave();

        List<Achievement> dialogueAchievements = NONE;
        Achievement byDialogue = achievementService.evaluateDialogueTrigger(currentDialogue.getId());
        if (byDialogue != null) dialogueAchievements = List.of(byDialogue);

        choiceAchievements.forEach(a -> events.publish(new AchievementUnlocked(a)));
        dialogueAchievements.forEach(a -> events.publish(new AchievementUnlocked(a)));

        if (!currentDialogue.hasChoices()) {
            // o diálogo é mostrado antes de acusar o erro, como no console original
            over = true;
            return new StepResult(Status.ERROR, publishShown(snapshot(sceneChanged, newChapter)), report, choiceAchievements,
                    dialogueAchievements, null,
                    "JOGO ENCERRADO POR NÃO TER ACHADO NENHUMA OPÇÃO DE DIÁLOGO (" + id + ")");
        }
        return new StepResult(Status.SHOWING, publishShown(snapshot(sceneChanged, newChapter)), report, choiceAchievements,
                dialogueAchievements, null, null);
    }

    private DialogueSnapshot publishShown(DialogueSnapshot snapshot) {
        events.publish(new DialogueShown(snapshot));
        return snapshot;
    }

    private void publishConsequences(ConsequenceReport report) {
        events.publish(new ChoiceApplied(report));
        if (!report.getChanges(ConsequenceReport.Kind.ENERGY).isEmpty()
                || !report.getChanges(ConsequenceReport.Kind.MONEY).isEmpty()) {
            events.publish(new StatsChanged(player.getEnergy(), player.getMoney()));
        }
        if (!report.getChanges(ConsequenceReport.Kind.ITEM_GAINED).isEmpty()
                || !report.getChanges(ConsequenceReport.Kind.ITEM_USED).isEmpty()) {
            events.publish(new InventoryChanged(player.getInventory().getItemNames()));
        }
    }

    /** Menor capítulo registrado com número maior que o atual, ou nulo. */
    private Chapter nextChapter() {
        for (int number : catalog.numbers()) {
            if (number > chapter.getChapterNumber()) return catalog.get(number);
        }
        return null;
    }

    private StepResult finish(Status status, ConsequenceReport report, List<Achievement> choiceAchievements,
                              EndingOutcome ending, String message) {
        over = true;
        return new StepResult(status, null, report, choiceAchievements, NONE, ending, message);
    }

    private List<Achievement> achievementsFor(Choice chosen) {
        List<Achievement> unlocked = new ArrayList<>();
        if (chosen.getTargetNpc() != null) {
            Achievement byAffinity = achievementService.evaluateAffinityTrigger(
                    chosen.getTargetNpc(), affinityTracker.getAffinity(chosen.getTargetNpc()));
            if (byAffinity != null) unlocked.add(byAffinity);
        }
        if (chosen.getFlagToSet() != null && !chosen.getFlagToSet().trim().isEmpty()) {
            Achievement byFlag = achievementService.evaluateFlagTrigger(chosen.getFlagToSet());
            if (byFlag != null) unlocked.add(byFlag);
        }
        return unlocked;
    }

    private DialogueSnapshot snapshot(boolean sceneChanged) {
        return snapshot(sceneChanged, false);
    }

    private DialogueSnapshot snapshot(boolean sceneChanged, boolean newChapter) {
        List<ChoiceView> views = new ArrayList<>();
        List<Choice> choices = currentDialogue.getChoices();
        for (int i = 0; i < choices.size(); i++) {
            ChoiceAvailability a = choiceEvaluator.evaluate(player, choices.get(i));
            views.add(new ChoiceView(i, choices.get(i).getText(), a.available(), a.blockReason()));
        }
        String speaker = currentDialogue.getSpeaker();
        return new DialogueSnapshot(
                chapter.getChapterNumber(), chapter.getTitle(), newChapter,
                currentScene.getTitle(), currentScene.getDescription(), sceneChanged,
                assets.background(currentScene).orElse(null),
                currentDialogue.getId(), speaker, currentDialogue.getExpression(),
                assets.sprite(speaker, currentDialogue.getExpression()).orElse(null),
                currentDialogue.getText(), "Narrador".equals(speaker),
                currentDialogue.isLinear(), views);
    }

    private void autoSave() {
        if (stateRepository == null || config == null || !config.isTurnAutoSave()) return;
        GameStates state = stateRepository.getCurrentStates();
        fillState(state);
        stateRepository.saveState(state);
    }

    private void fillState(GameStates state) {
        state.setCurrentChapterId(chapter.getChapterNumber());
        state.setCurrentSceneId(currentScene.getTitle());
        state.setCurrentDialogId(currentDialogue.getId());
        state.setSaveTimestamp(LocalDateTime.now().format(DATE_FORMATTER));
        playerService.populateStateFromPlayer(state, player, affinityTracker);
    }

    private String firstDialogueId() {
        for (StoryScene scene : chapter.getScenes()) {
            if (!scene.getDialogues().isEmpty()) return scene.getDialogues().get(0).getId();
        }
        return null;
    }

    private void requireActive() {
        if (over || currentDialogue == null) {
            throw new IllegalStateException("Não há diálogo ativo; chame start(...) primeiro.");
        }
    }
}
