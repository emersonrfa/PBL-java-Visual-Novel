package service;

import java.util.List;

/**
 * Tudo que uma tela precisa para desenhar o diálogo atual, já resolvido (sem lógica de jogo):
 * cena, fundo, quem fala, sprite, texto e escolhas com disponibilidade.
 * {@code background} e {@code sprite} são caminhos de recurso do classpath, ou nulos se não houver imagem.
 */
public record DialogueSnapshot(
        int chapterNumber,
        String chapterTitle,
        boolean chapterChanged,
        String sceneTitle,
        String sceneDescription,
        boolean sceneChanged,
        String background,
        String dialogueId,
        String speaker,
        String expression,
        String sprite,
        String text,
        boolean narration,
        boolean linear,
        List<ChoiceView> choices) {

    /** Uma opção para exibir; {@code blockReason} é nulo se disponível. */
    public record ChoiceView(int index, String text, boolean available, String blockReason) {}
}
