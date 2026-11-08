package tests;

import exception.GamePersistenceException;
import model.Chapter;
import model.Choice;
import model.GameConfig;
import model.GameStates;
import model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import repository.AchievementRepository;
import repository.EndingRepository;
import repository.StateRepository;
import repository.StoryBuilder;
import repository.UnlockedAchievementsRepository;
import repository.UnlockedEndingsRepository;
import service.AchievementService;
import service.AffinityTracker;
import service.AssetResolver;
import service.EndingService;
import service.GameSession;
import service.PlayerService;
import service.StepResult;
import service.StepResult.Status;

import java.nio.file.Path;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class GameSessionTest {
    @TempDir
    Path tempDir;

    private Player player;
    private StateRepository states;

    @BeforeEach
    public void setUp() {
        player = new Player("O+");
        states = new StateRepository(tempDir.resolve("saves").toString());
    }

    private GameSession session(Chapter chapter, boolean autoSave, Set<String> existingAssets) {
        String dir = tempDir.toString();
        AchievementService achievements = new AchievementService(new AchievementRepository(),
                new UnlockedAchievementsRepository(dir));
        EndingService endings = new EndingService(new EndingRepository(), new UnlockedEndingsRepository(dir), achievements);
        return new GameSession(chapter, player, new AffinityTracker(), new GameConfig(0, 1, false, autoSave, false),
                states, achievements, endings, new PlayerService(), new AssetResolver(existingAssets::contains));
    }

    private Chapter story() {
        return StoryBuilder.chapter(1, "Teste")
                .scene("Sala de Estar", "uma sala")
                .narrate("a1", "Início.")
                .say("a2", "Emily", "Surpresa", "Olá!")
                .ask("a3", "Emily", "Normal", "E agora?",
                        new Choice("Descansar", "b1").setEnergyChange(5).setFlagToSet("DESCANSOU"),
                        new Choice("Esforço absurdo", "b1").setEnergyChange(-1000),
                        new Choice("Final", "FINAL_VAMPIRA"))
                .scene("Quarto", "um quarto")
                .narrateEnd("b1", "Fim do capítulo.")
                .build();
    }

    @Test
    public void startShowsFirstDialogueWithSceneInfoAndNoImagesNeeded() {
        StepResult step = session(story(), false, Set.of()).start(null);

        assertEquals(Status.SHOWING, step.status());
        assertEquals("a1", step.snapshot().dialogueId());
        assertEquals("Sala de Estar", step.snapshot().sceneTitle());
        assertTrue(step.snapshot().sceneChanged());
        assertTrue(step.snapshot().narration());
        assertTrue(step.snapshot().linear());
        assertNull(step.snapshot().background());
        assertNull(step.snapshot().sprite());
    }

    @Test
    public void advanceWalksLinearLinesAndFlagsSceneChangeOnlyOnce() {
        GameSession session = session(story(), false, Set.of());
        session.start(null);

        StepResult second = session.advance();
        assertEquals("a2", second.snapshot().dialogueId());
        assertFalse(second.snapshot().sceneChanged());
        assertEquals("Surpresa", second.snapshot().expression());
        assertFalse(second.snapshot().narration());

        StepResult third = session.advance();
        assertFalse(third.snapshot().linear());
        assertEquals(3, third.snapshot().choices().size());
    }

    @Test
    public void advanceOnAChoiceDialogueIsRejected() {
        GameSession session = session(story(), false, Set.of());
        session.start("a3");

        assertThrows(IllegalStateException.class, session::advance);
    }

    @Test
    public void choicesReportAvailabilityAndReason() {
        StepResult step = session(story(), false, Set.of()).start("a3");

        assertTrue(step.snapshot().choices().get(0).available());
        assertNull(step.snapshot().choices().get(0).blockReason());
        assertFalse(step.snapshot().choices().get(1).available());
        assertEquals("[Energia Insuficiente]", step.snapshot().choices().get(1).blockReason());
    }

    @Test
    public void chooseAppliesConsequencesAndMoves() {
        GameSession session = session(story(), false, Set.of());
        session.start("a3");
        int energyBefore = player.getEnergy();

        StepResult step = session.choose(0);

        assertEquals(Status.SHOWING, step.status());
        assertEquals("b1", step.snapshot().dialogueId());
        assertTrue(step.snapshot().sceneChanged());
        assertEquals(Math.min(energyBefore + 5, 100), player.getEnergy());
        assertTrue(player.getFlag("DESCANSOU"));
        assertFalse(step.report().isEmpty());
    }

    @Test
    public void blockedChoiceChangesNothing() {
        GameSession session = session(story(), false, Set.of());
        session.start("a3");
        int energyBefore = player.getEnergy();

        StepResult step = session.choose(1);

        assertEquals(Status.BLOCKED, step.status());
        assertEquals("[Energia Insuficiente]", step.message());
        assertEquals(energyBefore, player.getEnergy());
        assertEquals("a3", session.currentSnapshot().dialogueId());
    }

    @Test
    public void invalidIndexIsRejected() {
        GameSession session = session(story(), false, Set.of());
        session.start("a3");

        assertThrows(IllegalArgumentException.class, () -> session.choose(9));
    }

    @Test
    public void endingCodeUnlocksEndingAndEndsSession() {
        GameSession session = session(story(), false, Set.of());
        session.start("a3");

        StepResult step = session.choose(2);

        assertEquals(Status.ENDING, step.status());
        assertTrue(step.ending().found());
        assertEquals("FINAL_VAMPIRA", step.ending().endingId());
        assertTrue(step.isOver());
        assertTrue(session.isOver());
        assertTrue(new UnlockedEndingsRepository(tempDir.toString()).getUnlockedKeys().contains("FINAL_VAMPIRA"));
        assertThrows(IllegalStateException.class, session::advance);
    }

    @Test
    public void lastLineOfChapterEndsTheChapter() {
        GameSession session = session(story(), false, Set.of());
        session.start("b1");

        assertEquals(Status.CHAPTER_END, session.advance().status());
    }

    @Test
    public void unknownIdIsAnErrorWithMessage() {
        StepResult step = session(story(), false, Set.of()).start("nao_existe");

        assertEquals(Status.ERROR, step.status());
        assertTrue(step.message().contains("nao_existe"));
    }

    @Test
    public void deadEndIsShownThenReportedAsError() {
        Chapter chapter = new Chapter(1, "T");
        model.StoryScene scene = new model.StoryScene("A", "a");
        scene.addDialogue(new model.Dialogue("x", "sem saída"));
        chapter.addScene(scene);

        StepResult step = session(chapter, false, Set.of()).start("x");

        assertEquals(Status.ERROR, step.status());
        assertEquals("x", step.snapshot().dialogueId());
        assertTrue(step.message().contains("x"));
    }

    @Test
    public void dialogueAchievementIsReportedOnTheStepThatShowsIt() {
        Chapter chapter = new repository.StoryRepository().buildCatalog().get(1);
        GameSession session = session(chapter, false, Set.of());

        StepResult step = session.start("dFIM.2"); // dispara ACH_PRIMEIROS_PASSOS
        assertEquals(1, step.dialogueAchievements().size());
        assertEquals("ACH_PRIMEIROS_PASSOS", step.dialogueAchievements().get(0).getId());

        // já desbloqueada: mostrar de novo não repete o aviso
        assertTrue(session.start("dFIM.2").dialogueAchievements().isEmpty());
    }

    @Test
    public void imagesAreResolvedWhenFilesExist() {
        Set<String> files = Set.of("/images/backgrounds/sala_de_estar.png", "/images/characters/emily/surpresa.png");
        GameSession session = session(story(), false, files);

        assertEquals("/images/backgrounds/sala_de_estar.png", session.start("a1").snapshot().background());
        assertNull(session.currentSnapshot().sprite()); // narrador
        assertEquals("/images/characters/emily/surpresa.png", session.advance().snapshot().sprite());
    }

    @Test
    public void autosaveWritesPositionOnlyWhenEnabled() {
        GameSession off = session(story(), false, Set.of());
        off.start("a2");
        assertFalse("a2".equals(states.getCurrentStates().getCurrentDialogId()));

        GameSession on = session(story(), true, Set.of());
        on.start("a2");
        GameStates saved = states.getCurrentStates();
        assertEquals("a2", saved.getCurrentDialogId());
        assertEquals(1, saved.getCurrentChapterId());
        assertEquals("Sala de Estar", saved.getCurrentSceneId());
    }

    @Test
    public void manualSaveStoresCurrentPositionInSlot() throws GamePersistenceException {
        GameSession session = session(story(), false, Set.of());
        session.start("a3");

        session.saveToSlot(2);

        GameStates slot = states.loadStateFromSlot(2);
        assertEquals("a3", slot.getCurrentDialogId());
        assertEquals("Sala de Estar", slot.getCurrentSceneId());
    }
}
