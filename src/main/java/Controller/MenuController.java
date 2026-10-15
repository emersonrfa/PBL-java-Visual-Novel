package Controller;

import Model.GameConfig;
import Repository.ConfigRepository;
import View.MenuView;
import java.util.List;

public class MenuController {
    private String[] options = {"Jogar", "Saves", "Configurações", "Creditos", "Sair"};
    private int selectedOption = 0;
    private MenuView view;
    private InputManager inputManager;
    private ConfigRepository configRepo;

    public MenuController(InputManager inputManager) {
        this.inputManager = inputManager;
        this.view = new MenuView();
        this.configRepo = new ConfigRepository();
    }

    public MenuController(InputManager inputManager, ConfigRepository configRepo) {
        this.inputManager = inputManager;
        this.view = new MenuView();
        this.configRepo = configRepo;
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
                    case 0: // Jogar
                        view.printMessage("iniciando jogo....\n");
                        return "jogar";

                    case 1: // Saves (apenas exibe e aguarda Enter para voltar)
                        view.displaySavesMenu();
                        inputManager.waitEnter();
                        break;

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

    public ConfigRepository getConfigRepo() {
        return configRepo;
    }
}