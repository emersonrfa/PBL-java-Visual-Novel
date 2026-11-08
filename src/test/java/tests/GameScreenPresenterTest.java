package tests;

import model.Chapter;
import model.Choice;
import model.Ending;
import model.GameConfig;
import model.Item;
import model.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import repository.AchievementRepository;
import repository.ChapterCatalog;
import repository.EndingRepository;
import repository.StateRepository;
import repository.StoryBuilder;
import repository.UnlockedAchievementsRepository;
import repository.UnlockedEndingsRepository;
import service.AchievementService;
import service.AffinityTracker;
import service.AssetResolver;
import service.DialogueSnapshot.ChoiceView;
import service.EndingService;
import service.GameSession;
import service.InventoryService.InventoryEntry;
import service.PlayerService;
import ui.game.GameScreenPresenter;
import ui.game.GameScreenView;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class GameScreenPresenterTest {
    @TempDir
    Path tempDir;

    /** Tela falsa: só anota o que o presenter pediu para desenhar. */
    private static class FakeView implements GameScreenView {
        final List<String> log = new ArrayList<>();
        List<ChoiceView> choices;
        List<InventoryEntry> inventory;
        List<String> slots;
        boolean continuePrompt;
        String lastSprite = "?";

        public void showChapterTitle(int n, String t) { log.add("chapter:" + n + ":" + t); }
        public void setBackground(String p) { log.add("bg:" + p); }
        public void setSprite(String p) { lastSprite = p; }
        public void showDialogue(String s, String t) { log.add("say:" + s + ":" + t); continuePrompt = false; choices = null; }
        public void showContinuePrompt() { continuePrompt = true; }
        public void showChoices(List<ChoiceView> c) { choices = c; }
        public void showHud(int e, double m) { log.add("hud:" + e + ":" + (int) m); }
        public void showToast(String m) { log.add("toast:" + m); }
        public void showInventory(List<InventoryEntry> e) { inventory = e; }
        public void hideInventory() { inventory = null; }
        public void showSaveSlots(List<String> l) { slots = l; }
        public void hideSaveSlots() { slots = null; }
        public void showEnding(Ending e) { log.add("ending:" + e.getId()); }
        public void showChapterEnd() { log.add("chapterEnd"); }
        public void showError(String m) { log.add("error:" + m); }
        public void exitToMenu() { log.add("menu"); }
    }

    private Player player;
    private FakeView view;
    private StateRepository states;
    private GameScreenPresenter presenter;

    @BeforeEach
    public void setUp() {
        player = new Player("O+");
        player.getInventory().addItem(new Item("Chave", "Abre o quarto.", "KEY", 0, 0));
        view = new FakeView();
        states = new StateRepository(tempDir.resolve("saves").toString());

        Chapter chapter = StoryBuilder.chapter(1, "Cap Um")
                .scene("Quarto", "q")
                .say("a1", "Player", "Cansada", "Bom dia.")
                .narrate("a2", "Narração.")
                .ask("a3", "Emily", "Normal", "E agora?",
                        new Choice("Dormir", "b1").setEnergyChange(-10),
                        new Choice("Impossível", "b1").setEnergyChange(-1000),
                        new Choice("Final", "FINAL_VAMPIRA"))
                .narrateEnd("b1", "Fim.")
                .build();
        ChapterCatalog catalog = new ChapterCatalog().register(1, () -> chapter);

        String dir = tempDir.toString();
        AchievementService achievements = new AchievementService(new AchievementRepository(), new UnlockedAchievementsRepository(dir));
        EndingService endings = new EndingService(new EndingRepository(), new UnlockedEndingsRepository(dir), achievements);
        GameSession session = new GameSession(catalog, 1, false, player, new AffinityTracker(),
                new GameConfig(0, 1, false, false, false), states, achievements, endings, new PlayerService(),
                new AssetResolver(Set.of("/images/backgrounds/quarto.png", "/images/characters/emily/cansada.png")::contains));
        presenter = new GameScreenPresenter(session, view, states);
    }

    @Test
    public void beginShowsHudChapterBackgroundSpriteAndDialogueWithProtagonistRenamed() {
        presenter.begin(null);

        assertEquals(List.of("hud:100:" + (int) player.getMoney(), "chapter:1:Cap Um", "bg:/images/backgrounds/quarto.png",
                "say:Emily:Bom dia."), view.log);
        assertEquals("/images/characters/emily/cansada.png", view.lastSprite);
        assertTrue(view.continuePrompt);
    }

    @Test
    public void narrationHasNoSpeakerAndNoSprite() {
        presenter.begin("a2");

        assertTrue(view.log.contains("say:null:Narração."));
        assertNull(view.lastSprite);
    }

    @Test
    public void continueAdvancesAndChoicesAreOfferedWithAvailability() {
        presenter.begin("a2");
        presenter.onContinue();

        assertNotNull(view.choices);
        assertEquals(3, view.choices.size());
        assertTrue(view.choices.get(0).available());
        assertFalse(view.choices.get(1).available());
    }

    @Test
    public void choosingUpdatesHudViaObserverAndShowsConsequenceToast() {
        presenter.begin("a3");
        view.log.clear();

        presenter.onChoice(0);

        assertTrue(view.log.stream().anyMatch(l -> l.startsWith("toast:")));
        assertTrue(view.log.contains("hud:90:" + (int) player.getMoney()));
        assertTrue(view.log.contains("say:null:Fim."));
    }

    @Test
    public void blockedChoiceOnlyShowsAToast() {
        presenter.begin("a3");
        view.log.clear();

        presenter.onChoice(1);

        assertEquals(1, view.log.size(), String.valueOf(view.log));
        assertTrue(view.log.get(0).startsWith("toast:Você não cumpre"));
        assertNotNull(view.choices);
    }

    @Test
    public void endingAndChapterEndStopFurtherInput() {
        presenter.begin("a3");
        presenter.onChoice(2);
        assertTrue(view.log.contains("ending:FINAL_VAMPIRA"));
        assertTrue(view.log.stream().anyMatch(l -> l.startsWith("toast:Conquista desbloqueada")));

        view.log.clear();
        presenter.onContinue();
        presenter.onChoice(0);
        assertTrue(view.log.isEmpty());
    }

    @Test
    public void chapterEndIsReported() {
        presenter.begin("b1");
        presenter.onContinue();

        assertTrue(view.log.contains("chapterEnd"));
    }

    @Test
    public void unknownIdIsShownAsError() {
        presenter.begin("nao_existe");

        assertTrue(view.log.stream().anyMatch(l -> l.startsWith("error:") && l.contains("nao_existe")));
    }

    @Test
    public void inventoryIsViewOnlyAndBlocksGameInputWhileOpen() {
        presenter.begin("a2");

        presenter.onOpenInventory();
        assertEquals(1, view.inventory.size());
        assertEquals("Chave", view.inventory.get(0).name());
        assertEquals("Abre o quarto.", view.inventory.get(0).description());
        assertNull(view.inventory.get(0).imagePath());

        view.log.clear();
        presenter.onContinue(); // ignorado com o inventário aberto
        assertTrue(view.log.isEmpty());

        presenter.onCloseInventory();
        assertNull(view.inventory);
        presenter.onContinue();
        assertFalse(view.log.isEmpty());
    }

    @Test
    public void saveSlotsAreListedAndSavingShowsToast() {
        presenter.begin("a3");

        presenter.onOpenSave();
        assertEquals(4, view.slots.size());
        assertTrue(view.slots.get(0).contains("vazio"));

        presenter.onSaveToSlot(1);
        assertNull(view.slots);
        assertTrue(view.log.contains("toast:Jogo salvo no Slot 1!"));

        presenter.onOpenSave();
        assertTrue(view.slots.get(0).contains("Cap. 1"));
    }

    @Test
    public void exitToMenuIsForwardedToTheView() {
        presenter.begin("a1");
        presenter.onExitToMenu();

        assertEquals("menu", view.log.get(view.log.size() - 1));
    }
}
