package service;

import model.Chapter;
import model.Choice;
import model.Dialogue;
import model.Ending;
import model.StoryScene;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Verifica a consistência do grafo de diálogos de um capítulo, sem executar o jogo.
 *
 * Erros: ids duplicados, escolhas que apontam para ids inexistentes, diálogos sem nenhuma
 * escolha (travariam o jogo) e diálogos inalcançáveis a partir do início do capítulo.
 * Referências a ids declarados como pendentes ({@link Chapter#markPending}) são só avisos.
 */
public final class StoryValidator {

    public enum Type { EMPTY_CHAPTER, DUPLICATE_ID, MISSING_TARGET, PENDING_TARGET, DEAD_END, UNREACHABLE }

    public record Issue(Type type, String dialogueId, String message) {
        /** Pendências de roteiro são avisos; todo o resto é erro. */
        public boolean isError() {
            return type != Type.PENDING_TARGET;
        }

        @Override
        public String toString() {
            return type + " [" + dialogueId + "]: " + message;
        }
    }

    private StoryValidator() {}

    public static List<Issue> validate(Chapter chapter) {
        List<Issue> issues = new ArrayList<>();
        Map<String, Dialogue> byId = new HashMap<>();
        List<Dialogue> inOrder = new ArrayList<>();

        for (StoryScene scene : chapter.getScenes()) {
            for (Dialogue dialogue : scene.getDialogues()) {
                inOrder.add(dialogue);
                if (byId.putIfAbsent(dialogue.getId(), dialogue) != null) {
                    issues.add(new Issue(Type.DUPLICATE_ID, dialogue.getId(),
                            "id repetido no capítulo " + chapter.getChapterNumber()));
                }
            }
        }

        if (inOrder.isEmpty()) {
            issues.add(new Issue(Type.EMPTY_CHAPTER, "-", "o capítulo " + chapter.getChapterNumber() + " não tem diálogos"));
            return issues;
        }

        Set<String> pending = chapter.getPendingDialogueIds();

        for (Dialogue dialogue : inOrder) {
            if (!dialogue.hasChoices()) {
                issues.add(new Issue(Type.DEAD_END, dialogue.getId(),
                        "não tem nenhuma escolha/continuação; o jogo travaria aqui"));
                continue;
            }
            for (Choice choice : dialogue.getChoices()) {
                String target = choice.getNextDialogueId();
                if (isEndOfChapter(target) || isEndingCode(target) || byId.containsKey(target)) {
                    continue;
                }
                if (pending.contains(target)) {
                    issues.add(new Issue(Type.PENDING_TARGET, dialogue.getId(),
                            "aponta para '" + target + "', ainda a ser escrito"));
                } else {
                    issues.add(new Issue(Type.MISSING_TARGET, dialogue.getId(),
                            "aponta para '" + target + "', que não existe no capítulo"));
                }
            }
        }

        Set<String> reachable = reachableFromStart(inOrder.get(0), byId);
        for (Dialogue dialogue : inOrder) {
            if (!reachable.contains(dialogue.getId())) {
                issues.add(new Issue(Type.UNREACHABLE, dialogue.getId(), "nenhum caminho a partir do início chega aqui"));
            }
        }
        return issues;
    }

    /** Somente os problemas que devem ser corrigidos (ignora avisos de roteiro pendente). */
    public static List<Issue> errors(Chapter chapter) {
        List<Issue> errors = new ArrayList<>();
        for (Issue issue : validate(chapter)) {
            if (issue.isError()) errors.add(issue);
        }
        return errors;
    }

    private static boolean isEndOfChapter(String target) {
        return target == null || target.isBlank();
    }

    private static boolean isEndingCode(String target) {
        return target != null && target.startsWith(Ending.CODE_PREFIX);
    }

    private static Set<String> reachableFromStart(Dialogue start, Map<String, Dialogue> byId) {
        Set<String> visited = new LinkedHashSet<>();
        Deque<Dialogue> queue = new ArrayDeque<>();
        queue.add(start);
        visited.add(start.getId());
        while (!queue.isEmpty()) {
            for (Choice choice : queue.poll().getChoices()) {
                Dialogue next = byId.get(choice.getNextDialogueId());
                if (next != null && visited.add(next.getId())) {
                    queue.add(next);
                }
            }
        }
        return new HashSet<>(visited);
    }
}
