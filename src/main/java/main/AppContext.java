package main;

import controller.DialogueController;
import controller.EndingManager;
import controller.GameManager;
import controller.InputManager;
import controller.MenuController;
import model.GameStates;
import model.Player;
import repository.AchievementRepository;
import repository.ChapterCatalog;
import repository.ConfigRepository;
import repository.EndingRepository;
import repository.ItemRepository;
import repository.StateRepository;
import repository.StoryRepository;
import repository.UnlockedAchievementsRepository;
import repository.UnlockedEndingsRepository;
import service.AchievementService;
import service.AffinityTracker;
import service.PlayerService;

/**
 * Raiz de composição da aplicação: cria UMA instância de cada repositório e serviço e as entrega
 * a quem precisa.
 *
 * Isso evita o problema de várias cópias do mesmo repositório (cada uma com seu próprio Set em
 * memória) sobrescreverem o arquivo umas das outras e perderem conquistas/finais desbloqueados.
 * Controllers e Views não conhecem esta classe: recebem as dependências pelo construtor.
 */
public final class AppContext {
    private final StateRepository stateRepository = new StateRepository();
    private final ConfigRepository configRepository = new ConfigRepository();
    private final ItemRepository itemRepository = new ItemRepository();
    private final ChapterCatalog chapterCatalog = new StoryRepository().buildCatalog();
    private final EndingRepository endingRepository = new EndingRepository();
    private final UnlockedEndingsRepository unlockedEndingsRepository = new UnlockedEndingsRepository();
    private final AchievementService achievementService =
            new AchievementService(new AchievementRepository(), new UnlockedAchievementsRepository());
    private final EndingManager endingManager =
            new EndingManager(endingRepository, unlockedEndingsRepository, achievementService);
    private final PlayerService playerService = new PlayerService();

    public StateRepository getStateRepository() {
        return stateRepository;
    }

    public ItemRepository getItemRepository() {
        return itemRepository;
    }

    public PlayerService getPlayerService() {
        return playerService;
    }

    public MenuController createMenuController(InputManager inputManager) {
        return new MenuController(inputManager, configRepository, stateRepository,
                endingRepository, unlockedEndingsRepository, achievementService);
    }

    /**
     * Monta uma partida. Se {@code activeState} não for nulo, as afinidades são restauradas dele;
     * caso contrário usa o estado atual (autosave) apenas como referência.
     */
    public GameManager createGameManager(Player player, InputManager inputManager, GameStates activeState) {
        AffinityTracker tracker = new AffinityTracker();
        if (activeState != null) {
            playerService.restoreAffinityFromState(activeState, tracker);
        }
        GameStates currentState = (activeState != null) ? activeState : stateRepository.getCurrentStates();

        DialogueController dialogueController = new DialogueController(
                player, inputManager, tracker, configRepository.getCurrentConfig(),
                stateRepository, achievementService, endingManager);

        return new GameManager(dialogueController, createMenuController(inputManager),
                chapterCatalog, endingManager, currentState);
    }
}
