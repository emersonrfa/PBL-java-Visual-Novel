package Controller;

import Exception.CorruptedSaveException;
import Exception.GamePersistenceException;
import Exception.SaveSlotNotFoundException;
import Model.Ending;
import Model.GameConfig;
import Model.GameStates;
import Repository.ConfigRepository;
import Repository.EndingRepository;
import Repository.StateRepository;
import Repository.UnlockedEndingsRepository;
import Service.AchievementService;
import View.AchievementView;
import View.MenuView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MenuController {
    private String[] options = {
            "Jogar",
            "Galeria de Finais",
            "Galeria de Conquistas",
            "Configurações",
            "Créditos",
            "Sair"
    };
    private int selectedOption = 0;
    private MenuView view;
    private AchievementView achievementView;
    private InputManager inputManager;
    private ConfigRepository configRepo;
    private StateRepository stateRepo;
    private EndingRepository endingRepo;
    private UnlockedEndingsRepository unlockedEndingsRepo;
    private AchievementService achievementService;
    private int selectedSaveSlot = -1;

    public MenuController(InputManager inputManager) {
        this(inputManager, new ConfigRepository());
    }

    public MenuController(InputManager inputManager, ConfigRepository configRepo) {
        this.inputManager = inputManager;
        this.view = new MenuView();
        this.achievementView = new AchievementView();
        this.configRepo = configRepo;
        this.stateRepo = new StateRepository();
        this.endingRepo = new EndingRepository();
        this.unlockedEndingsRepo = new UnlockedEndingsRepository();
        this.achievementService = new AchievementService();
    }

    public String start() {
        boolean running = true;

        while (running) {
            view.printMenu(options, selectedOption);
            String input = inputManager.readString().trim().toUpperCase();

            // Gatilho secreto ADMIN para seleção de capítulos
            if (input.equals("ADMIN")) {
                view.printMessage("\n[Modo Admin] Abrindo seleção de capítulos...");
                return "admin";
            }

            if (input.equals("W")) {
                selectedOption--;
                if (selectedOption < 0) {
                    selectedOption = options.length - 1;
                }
            } else if (input.equals("S")) {
                selectedOption++;
                if (selectedOption >= options.length) {
                    selectedOption = 0;
                }
            } else if (input.equals("E")) {
                switch (selectedOption) {
                    case 0: // Jogar -> Submenu (Novo Jogo / Carregar Save)
                        String playResult = playSubMenuLoop();
                        if (!playResult.equals("voltar")) {
                            return playResult;
                        }
                        break;

                    case 1: // Galeria de Finais
                        endingsGalleryLoop();
                        break;

                    case 2: // Galeria de Conquistas
                        achievementsGalleryLoop();
                        break;

                    case 3: // Configurações
                        configLoop();
                        break;

                    case 4: // Créditos
                        view.printCredits();
                        inputManager.waitEnter();
                        break;

                    case 5: // Sair
                        view.printMessage("Saindo...\n");
                        running = false;
                        return "sair";
                }
            }
        }
        return "sair";
    }

    private String playSubMenuLoop() {
        String[] playOptions = {"Novo Jogo", "Carregar Save", "Voltar"};
        int selected = 0;

        while (true) {
            view.printPlaySubMenu(playOptions, selected);
            String input = inputManager.readString().trim().toUpperCase();

            if (input.equals("W")) {
                selected--;
                if (selected < 0) selected = playOptions.length - 1;
            } else if (input.equals("S")) {
                selected++;
                if (selected >= playOptions.length) selected = 0;
            } else if (input.equals("E")) {
                switch (selected) {
                    case 0: // Novo Jogo (escolher slot para criar/sobrescrever)
                        if (newGameSlotSelectionLoop()) {
                            return "novo_jogo";
                        }
                        break;
                    case 1: // Carregar Save
                        if (savesLoop()) {
                            return "carregar";
                        }
                        break;
                    case 2: // Voltar
                        return "voltar";
                }
            }
        }
    }

    public boolean newGameSlotSelectionLoop() {
        while (true) {
            Map<Integer, GameStates> slotsMap = new HashMap<>();
            for (int i = 1; i <= 4; i++) {
                slotsMap.put(i, stateRepo.loadStateFromSlot(i));
            }

            view.displayNewGameSlotMenu(slotsMap);
            int choice = inputManager.readValidChoice(0, 4);

            if (choice == 0) {
                return false;
            }

            if (stateRepo.slotExists(choice)) {
                view.printOverwriteConfirmation(choice);
                String confirmation = inputManager.readString().trim();
                if (!confirmation.equalsIgnoreCase("S")) {
                    view.printMessage("\n[Cancelado] Escolha outro slot ou digite 0 para voltar.");
                    continue;
                }
            }

            this.selectedSaveSlot = choice;
            view.printMessage("\nSlot " + choice + " preparado para iniciar um Novo Jogo!");
            return true;
        }
    }

    public void achievementsGalleryLoop() {
        achievementView.displayAchievementsGallery(
                achievementService.getAllAchievements(),
                achievementService.getUnlockedRepo()
        );
        inputManager.waitEnter();
    }

    public void endingsGalleryLoop() {
        boolean inGallery = true;
        List<Ending> allEndings = endingRepo.getAllEndings();

        while (inGallery) {
            view.displayEndingsGallery(allEndings, unlockedEndingsRepo);
            int choice = inputManager.readValidChoice(0, allEndings.size());

            if (choice == 0) {
                inGallery = false;
            } else {
                Ending chosen = allEndings.get(choice - 1);
                if (unlockedEndingsRepo.isUnlocked(chosen.getId())) {
                    view.printEndingDetails(chosen);
                } else {
                    view.printMessage("\n[Bloqueado] Você ainda não descobriu este final na história!");
                }
                view.printMessage("\n[Pressione ENTER para voltar à galeria]");
                inputManager.waitEnter();
            }
        }
    }

    public boolean savesLoop() {
        boolean inSaves = true;

        while (inSaves) {
            Map<Integer, GameStates> slotsMap = new HashMap<>();
            for (int i = 0; i <= 4; i++) {
                slotsMap.put(i, stateRepo.loadStateFromSlot(i));
            }

            view.displaySavesMenu(slotsMap);
            int choice = inputManager.readValidChoice(0, 9);

            if (choice == 9) {
                inSaves = false;
                return false;
            } else if (choice == 8) {
                handleDeleteSave();
            } else if (choice >= 0 && choice <= 4) {
                try {
                    stateRepo.loadStateFromSlotChecked(choice);
                    this.selectedSaveSlot = choice;
                    view.printMessage("\nSave do Slot " + (choice == 0 ? "Auto-Save" : choice) + " validado e selecionado com sucesso!");
                    return true;
                } catch (SaveSlotNotFoundException e) {
                    view.printMessage("\n[Aviso] " + e.getMessage() + " Escolha outro slot ou pressione 9 para voltar.");
                } catch (CorruptedSaveException e) {
                    view.printMessage("\n[Erro - Save Corrompido] " + e.getMessage());
                } catch (GamePersistenceException e) {
                    view.printMessage("\n[Erro de Leitura] " + e.getMessage());
                }
            } else {
                view.printMessage("\nOpção inválida! Escolha um slot entre 0 e 4, 8 para excluir ou 9 para voltar.");
            }
        }

        return false;
    }

    public void handleDeleteSave() {
        Map<Integer, GameStates> slotsMap = new HashMap<>();
        for (int i = 0; i <= 4; i++) {
            slotsMap.put(i, stateRepo.loadStateFromSlot(i));
        }

        view.displayDeleteMenu(slotsMap);
        int choice = inputManager.readValidChoice(0, 9);

        if (choice == 9) {
            view.printMessage("\nOperação de exclusão cancelada.");
            return;
        }

        if (choice < 0 || choice > 4) {
            view.printMessage("\n[Aviso] Slot inválido!");
            return;
        }

        if (!stateRepo.slotExists(choice)) {
            view.printMessage("\n[Aviso] O Slot " + choice + " já está vazio. Nada para deletar.");
            return;
        }

        view.printDeleteConfirmation(choice);
        String confirmation = inputManager.readString().trim();

        if (confirmation.equalsIgnoreCase("S")) {
            try {
                stateRepo.deleteSlotChecked(choice);
                view.printMessage("\n[Sucesso] O save do Slot " + (choice == 0 ? "Auto-Save" : choice) + " foi deletado com sucesso!");
            } catch (GamePersistenceException e) {
                view.printMessage("\n[Erro] " + e.getMessage());
            }
        } else {
            view.printMessage("\n[Cancelado] A exclusão do Slot " + choice + " foi cancelada.");
        }

        view.printMessage("\n[Pressione ENTER para continuar]");
        inputManager.waitEnter();
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
            String input = inputManager.readString().trim().toUpperCase();

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