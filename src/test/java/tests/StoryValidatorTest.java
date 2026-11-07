package tests;

import model.Chapter;
import model.Choice;
import model.Dialogue;
import model.StoryScene;
import org.junit.jupiter.api.Test;
import repository.ChapterCatalog;
import repository.StoryBuilder;
import repository.StoryRepository;
import service.StoryValidator;
import service.StoryValidator.Issue;
import service.StoryValidator.Type;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class StoryValidatorTest {

    private static boolean has(List<Issue> issues, Type type, String dialogueId) {
        return issues.stream().anyMatch(i -> i.type() == type && i.dialogueId().equals(dialogueId));
    }

    /** Guarda de integridade: vale para todo capítulo registrado, inclusive os que forem criados depois. */
    @Test
    public void everyRegisteredChapterHasNoStructuralErrors() {
        ChapterCatalog catalog = new StoryRepository().buildCatalog();

        for (int number : catalog.numbers()) {
            List<Issue> errors = StoryValidator.errors(catalog.get(number));
            assertTrue(errors.isEmpty(), "Capítulo " + number + " com problemas: " + errors);
        }
    }

    @Test
    public void danglingTargetIsAnErrorUnlessDeclaredPending() {
        Chapter withoutPending = StoryBuilder.chapter(1, "T")
                .scene("A", "a").narrate("x", "texto", "falta").build();
        Chapter withPending = StoryBuilder.chapter(1, "T").pending("falta")
                .scene("A", "a").narrate("x", "texto", "falta").build();

        assertTrue(has(StoryValidator.validate(withoutPending), Type.MISSING_TARGET, "x"));
        assertFalse(StoryValidator.validate(withoutPending).isEmpty());

        assertTrue(has(StoryValidator.validate(withPending), Type.PENDING_TARGET, "x"));
        assertTrue(StoryValidator.errors(withPending).isEmpty());
    }

    @Test
    public void unreachableDialogueIsReported() {
        Chapter chapter = StoryBuilder.chapter(1, "T")
                .scene("A", "a")
                .narrate("inicio", "começo", "fim")
                .narrateEnd("fim", "fim")
                .narrateEnd("orfao", "ninguém chega aqui")
                .build();

        List<Issue> issues = StoryValidator.validate(chapter);
        assertTrue(has(issues, Type.UNREACHABLE, "orfao"));
        assertFalse(has(issues, Type.UNREACHABLE, "fim"));
    }

    @Test
    public void endingCodesAndChapterEndsAreValidTargets() {
        Chapter chapter = StoryBuilder.chapter(1, "T")
                .scene("A", "a")
                .ask("q", "Escolha",
                        new Choice("Final", "FINAL_VAMPIRA"),
                        new Choice("Continuar", "fim"))
                .narrateEnd("fim", "fim")
                .build();

        assertTrue(StoryValidator.validate(chapter).isEmpty());
    }

    @Test
    public void dialogueWithoutChoicesIsADeadEnd() {
        Chapter chapter = new Chapter(1, "T");
        StoryScene scene = new StoryScene("A", "a");
        scene.addDialogue(new Dialogue("x", "texto sem continuação"));
        chapter.addScene(scene);

        assertTrue(has(StoryValidator.validate(chapter), Type.DEAD_END, "x"));
    }

    @Test
    public void duplicateIdsAreReported() {
        Chapter chapter = new Chapter(1, "T");
        StoryScene scene = new StoryScene("A", "a");
        scene.addSimpleDialogue("x", "um", "x");
        scene.addSimpleDialogue("x", "outro", null);
        chapter.addScene(scene);

        assertTrue(has(StoryValidator.validate(chapter), Type.DUPLICATE_ID, "x"));
    }

    @Test
    public void emptyChapterIsReported() {
        List<Issue> issues = StoryValidator.validate(new Chapter(1, "Vazio"));

        assertEquals(Type.EMPTY_CHAPTER, issues.get(0).type());
    }
}
