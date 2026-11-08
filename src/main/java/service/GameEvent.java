package service;

import event.Observer;
import model.Achievement;

import java.util.List;

/**
 * Eventos publicados pelo {@link GameSession}. Quem se registra com
 * {@link GameSession#addObserver(Observer)} recebe todos e escolhe os que interessam
 * (ex.: o HUD reage a {@link StatsChanged}; um aviso na tela, a {@link AchievementUnlocked}).
 */
public sealed interface GameEvent {
    record ChapterStarted(int number, String title) implements GameEvent {}

    record DialogueShown(DialogueSnapshot snapshot) implements GameEvent {}

    /** Uma escolha foi aplicada; {@code report} descreve tudo que mudou. */
    record ChoiceApplied(ConsequenceReport report) implements GameEvent {}

    record StatsChanged(int energy, double money) implements GameEvent {}

    record InventoryChanged(List<String> itemNames) implements GameEvent {}

    record AchievementUnlocked(Achievement achievement) implements GameEvent {}

    record EndingReached(EndingService.EndingOutcome outcome) implements GameEvent {}

    record ChapterFinished(int number) implements GameEvent {}

    record GameSaved(int slot) implements GameEvent {}
}
