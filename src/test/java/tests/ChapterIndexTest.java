package tests;

import model.Chapter;
import model.Dialogue;
import model.Protagonist;
import model.StoryScene;
import org.junit.jupiter.api.Test;
import repository.StoryRepository;
import service.StoryService;

import static org.junit.jupiter.api.Assertions.*;

public class ChapterIndexTest {

    @Test
    public void findsSceneAndDialogueById() {
        Chapter chapter = new Chapter(1, "T");
        StoryScene a = new StoryScene("A", "a");
        a.addSimpleDialogue("x1", "um", "x2");
        StoryScene b = new StoryScene("B", "b");
        b.addSimpleDialogue("y1", "dois", null);
        chapter.addScene(a);
        chapter.addScene(b);

        assertSame(a, chapter.findSceneByDialogueId("x1"));
        assertSame(b, chapter.findSceneByDialogueId("y1"));
        assertEquals("dois", chapter.findDialogueById("y1").getText());
        assertNull(chapter.findSceneByDialogueId("nao_existe"));
        assertNull(chapter.findSceneByDialogueId(null));
    }

    @Test
    public void indexSeesDialoguesAndScenesAddedAfterTheFirstLookup() {
        Chapter chapter = new Chapter(1, "T");
        StoryScene a = new StoryScene("A", "a");
        a.addSimpleDialogue("x1", "um", null);
        chapter.addScene(a);
        assertNull(chapter.findSceneByDialogueId("x2"));

        a.addSimpleDialogue("x2", "dois", null);
        StoryScene b = new StoryScene("B", "b");
        b.addSimpleDialogue("y1", "três", null);
        chapter.addScene(b);

        assertSame(a, chapter.findSceneByDialogueId("x2"));
        assertSame(b, chapter.findSceneByDialogueId("y1"));
    }

    @Test
    public void duplicateIdResolvesToTheFirstSceneLikeTheOldScan() {
        Chapter chapter = new Chapter(1, "T");
        StoryScene a = new StoryScene("A", "a");
        a.addSimpleDialogue("x", "primeiro", null);
        StoryScene b = new StoryScene("B", "b");
        b.addSimpleDialogue("x", "segundo", null);
        chapter.addScene(a);
        chapter.addScene(b);

        assertSame(a, chapter.findSceneByDialogueId("x"));
    }

    @Test
    public void storyServiceDelegatesToTheIndexAndAgreesWithEveryRealDialogue() {
        Chapter chapter = new StoryRepository().buildCatalog().get(1);
        for (StoryScene scene : chapter.getScenes()) {
            for (Dialogue dialogue : scene.getDialogues()) {
                assertSame(scene, StoryService.findSceneByDialogueId(chapter, dialogue.getId()));
            }
        }
    }

    @Test
    public void playerIsTheSameCharacterAsEmily() {
        assertEquals("Emily", Protagonist.canonicalName("Player"));
        assertEquals("Emily", Protagonist.canonicalName("emily"));
        assertEquals("Arthur", Protagonist.canonicalName("Arthur"));
        assertFalse(Protagonist.isProtagonist("Narrador"));
    }
}
