package main;

import controller.GameManager;
import controller.InputManager;
import controller.MenuController;
import model.GameStates;
import model.Player;
import repository.ItemRepository;
import repository.StateRepository;
import service.PlayerService;
import view.MenuView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {

    public static void main(String[] args) {
        AppContext context = new AppContext();
        InputManager inputManager = new InputManager();
        MenuView menuView = new MenuView();
        MenuController menuController = context.createMenuController(inputManager);
        PlayerService playerService = context.getPlayerService();
        ItemRepository itemRepository = context.getItemRepository();
        StateRepository stateRepository = context.getStateRepository();

        // Rede de segurança para encerramentos abruptos (Ctrl+C, terminal fechado).
        // archiveAutoSaveToEmptySlot() é idempotente: se a opção "Sair" já arquivou, não duplica.
        Runtime.getRuntime().addShutdownHook(new Thread(stateRepository::archiveAutoSaveToEmptySlot));

        menuView.printMessage("inicializando jogo...\n");

        boolean systemRunning = true;

        while (systemRunning) {
            String menuResponse = menuController.start();

            if (menuResponse.equals("novo_jogo")) {
                int targetSlot = menuController.getSelectedSaveSlot();

                menuView.printBloodTypePrompt();
                String tipoSanguineo = inputManager.readBloodType();

                Player player = new Player(tipoSanguineo);
                menuView.printMessage("\nTipo sanguíneo [" + player.getBloodType() + "] registrado com sucesso.");

                GameStates initialState = new GameStates();
                initialState.setCurrentChapterId(1);
                initialState.setCurrentSceneId("Quarto");
                initialState.setCurrentDialogId("d1.1");
                initialState.setSaveTimestamp(LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
                playerService.populateStateFromPlayer(initialState, player);

                stateRepository.saveStateToSlot(initialState, targetSlot);
                stateRepository.saveState(initialState);
                menuView.printMessage("[Sucesso] Novo jogo gravado no Slot " + targetSlot + "!\n");

                GameManager gameManager = context.createGameManager(player, inputManager, initialState);
                gameManager.startGame();
            }
            else if (menuResponse.equals("carregar")) {
                int slot = menuController.getSelectedSaveSlot();
                GameStates savedState = stateRepository.loadStateFromSlot(slot);

                if (savedState != null) {
                    menuView.printMessage("\nRecuperando dados do save (Slot " + (slot == 0 ? "Auto-Save" : slot) + ")...");

                    Player player = playerService.createPlayerFromState(savedState, itemRepository);
                    stateRepository.saveState(savedState);

                    menuView.printMessage("Save carregado com sucesso!\n");

                    GameManager gameManager = context.createGameManager(player, inputManager, savedState);
                    gameManager.loadAndStartGame();
                } else {
                    menuView.printMessage("\n[Erro] Falha ao ler os dados do slot selecionado.\n");
                }
            }
            else if (menuResponse.equals("admin")) {
                Player adminPlayer = new Player("O-");
                GameManager gameManager = context.createGameManager(adminPlayer, inputManager, null);
                gameManager.gameLoop();
            }
            else if (menuResponse.equals("sair")) {
                int archivedSlot = stateRepository.archiveAutoSaveToEmptySlot();

                if (archivedSlot != -1) {
                    menuView.printMessage("\n[Backup] Seu progresso recente foi arquivado no Slot " + archivedSlot + ".");
                }

                menuView.printMessage("finalizando sistema...");
                systemRunning = false;
            }
        }

        inputManager.close();
    }
}
