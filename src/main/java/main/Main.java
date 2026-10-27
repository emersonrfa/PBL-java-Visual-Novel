package main;

import Controller.GameManager;
import Controller.InputManager;
import Controller.MenuController;
import Model.GameStates;
import Model.Player;
import Repository.ItemRepository;
import Repository.StateRepository;
import Service.PlayerService;
import View.MenuView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Main {

    public static void main(String[] args) {
        InputManager inputManager = new InputManager();
        MenuView menuView = new MenuView();
        MenuController menuController = new MenuController(inputManager);
        PlayerService playerService = new PlayerService();
        ItemRepository itemRepository = new ItemRepository();

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            StateRepository repo = new StateRepository();
            repo.archiveAutoSaveToEmptySlot();
        }));

        menuView.printMessage("inicializando jogo...\n");

        boolean systemRunning = true;

        while (systemRunning) {
            String menuResponse = menuController.start();

            if (menuResponse.equals("novo_jogo")) {
                int targetSlot = menuController.getSelectedSaveSlot();
                StateRepository stateRepository = menuController.getStateRepo();

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

                GameManager gameManager = new GameManager(player, inputManager, initialState);
                gameManager.startGame();
            }
            else if (menuResponse.equals("carregar")) {
                int slot = menuController.getSelectedSaveSlot();
                StateRepository stateRepository = menuController.getStateRepo();
                GameStates savedState = stateRepository.loadStateFromSlot(slot);

                if (savedState != null) {
                    menuView.printMessage("\nRecuperando dados do save (Slot " + (slot == 0 ? "Auto-Save" : slot) + ")...");

                    Player player = playerService.createPlayerFromState(savedState, itemRepository);
                    stateRepository.saveState(savedState);

                    menuView.printMessage("Save carregado com sucesso!\n");

                    GameManager gameManager = new GameManager(player, inputManager, savedState);
                    gameManager.loadAndStartGame();
                } else {
                    menuView.printMessage("\n[Erro] Falha ao ler os dados do slot selecionado.\n");
                }
            }
            else if (menuResponse.equals("admin")) {
                Player adminPlayer = new Player("O-");
                GameManager gameManager = new GameManager(adminPlayer, inputManager);
                gameManager.gameLoop();
            }
            else if (menuResponse.equals("sair")) {
                StateRepository stateRepository = new StateRepository();
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