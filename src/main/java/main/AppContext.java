package main;

import controller.DialogueController;
import controller.EndingManager;
import controller.GameManager;
import controller.InputManager;
import controller.MenuController;
import model.GameConfig;
import model.GameStates;
import model.Player;
import repository.AchievementRepository;
import repository.ChapterCatalog;
import repository.ConfigRepository;
import repository.EndingRepository;
import repository.GamePaths;
import repository.ItemRepository;
import repository.StateRepository;
import repository.StoryRepository;
import repository.UnlockedAchievementsRepository;
import repository.UnlockedEndingsRepository;
import service.AchievementService;
import service.AffinityTracker;
import service.AssetResolver;
import service.GameSession;
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
    // roda antes dos campos abaixo: leva arquivos de versões antigas para data/
    { GamePaths.migrateLegacyFiles(); }

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

    public GameConfig getConfig() {
        return configRepository.getCurrentConfig();
    }

    /**
     * Cria a partida (modo campanha) para uma interface gráfica. Se {@code restoreFrom} não for
     * nulo, as afinidades são restauradas dele; quem chama inicia com {@code session.start(dialogId)}.
     */
    public GameSession createGameSession(Player player, int chapterNumber, GameStates restoreFrom) {
        AffinityTracker tracker = new AffinityTracker();
        if (restoreFrom != null) {
            playerService.restoreAffinityFromState(restoreFrom, tracker);
        }
        return new GameSession(chapterCatalog, chapterNumber, true, player, tracker, configRepository.getCurrentConfig(),
                stateRepository, achievementService, endingManager.getService(), playerService, new AssetResolver());
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
