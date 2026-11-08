package service;

import model.Achievement;
import service.EndingService.EndingOutcome;

import java.util.List;

/**
 * O que aconteceu após iniciar, avançar ou escolher no {@link GameSession}.
 *
 * <ul>
 *   <li>SHOWING – há um diálogo para mostrar em {@code snapshot}</li>
 *   <li>ENDING – chegou a um final ({@code ending})</li>
 *   <li>CHAPTER_END – o capítulo acabou (próximo vazio)</li>
 *   <li>BLOCKED – escolha recusada por requisitos; continua no mesmo diálogo ({@code message} = motivo)</li>
 *   <li>ERROR – id inexistente ou diálogo sem continuação ({@code message} descreve)</li>
 * </ul>
 *
 * {@code report} e {@code choiceAchievements} vêm da escolha feita; {@code dialogueAchievements} do
 * diálogo recém-exibido (nessa ordem na tela).
 */
public record StepResult(
        Status status,
        DialogueSnapshot snapshot,
        ConsequenceReport report,
        List<Achievement> choiceAchievements,
        List<Achievement> dialogueAchievements,
        EndingOutcome ending,
        String message) {

    public enum Status { SHOWING, ENDING, CHAPTER_END, BLOCKED, ERROR }

    public boolean isShowing() {
        return status == Status.SHOWING;
    }

    /** Verdadeiro quando a sessão não tem mais diálogo a mostrar. */
    public boolean isOver() {
        return status == Status.ENDING || status == Status.CHAPTER_END || status == Status.ERROR;
    }
}
