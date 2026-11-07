package repository;

import model.Chapter;
import model.Choice;
import model.Dialogue;
import model.StoryScene;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Montagem fluente de capítulos. Evita repetir o id do próximo diálogo a cada linha e usa uma
 * ordem de parâmetros única, com nomes diferentes para cada tipo de linha.
 *
 * <pre>
 * StoryBuilder.chapter(3, "Título")
 *     .scene("Cozinha", "Descrição da cena")
 *         .narrate("d1.1", "Texto do narrador.")                       // segue para o próximo da cena
 *         .say("d1.2", "Emily", "Surpresa", "Ah! É verdade.")          // fala de personagem
 *         .narrate("d1.3", "Vai para outra cena.", "d2.1")             // próximo explícito
 *         .ask("d1.4", "Emily", "Normal", "O que eu faço?",            // escolhas
 *              new Choice("Opção A", "d2.1"),
 *              new Choice("Opção B", "d3.1").setEnergyChange(-10))
 *     .scene("Outra cena", "...")
 *         .narrateEnd("d2.1", "Última linha do capítulo.")              // fim (sem próximo)
 *     .build();
 * </pre>
 *
 * Regras checadas em {@link #build()}: ids únicos no capítulo, cenas não vazias e toda linha linear
 * com próximo definido (explícito, o seguinte da cena, ou fim declarado com {@code ...End}).
 * Se o próximo apontar para um id inexistente, quem acusa é o {@link service.StoryValidator}.
 */
public final class StoryBuilder {
    private enum Next { AUTO, EXPLICIT, END }

    private record Line(String id, String speaker, String expression, String text, Next nextKind, String nextId, Choice[] choices) {}

    private static final class SceneDraft {
        final String title;
        final String description;
        final List<Line> lines = new ArrayList<>();

        SceneDraft(String title, String description) {
            this.title = title;
            this.description = description;
        }
    }

    private static final String NARRATOR = "Narrador";
    private static final String NO_EXPRESSION = "Nada";

    private final int number;
    private final String title;
    private final List<SceneDraft> scenes = new ArrayList<>();
    private final List<String> pendingIds = new ArrayList<>();
    private SceneDraft current;

    private StoryBuilder(int number, String title) {
        this.number = number;
        this.title = title;
    }

    public static StoryBuilder chapter(int number, String title) {
        return new StoryBuilder(number, title);
    }

    public StoryBuilder scene(String title, String description) {
        current = new SceneDraft(title, description);
        scenes.add(current);
        return this;
    }

    // ---------------------------------------------- narrador

    /** Narração; ao avançar, vai para o próximo diálogo da cena. */
    public StoryBuilder narrate(String id, String text) {
        return add(new Line(id, NARRATOR, NO_EXPRESSION, text, Next.AUTO, null, null));
    }

    /** Narração com próximo explícito (outra cena ou salto). */
    public StoryBuilder narrate(String id, String text, String nextId) {
        return add(new Line(id, NARRATOR, NO_EXPRESSION, text, Next.EXPLICIT, requireNext(id, nextId), null));
    }

    /** Narração que encerra o capítulo. */
    public StoryBuilder narrateEnd(String id, String text) {
        return add(new Line(id, NARRATOR, NO_EXPRESSION, text, Next.END, null, null));
    }

    // ---------------------------------------------- personagens

    /** Fala de personagem; ao avançar, vai para o próximo diálogo da cena. */
    public StoryBuilder say(String id, String speaker, String expression, String text) {
        return add(new Line(id, speaker, expression, text, Next.AUTO, null, null));
    }

    /** Fala de personagem com próximo explícito. */
    public StoryBuilder say(String id, String speaker, String expression, String text, String nextId) {
        return add(new Line(id, speaker, expression, text, Next.EXPLICIT, requireNext(id, nextId), null));
    }

    /** Fala de personagem que encerra o capítulo. */
    public StoryBuilder sayEnd(String id, String speaker, String expression, String text) {
        return add(new Line(id, speaker, expression, text, Next.END, null, null));
    }

    // ---------------------------------------------- escolhas

    /** Pergunta do narrador com escolhas. */
    public StoryBuilder ask(String id, String text, Choice... choices) {
        return ask(id, NARRATOR, NO_EXPRESSION, text, choices);
    }

    /** Pergunta de um personagem com escolhas. */
    public StoryBuilder ask(String id, String speaker, String expression, String text, Choice... choices) {
        if (choices == null || choices.length == 0) {
            throw new IllegalArgumentException("O diálogo '" + id + "' precisa de ao menos uma escolha.");
        }
        return add(new Line(id, speaker, expression, text, Next.EXPLICIT, null, choices));
    }

    // ---------------------------------------------- roteiro em andamento

    /** Declara ids que serão escritos depois; referências a eles não são tratadas como erro pelo validador. */
    public StoryBuilder pending(String... dialogueIds) {
        pendingIds.addAll(List.of(dialogueIds));
        return this;
    }

    // ---------------------------------------------- montagem

    public Chapter build() {
        Chapter chapter = new Chapter(number, title);
        Set<String> seen = new HashSet<>();

        for (SceneDraft draft : scenes) {
            if (draft.lines.isEmpty()) {
                throw new IllegalStateException("A cena '" + draft.title + "' não tem diálogos.");
            }
            StoryScene scene = new StoryScene(draft.title, draft.description);

            for (int i = 0; i < draft.lines.size(); i++) {
                Line line = draft.lines.get(i);
                if (!seen.add(line.id())) {
                    throw new IllegalStateException("ID de diálogo duplicado no capítulo " + number + ": " + line.id());
                }
                scene.addDialogue(toDialogue(draft, i, line));
            }
            chapter.addScene(scene);
        }

        chapter.markPending(pendingIds.toArray(new String[0]));
        return chapter;
    }

    private Dialogue toDialogue(SceneDraft draft, int index, Line line) {
        if (line.choices() != null) {
            Dialogue dialogue = new Dialogue(line.id(), line.speaker(), line.text(), line.expression());
            for (Choice choice : line.choices()) {
                dialogue.addChoice(choice);
            }
            return dialogue;
        }

        String next;
        switch (line.nextKind()) {
            case EXPLICIT -> next = line.nextId();
            case END -> next = null;
            default -> { // AUTO: o próximo diálogo declarado na mesma cena
                if (index + 1 >= draft.lines.size()) {
                    throw new IllegalStateException("O diálogo '" + line.id() + "' é o último da cena '" + draft.title
                            + "' e não tem próximo: informe o id do próximo ou use narrateEnd/sayEnd.");
                }
                next = draft.lines.get(index + 1).id();
            }
        }
        return new Dialogue(line.id(), line.speaker(), line.expression(), line.text(), next);
    }

    private StoryBuilder add(Line line) {
        if (current == null) {
            throw new IllegalStateException("Declare uma cena com scene(...) antes de adicionar o diálogo '" + line.id() + "'.");
        }
        current.lines.add(line);
        return this;
    }

    private static String requireNext(String id, String nextId) {
        return Objects.requireNonNull(nextId, "O diálogo '" + id + "' recebeu próximo nulo; use narrateEnd/sayEnd para encerrar.");
    }
}
