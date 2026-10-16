package Controller;

import Model.GameConfig;
import Model.GameStates;
import Repository.ConfigRepository;
import Repository.StateRepository;
import View.MenuView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuController {
    private String[] options = {"Jogar", "Saves", "Configuracoes", "Creditos", "Sair"};
    private int selectedOption = 0;
    private MenuView view;
    private InputManager inputManager;
    private ConfigRepository configRepo;
    private StateRepository stateRepo;
    private int selectedSaveSlot = -1; // Guarda qual slot o jogador escolheu para carregar

    public MenuController(InputManager inputManager) {
        this.inputManager = inputManager;
        this.view = new MenuView();
        this.configRepo = new ConfigRepository();
        this.stateRepo = new StateRepository();
    }

    public MenuController(InputManager inputManager, ConfigRepository configRepo) {
        this.inputManager = inputManager;
        this.view = new MenuView();
        this.configRepo = configRepo;
        this.stateRepo = new StateRepository();
    }

    public String start() {
        boolean running = true;

        while (running) {
            view.printMenu(options, selectedOption);
            String input = inputManager.readString().toUpperCase();

            if (input.equals("W")) {
                selectedOption--;
                if (selectedOption < 0) {
                    selectedOption = options.length - 1;
                }
            }
            else if (input.equals("S")) {
                selectedOption++;
                if (selectedOption >= options.length) {
                    selectedOption = 0;
                }
            }
            else if (input.equals("E")) {
                switch (selectedOption) {
                    case 0: // Jogar (Novo Jogo)
                        view.printMessage("iniciando novo jogo....\n");
                        return "jogar";

                    case 1: // Menu de Saves
                        boolean loaded = savesLoop();
                        if (loaded) {
                            return "carregar"; // Sinaliza para o Main carregar o slot escolhido
                        }
                        break; // Se escolheu voltar (9), redesenha o menu principal

                    case 2: // Configurações
                        configLoop();
                        break;

                    case 3: // Créditos
                        view.printCredits();
                        inputManager.waitEnter();
                        break;

                    case 4: // Sair
                        view.printMessage("saindo...\n");
                        running = false;
                        return "sair";
                }
            }
        }
        return "sair";
    }

    /**
     * Gerencia a tela e escolha dos slots de save.
     * @return true se o jogador escolheu um save válido para carregar; false se voltou ao menu.
     */
    public boolean savesLoop() {
        boolean inSaves = true;

        while (inSaves) {
            // Monta o mapa dos slots (0 = autosave, 1 a 4 = manuais)
            Map<Integer, GameStates> slotsMap = new HashMap<>();
            for (int i = 0; i <= 4; i++) {
                slotsMap.put(i, stateRepo.loadStateFromSlot(i));
            }

            view.displaySavesMenu(slotsMap);

            // Permite escolher 0, 1, 2, 3, 4 ou 9 (voltar)
            int choice = inputManager.readValidChoice(0, 9);

            if (choice == 9) {
                inSaves = false;
                return false;
            } else if (choice >= 0 && choice <= 4) {
                if (stateRepo.slotExists(choice)) {
                    this.selectedSaveSlot = choice;
                    view.printMessage("\nSave do Slot " + (choice == 0 ? "Auto-Save" : choice) + " selecionado com sucesso!");
                    return true;
                } else {
                    view.printMessage("\n[Aviso] O Slot selecionado está vazio. Escolha outro slot ou pressione 9 para voltar.");
                }
            } else {
                view.printMessage("\nOpção inválida! Escolha um slot entre 0 e 4, ou 9 para voltar.");
            }
        }

        return false;
    }

    public int chapterSelection() {
        view.displayChapterSelection();
        return inputManager.readValidChoice(0, 11);
    }

    public int endingSelection(List<String> endingTitles) {
        view.displayEndingSelection(endingTitles);
        return inputManager.readValidChoice(0, endingTitles.size());
    }

    public void configLoop() {
        String[] configOptions = {
                "Velocidade do Texto",
                "Confirmar Escolhas",
                "Música",
                "Salvamento Automático",
                "Voltar"
        };
        int selected = 0;
        boolean inConfig = true;

        GameConfig config = configRepo.getCurrentConfig();

        while (inConfig) {
            view.printConfigMenu(configOptions, selected, config);
            String input = inputManager.readString().toUpperCase();

            if (input.equals("W")) {
                selected--;
                if (selected < 0) selected = configOptions.length - 1;
            } else if (input.equals("S")) {
                selected++;
                if (selected >= configOptions.length) selected = 0;
            } else if (input.equals("E")) {
                switch (selected) {
                    case 0:
                        int currentSpeed = config.getTextSpeedMs();
                        if (currentSpeed == 0) config.setTextSpeedMs(10);
                        else if (currentSpeed == 10) config.setTextSpeedMs(25);
                        else if (currentSpeed == 25) config.setTextSpeedMs(50);
                        else config.setTextSpeedMs(0);
                        configRepo.saveConfig(config);
                        break;
                    case 1:
                        config.setConfirmChoices(!config.isConfirmChoices());
                        configRepo.saveConfig(config);
                        break;
                    case 2:
                        config.setTurnMusic(!config.isTurnMusic());
                        configRepo.saveConfig(config);
                        break;
                    case 3:
                        config.setTurnAutoSave(!config.isTurnAutoSave());
                        configRepo.saveConfig(config);
                        break;
                    case 4:
                        inConfig = false;
                        break;
                }
            }
        }
    }

    public int getSelectedSaveSlot() {
        return selectedSaveSlot;
    }

    public ConfigRepository getConfigRepo() {
        return configRepo;
    }

    public StateRepository getStateRepo() {
        return stateRepo;
    }
}