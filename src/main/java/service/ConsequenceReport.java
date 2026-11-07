package service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Resultado estruturado da aplicação de uma escolha: lista as alterações que de fato aconteceram
 * (energia, dinheiro, flags, itens, afinidade e suspeita).
 *
 * A camada de apresentação (console hoje, JavaFX depois) decide como mostrar cada {@link Change};
 * o service não imprime nada nem conhece a interface.
 */
public final class ConsequenceReport {

    public enum Kind { ENERGY, MONEY, FLAG, ITEM_GAINED, ITEM_USED, AFFINITY, SUSPICION }

    /**
     * Uma alteração efetiva. {@code before}/{@code after} valem para grandezas numéricas.
     * {@code message} vazia significa "não exibir ao jogador" (ex.: flags internas da história).
     */
    public record Change(Kind kind, String subject, double before, double after, String message) {
        public double delta() {
            return after - before;
        }

        public boolean isDisplayable() {
            return message != null && !message.isBlank();
        }
    }

    private static final Locale PT_BR = Locale.forLanguageTag("pt-BR");

    private final List<Change> changes = new ArrayList<>();

    // ---- registro (usado pelo PlayerService, mesmo pacote) ----

    void addEnergy(int before, int after) {
        if (before == after) return; // ex.: energia já estava no máximo
        changes.add(new Change(Kind.ENERGY, "Energia", before, after,
                String.format(PT_BR, "[Energia %+d (%d → %d)]", after - before, before, after)));
    }

    void addMoney(double before, double after) {
        if (Double.compare(before, after) == 0) return;
        changes.add(new Change(Kind.MONEY, "Dinheiro", before, after,
                String.format(PT_BR, "[Dinheiro: R$ %.2f → R$ %.2f]", before, after)));
    }

    void addFlag(String flag) {
        changes.add(new Change(Kind.FLAG, flag, 0, 1, ""));
    }

    void addItemGained(String itemName) {
        changes.add(new Change(Kind.ITEM_GAINED, itemName, 0, 1, "[+ " + itemName + "]"));
    }

    void addItemUsed(String itemName) {
        changes.add(new Change(Kind.ITEM_USED, itemName, 1, 0, "[-1 " + itemName + " (Utilizado)]"));
    }

    void addAffinity(String npc, int before, int after) {
        if (before == after) return;
        changes.add(new Change(Kind.AFFINITY, npc, before, after,
                String.format(PT_BR, "[Afinidade com %s: %d → %d]", npc, before, after)));
    }

    void addSuspicion(String npc, int before, int after) {
        if (before == after) return;
        changes.add(new Change(Kind.SUSPICION, npc, before, after,
                String.format(PT_BR, "[Suspeita de %s: %d → %d]", npc, before, after)));
    }

    // ---- consulta ----

    public List<Change> getChanges() {
        return Collections.unmodifiableList(changes);
    }

    public List<Change> getChanges(Kind kind) {
        List<Change> result = new ArrayList<>();
        for (Change change : changes) {
            if (change.kind() == kind) result.add(change);
        }
        return result;
    }

    /** Mensagens prontas para exibir ao jogador (ignora as alterações internas, como flags). */
    public List<String> messages() {
        List<String> result = new ArrayList<>();
        for (Change change : changes) {
            if (change.isDisplayable()) result.add(change.message());
        }
        return result;
    }

    public boolean isEmpty() {
        return changes.isEmpty();
    }
}
