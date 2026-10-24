package View;

import Model.GameConfig;
import Model.GameStates;

import java.util.List;
import java.util.Map;

// Classe responsável por fazer os prints do menu
public class MenuView {

    public void printMenu(String[] options, int selectedOption) {
        System.out.println("\n---- MENU INICIAL ----");
        System.out.println("W para subir, S para descer e E para confirmar");

        for (int i = 0; i < options.length; i++) {
            if (i == selectedOption) {
                System.out.println("-> " + options[i]);
            } else {
                System.out.println("   " + options[i]);
            }
        }
        System.out.print("Esperando entrada:\n> ");
    }

    /**
     * Exibe os 5 slots de save (0 = Auto-Save, 1 a 4 = Slots Manuais)
     *
     * @param slotsMap Mapa contendo o número do slot (0 a 4) e o respectivo GameStates (ou null se vazio)
     */
    public void displaySavesMenu(Map<Integer, GameStates> slotsMap) {
        System.out.println("\n==========================================================================================================");
        System.out.println("                                           ARQUIVOS DE SAVE                                               ");
        System.out.println("==========================================================================================================");

        // Slot 0: Auto-Save
        printSlotDetails(0, "Auto-Save", slotsMap.get(0));

        System.out.println("----------------------------------------------------------------------------------------------------------");

        // Slots 1 a 4: Saves Manuais
        for (int i = 1; i <= 4; i++) {
            printSlotDetails(i, "Slot " + i, slotsMap.get(i));
        }

        System.out.println("==========================================================================================================");
        System.out.println(" Digite o número do slot (0 a 4) para selecionar");
        System.out.println(" 8 - Excluir um save");
        System.out.println(" 9 - Voltar ao Menu Principal");
        System.out.println("==========================================================================================================");
        System.out.print("Escolha uma opção:\n> ");
    }

    /**
     * Exibe os slots para que o jogador escolha qual arquivo deseja excluir.
     *
     * @param slotsMap Mapa contendo o número do slot (0 a 4) e o respectivo GameStates (ou null se vazio)
     */
    public void displayDeleteMenu(Map<Integer, GameStates> slotsMap) {
        System.out.println("\n==========================================================================================================");
        System.out.println("                                          EXCLUIR ARQUIVO DE SAVE                                         ");
        System.out.println("==========================================================================================================");

        // Slot 0: Auto-Save
        printSlotDetails(0, "Auto-Save", slotsMap.get(0));

        System.out.println("----------------------------------------------------------------------------------------------------------");

        // Slots 1 a 4: Saves Manuais
        for (int i = 1; i <= 4; i++) {
            printSlotDetails(i, "Slot " + i, slotsMap.get(i));
        }

        System.out.println("==========================================================================================================");
        System.out.println(" Digite o número do slot (0 a 4) que deseja EXCLUIR");
        System.out.println(" 9 - Cancelar e voltar");
        System.out.println("==========================================================================================================");
        System.out.print("Escolha uma opção:\n> ");
    }

    /**
     * Exibe o diálogo de confirmação prévia antes de apagar permanentemente um save.
     */
    public void printDeleteConfirmation(int slot) {
        String slotLabel = (slot == 0) ? "Auto-Save (Slot 0)" : "Slot " + slot;
        System.out.println("\n[ATENÇÃO] Tem certeza de que deseja deletar permanentemente o " + slotLabel + "?");
        System.out.print("Digite 'S' para confirmar ou 'N' para cancelar:\n> ");
    }

    private void printSlotDetails(int slotIndex, String label, GameStates state) {
        if (state != null) {
            String saveName = (state.getSaveName() != null && !state.getSaveName().isEmpty()) ? state.getSaveName() : "Emily";
            String blood = state.getPlayerBloodType() != null ? state.getPlayerBloodType() : "N/D";
            int itemsCount = state.getPlayerInventoryItems() != null ? state.getPlayerInventoryItems().size() : 0;
            String timestamp = (state.getSaveTimestamp() != null && !state.getSaveTimestamp().trim().isEmpty())
                    ? state.getSaveTimestamp()
                    : "Sem registro";

            System.out.printf(" [%d] %-10s | Nome: %-8s | Cap: %-2d (%-15s) | Resumo: [Energia: %3d%%, R$: %5.2f, Sangue: %-3s, Itens: %-2d] | Data: %s\n",
                    slotIndex,
                    label,
                    saveName,
                    state.getCurrentChapterId(),
                    state.getCurrentSceneId() != null ? state.getCurrentSceneId() : "N/A",
                    state.getPlayerEnergy(),
                    state.getPlayerMoney(),
                    blood,
                    itemsCount,
                    timestamp
            );
        } else {
            System.out.printf(" [%d] %-10s | [Vazio ou Indisponível]\n", slotIndex, label);
        }
    }

    public void displayChapterSelection() {
        System.out.println("\n==================================================");
        System.out.println("               SELEÇÃO DE CAPÍTULO                ");
        System.out.println("==================================================");
        System.out.println(" 1 - Capítulo 1: O Prólogo");
        System.out.println(" 2 - Capítulo 2: A entrevista de emprego");
        System.out.println(" 11 - Galeria de Finais");
        System.out.println(" 0 - Voltar / Sair");
        System.out.println("==================================================");
    }

    public void displayEndingSelection(List<String> endingTitles) {
        System.out.println("\n==================================================");
        System.out.println("                 GALERIA DE FINAIS                ");
        System.out.println("==================================================");
        for (int i = 0; i < endingTitles.size(); i++) {
            System.out.println(" " + (i + 1) + " - " + endingTitles.get(i));
        }
        System.out.println(" 0 - Voltar para Seleção de Capítulos");
        System.out.println("==================================================");
    }

    public void displayManualSaveMenu(Map<Integer, GameStates> slotsMap) {
        System.out.println("\n==========================================================================================================");
        System.out.println("                                          SALVAMENTO MANUAL                                               ");
        System.out.println("==========================================================================================================");

        for (int i = 1; i <= 4; i++) {
            printSlotDetails(i, "Slot " + i, slotsMap.get(i));
        }

        System.out.println("==========================================================================================================");
        System.out.println(" Digite o número do slot (1 a 4) para salvar (sobrescrever)");
        System.out.println(" 0 - Cancelar e voltar ao jogo");
        System.out.println("==========================================================================================================");
        System.out.print("Escolha uma opção:\n> ");
    }

    public void printConfigMenu(String[] options, int selectedOption, GameConfig config) {
        System.out.println("\n---- CONFIGURAÇÕES ----");
        System.out.println("W para subir, S para descer e E para alterar/confirmar\n");

        for (int i = 0; i < options.length; i++) {
            String valorAtual = "";
            switch (i) {
                case 0:
                    valorAtual = " [" + (config.getTextSpeedMs() == 0 ? "Instantâneo" : config.getTextSpeedMs() + "ms") + "]";
                    break;
                case 1:
                    valorAtual = " [" + (config.isConfirmChoices() ? "Ativado" : "Desativado") + "]";
                    break;
                case 2:
                    valorAtual = " [" + (config.isTurnMusic() ? "Ligada" : "Desligada") + "]";
                    break;
                case 3:
                    valorAtual = " [" + (config.isTurnAutoSave() ? "Ativado" : "Desativado") + "]";
                    break;
                default:
                    valorAtual = "";
                    break;
            }

            if (i == selectedOption) {
                System.out.println("-> " + options[i] + valorAtual);
            } else {
                System.out.println("   " + options[i] + valorAtual);
            }
        }
        System.out.print("Esperando entrada:\n> ");
    }

    public void printMessage(String message) {
        System.out.println(message);
    }

    public void printBloodTypePrompt() {
        System.out.println("\nQual seu tipo sanguíneo, Emily? (Ex: A+, O-, AB+):");
        System.out.print("> ");
    }

    public void printCredits() {
        System.out.println("\nmostrando creditos...");
        System.out.println("\nFeito por: \nEmerson Rodrigues Farias de Araújo\nDaniel Fontes Aguiar\n\nProjeto" +
                " PBL da matéria de MI - Algoritmos de Programação II.");
        System.out.println("pressione ENTER para voltar ao menu");
    }

    public void printOverwriteConfirmation(int slot) {
        System.out.println("\n[ATENÇÃO] O Slot " + slot + " já possui um save gravado!");
        System.out.print("Deseja realmente sobrescrevê-lo? Digite 'S' para confirmar ou qualquer outra tecla para cancelar:\n> ");
    }

    public void displayEndingsGallery(List<Model.Ending> allEndings, Repository.UnlockedEndingsRepository unlockedRepo) {
        System.out.println("\n==========================================================================================================");
        System.out.println("                                           GALERIA DE FINAIS                                              ");
        System.out.println("==========================================================================================================");

        for (int i = 0; i < allEndings.size(); i++) {
            Model.Ending e = allEndings.get(i);
            boolean unlocked = unlockedRepo.isUnlocked(e.getId());

            if (unlocked) {
                System.out.printf(" [%d] %-40s | [DESBLOQUEADO]\n", (i + 1), e.getTitle());
            } else {
                System.out.printf(" [%d] %-40s | [BLOQUEADO]\n", (i + 1), "???");
            }
        }

        System.out.println("==========================================================================================================");
        System.out.println(" Digite o número do final (1 a " + allEndings.size() + ") para ler a descrição");
        System.out.println(" 0 - Voltar ao Menu Principal");
        System.out.println("==========================================================================================================");
        System.out.print("Escolha uma opção:\n> ");
    }

    /**
     * Exibe os detalhes e a narrativa do final desbloqueado selecionado.
     */
    public void printEndingDetails(Model.Ending ending) {
        System.out.println("\n----------------------------------------------------------------------------------------------------------");
        System.out.println("TÍTULO: " + ending.getTitle());
        System.out.println("----------------------------------------------------------------------------------------------------------");
        System.out.println(ending.getDescription());
        System.out.println("----------------------------------------------------------------------------------------------------------");
    }

    public void printPlaySubMenu(String[] playOptions, int selectedOption) {
        System.out.println("\n---- MODO DE JOGO ----");
        System.out.println("W para subir, S para descer e E para confirmar");

        for (int i = 0; i < playOptions.length; i++) {
            if (i == selectedOption) {
                System.out.println("-> " + playOptions[i]);
            } else {
                System.out.println("   " + playOptions[i]);
            }
        }
        System.out.print("Esperando entrada:\n> ");
    }

    public void displayNewGameSlotMenu(Map<Integer, GameStates> slotsMap) {
        System.out.println("\n==========================================================================================================");
        System.out.println("                                   SELECIONE UM SLOT PARA O NOVO JOGO                                     ");
        System.out.println("==========================================================================================================");

        for (int i = 1; i <= 4; i++) {
            printSlotDetails(i, "Slot " + i, slotsMap.get(i));
        }

        System.out.println("==========================================================================================================");
        System.out.println(" Digite o número do slot (1 a 4) onde deseja iniciar o Novo Jogo");
        System.out.println(" 0 - Cancelar e voltar");
        System.out.println("==========================================================================================================");
        System.out.print("Escolha uma opção:\n> ");
    }
}