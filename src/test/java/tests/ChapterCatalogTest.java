package tests;

import model.Chapter;
import org.junit.jupiter.api.Test;
import repository.ChapterCatalog;
import repository.StoryRepository;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.*;

public class ChapterCatalogTest {

    @Test
    public void returnsRegisteredChaptersInNumericOrder() {
        ChapterCatalog catalog = new ChapterCatalog()
                .register(2, () -> new Chapter(2, "Segundo"))
                .register(1, () -> new Chapter(1, "Primeiro"));

        assertEquals(List.of(1, 2), List.copyOf(catalog.numbers()));
        assertEquals("Primeiro", catalog.get(1).getTitle());
        assertEquals("Primeiro", catalog.titles().get(1));
        assertEquals("Segundo", catalog.titles().get(2));
    }

    @Test
    public void unknownChapterIsNullAndNotContained() {
        ChapterCatalog catalog = new ChapterCatalog().register(1, () -> new Chapter(1, "Primeiro"));

        assertNull(catalog.get(7));
        assertFalse(catalog.contains(7));
        assertTrue(catalog.contains(1));
    }

    @Test
    public void chapterIsBuiltOnlyOnce() {
        AtomicInteger builds = new AtomicInteger();
        ChapterCatalog catalog = new ChapterCatalog().register(1, () -> {
            builds.incrementAndGet();
            return new Chapter(1, "Primeiro");
        });

        assertSame(catalog.get(1), catalog.get(1));
        assertEquals(1, builds.get());
    }

    @Test
    public void duplicateRegistrationIsRejected() {
        ChapterCatalog catalog = new ChapterCatalog().register(1, () -> new Chapter(1, "A"));

        assertThrows(IllegalArgumentException.class, () -> catalog.register(1, () -> new Chapter(1, "B")));
    }

    @Test
    public void chapterWithWrongNumberIsRejected() {
        ChapterCatalog catalog = new ChapterCatalog().register(3, () -> new Chapter(1, "Número errado"));

        assertThrows(IllegalStateException.class, () -> catalog.get(3));
    }

    @Test
    public void storyRepositoryCatalogHasTheCurrentChapters() {
        ChapterCatalog catalog = new StoryRepository().buildCatalog();

        assertEquals(List.of(1, 2), List.copyOf(catalog.numbers()));
        assertEquals(1, catalog.get(1).getChapterNumber());
        assertEquals(2, catalog.get(2).getChapterNumber());
    }
}
