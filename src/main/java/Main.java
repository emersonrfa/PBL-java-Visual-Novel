   import Controller.GameManager;
import Controller.InputManager;
import Controller.MenuController;
import Model.GameStates;
import Model.Item;
import Model.Player;
import Repository.ItemRepository;
import Repository.StateRepository;
import View.MenuView;

public class Main {

    public static void main(String[] args) {
        InputManager inputManager = new InputManager();
        MenuView menuView = new MenuView();
        MenuController menuController = new MenuController(inputManager);

        // Hook de segurança: garante o arquivamento mesmo se a aplicação for parada abruptamente
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            StateRepository repo = new StateRepository();
            repo.archiveAutoSaveToEmptySlot();
        }));

        menuView.printMessage("inicializando jogo...\n");

        boolean systemRunning = true;

        while (systemRunning) {
            String menuResponse = menuController.start();

            if (menuResponse.equals("jogar")) {
                // FLUXO DE NOVO JOGO
                menuView.printBloodTypePrompt();
                String tipoSanguineo = inputManager.readBloodType();

                Player player = new Player(tipoSanguineo);
                menuView.printMessage("\nTipo sanguíneo [" + player.getBloodType() + "] registrado com sucesso.\n");

                GameManager gameManager = new GameManager(player, inputManager);
                gameManager.startGame();
            }
            else if (menuResponse.equals("carregar")) {
                // FLUXO DE CARREGAR JOGO SALVO
                int slot = menuController.getSelectedSaveSlot();
                StateRepository stateRepository = menuController.getStateRepo();
                GameStates savedState = stateRepository.loadStateFromSlot(slot);

                if (savedState != null) {
                    menuView.printMessage("\nRecuperando dados do save (Slot " + (slot == 0 ? "Auto-Save" : slot) + ")...");

                    // 1. Reconstrói o Player com o tipo sanguíneo salvo
                    String bloodType = (savedState.getPlayerBloodType() != null)
                            ? savedState.getPlayerBloodType()
                            : "A+";
                    Player player = new Player(bloodType);

                    // 2. Restaura dinheiro e energia
                    player.setMoney(savedState.getPlayerMoney());
                    player.setEnergy(savedState.getPlayerEnergy());

                    // 3. Restaura itens do inventário via ItemRepository (respeitando MVC)
                    if (savedState.getPlayerInventoryItems() != null) {
                        ItemRepository itemRepository = new ItemRepository();

                        for (String itemName : savedState.getPlayerInventoryItems()) {
                            Item item = itemRepository.findByName(itemName);
                            if (item != null) {
                                player.getInventory().addItem(item);
                            } else {
                                System.err.println("[Aviso] Item não catalogado no ItemRepository: " + itemName);
                            }
                        }
                    }

                    // 4. Sincroniza as flags narrativas salvas
                    if (savedState.getStoryFlags() != null) {
                        for (var entry : savedState.getStoryFlags().entrySet()) {
                            player.setFlag(entry.getKey(), entry.getValue());
                        }
                    }

                    menuView.printMessage("Save carregado com sucesso!\n");

                    // 5. Inicia o GameManager a partir do ponto salvo
                    GameManager gameManager = new GameManager(player, inputManager);
                    gameManager.loadAndStartGame();
                } else {
                    menuView.printMessage("\n[Erro] Falha ao ler os dados do slot selecionado.\n");
                }
            }
            else if (menuResponse.equals("sair")) {
                // Arquiva o auto-save no primeiro slot manual livre antes de encerrar
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