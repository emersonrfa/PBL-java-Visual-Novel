package controller;

import model.Achievement;
import model.Ending;
import repository.EndingRepository;
import repository.UnlockedEndingsRepository;
import service.AchievementService;
import service.EndingService;
import view.AchievementView;
import view.EndingView;

import java.util.List;

/**
 * Apresentação de finais no console: delega as regras ao {@link EndingService} e imprime o resultado.
 * Uma tela JavaFX usa o EndingService diretamente.
 */
public class EndingManager {
    private final EndingService service;
    private final EndingView view = new EndingView();
    private final AchievementView achievementView = new AchievementView();

    /** Recebe as instâncias compartilhadas da aplicação (ver main.AppContext). */
    public EndingManager(EndingRepository endingRepo,
                         UnlockedEndingsRepository unlockedEndingsRepo,
                         AchievementService achievementService) {
        this.service = new EndingService(endingRepo, unlockedEndingsRepo, achievementService);
    }

    public EndingService getService() {
        return service;
    }

    public boolean isEndingCode(String dialogueId) {
        return service.isEndingCode(dialogueId);
    }

    public void triggerEnding(String endingId) {
        print(service.reach(endingId));
    }

    /** Imprime um final já processado (usado quando o GameSession o desbloqueou). */
    public void print(EndingService.EndingOutcome outcome) {
        if (!outcome.found()) {
            view.printEndingNotFoundWarning(outcome.endingId());
            return;
        }
        view.printEnding(outcome.ending());
        for (Achievement achievement : outcome.newAchievements()) {
            achievementView.printAchievementBanner(achievement);
        }
    }

    public List<Ending> getAllEndings() {
        return service.getAllEndings();
    }
}
