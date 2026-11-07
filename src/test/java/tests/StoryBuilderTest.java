package tests;

import model.Chapter;
import model.Choice;
import model.Dialogue;
import model.StoryScene;
import org.junit.jupiter.api.Test;
import repository.StoryBuilder;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class StoryBuilderTest {

    private static String nextOf(Dialogue dialogue) {
        return dialogue.getChoices().get(0).getNextDialogueId();
    }

    @Test
    public void linearLinesChainToTheNextDialogueOfTheScene() {
        Chapter chapter = StoryBuilder.chapter(1, "T")
                .scene("A", "a")
                .narrate("x1", "um")
                .narrate("x2", "dois")
                .narrate("x3", "três", "y1")
                .scene("B", "b")
                .narrateEnd("y1", "fim")
                .build();

        StoryScene sceneA = chapter.getScenes().get(0);
        assertEquals("x2", nextOf(sceneA.getDialogueById("x1")));
        assertEquals("x3", nextOf(sceneA.getDialogueById("x2")));
        assertEquals("y1", nextOf(sceneA.getDialogueById("x3")));
        assertNull(nextOf(chapter.getScenes().get(1).getDialogueById("y1")));
    }

    @Test
    public void parametersLandInTheRightFields() {
        Chapter chapter = StoryBuilder.chapter(1, "T")
                .scene("A", "a")
                .say("s1", "Arthur", "Envergonhado", "Olá!", "q1")
                .ask("q1", "Emily", "Normal", "O que eu faço?", new Choice("Opção", "s1"))
                .narrate("n1", "texto do narrador", "s1")
                .build();

        Dialogue say = chapter.getScenes().get(0).getDialogueById("s1");
        assertEquals("Arthur", say.getSpeaker());
        assertEquals("Envergonhado", say.getExpression());
        assertEquals("Olá!", say.getText());

        Dialogue ask = chapter.getScenes().get(0).getDialogueById("q1");
        assertEquals("Emily", ask.getSpeaker());
        assertEquals("Normal", ask.getExpression());
        assertEquals("O que eu faço?", ask.getText());
        assertEquals("Opção", ask.getChoices().get(0).getText());

        Dialogue narration = chapter.getScenes().get(0).getDialogueById("n1");
        assertEquals("Narrador", narration.getSpeaker());
        assertEquals("Nada", narration.getExpression());
        assertEquals("texto do narrador", narration.getText());
    }

    @Test
    public void duplicateIdsAreRejectedEvenAcrossScenes() {
        StoryBuilder builder = StoryBuilder.chapter(1, "T")
                .scene("A", "a").narrate("x", "1", "y")
                .scene("B", "b").narrateEnd("x", "2");

        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    public void lastLineOfASceneNeedsAnExplicitNextOrEnd() {
        StoryBuilder builder = StoryBuilder.chapter(1, "T")
                .scene("A", "a").narrate("x", "sem próximo");

        IllegalStateException error = assertThrows(IllegalStateException.class, builder::build);
        assertTrue(error.getMessage().contains("x"));
    }

    @Test
    public void askNeedsAtLeastOneChoice() {
        StoryBuilder builder = StoryBuilder.chapter(1, "T").scene("A", "a");

        assertThrows(IllegalArgumentException.class, () -> builder.ask("q", "pergunta"));
    }

    @Test
    public void nullNextIsRejectedInsteadOfSilentlyEndingTheGame() {
        StoryBuilder builder = StoryBuilder.chapter(1, "T").scene("A", "a");

        assertThrows(NullPointerException.class, () -> builder.narrate("x", "texto", null));
    }

    @Test
    public void linesNeedAScene() {
        assertThrows(IllegalStateException.class, () -> StoryBuilder.chapter(1, "T").narrate("x", "texto"));
    }

    @Test
    public void emptySceneIsRejected() {
        StoryBuilder builder = StoryBuilder.chapter(1, "T").scene("Vazia", "sem diálogos");

        assertThrows(IllegalStateException.class, builder::build);
    }

    @Test
    public void pendingIdsAreStoredOnTheChapter() {
        Chapter chapter = StoryBuilder.chapter(1, "T")
                .pending("d9.1", "d9.2")
                .scene("A", "a").narrateEnd("x", "fim")
                .build();

        assertEquals(Set.of("d9.1", "d9.2"), chapter.getPendingDialogueIds());
    }
}
