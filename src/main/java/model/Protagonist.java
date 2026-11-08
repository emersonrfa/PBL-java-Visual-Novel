package model;

import java.util.Set;

/**
 * A protagonista. O roteiro às vezes a chama de "Player" ("Emily" nas falas dela em outras
 * cenas); para sprites e para o nome exibido ambos valem como {@value #NAME}.
 */
public final class Protagonist {
    public static final String NAME = "Emily";
    private static final Set<String> ALIASES = Set.of("player", "jogador", "jogadora", "emily");

    private Protagonist() {}

    public static boolean isProtagonist(String speaker) {
        return speaker != null && ALIASES.contains(speaker.trim().toLowerCase());
    }

    /** Nome para exibir/usar em arquivos: "Player" vira "Emily"; os demais ficam como estão. */
    public static String canonicalName(String speaker) {
        return isProtagonist(speaker) ? NAME : speaker;
    }
}
