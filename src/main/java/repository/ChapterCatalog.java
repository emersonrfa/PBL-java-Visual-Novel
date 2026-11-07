package repository;

import model.Chapter;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Supplier;

/**
 * Registro dos capítulos do jogo, indexados pelo número. Substitui os {@code switch} por número
 * de capítulo: para incluir um capítulo novo basta registrá-lo em {@link StoryRepository#buildCatalog()}.
 *
 * Cada capítulo é montado na primeira vez que é pedido e reaproveitado depois.
 */
public class ChapterCatalog {
    private final Map<Integer, Supplier<Chapter>> builders = new TreeMap<>();
    private final Map<Integer, Chapter> cache = new HashMap<>();

    public ChapterCatalog register(int chapterNumber, Supplier<Chapter> builder) {
        Objects.requireNonNull(builder, "builder");
        if (builders.putIfAbsent(chapterNumber, builder) != null) {
            throw new IllegalArgumentException("Capítulo " + chapterNumber + " já está registrado.");
        }
        return this;
    }

    public boolean contains(int chapterNumber) {
        return builders.containsKey(chapterNumber);
    }

    /** Retorna o capítulo, ou {@code null} se o número não estiver registrado. */
    public Chapter get(int chapterNumber) {
        if (!builders.containsKey(chapterNumber)) {
            return null;
        }
        return cache.computeIfAbsent(chapterNumber, number -> {
            Chapter chapter = builders.get(number).get();
            if (chapter.getChapterNumber() != number) {
                throw new IllegalStateException("O capítulo registrado como " + number
                        + " declara o número " + chapter.getChapterNumber() + ".");
            }
            return chapter;
        });
    }

    /** Números dos capítulos registrados, em ordem crescente. */
    public Set<Integer> numbers() {
        return Collections.unmodifiableSet(builders.keySet());
    }

    /** Número → título, em ordem crescente (para montar menus). */
    public Map<Integer, String> titles() {
        Map<Integer, String> titles = new TreeMap<>();
        for (Integer number : builders.keySet()) {
            titles.put(number, get(number).getTitle());
        }
        return titles;
    }
}
