package service;

import model.GameStates;
import model.Player;
import repository.ItemRepository;
import repository.StateRepository;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Operações de save/novo jogo sem nenhuma saída de tela, para as telas gráficas (e qualquer outra
 * interface) usarem. Slot 0 é o autosave; 1 a 4 são os slots manuais.
 */
public class SaveService {
    public static final int AUTOSAVE_SLOT = 0;
    public static final int MANUAL_SLOTS = 4;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");

    /** Resumo de um slot para listar na tela. {@code text} já vem formatado. */
    public record SlotSummary(int slot, boolean empty, boolean corrupted, String text) {
        public boolean loadable() {
            return !empty && !corrupted;
        }
    }

    /** Jogador + estado prontos para iniciar uma partida. */
    public record LoadedGame(Player player, GameStates state) {}

    private final StateRepository stateRepository;
    private final PlayerService playerService;
    private final ItemRepository itemRepository;

    public SaveService(StateRepository stateRepository, PlayerService playerService, ItemRepository itemRepository) {
        this.stateRepository = stateRepository;
        this.playerService = playerService;
        this.itemRepository = itemRepository;
    }

    public List<SlotSummary> summaries(boolean includeAutosave) {
        List<SlotSummary> list = new ArrayList<>();
        for (int slot = includeAutosave ? AUTOSAVE_SLOT : 1; slot <= MANUAL_SLOTS; slot++) {
            list.add(summary(slot));
        }
        return list;
    }

    public SlotSummary summary(int slot) {
        String label = slot == AUTOSAVE_SLOT ? "Autosave" : "Slot " + slot;
        if (!stateRepository.slotExists(slot)) {
            return new SlotSummary(slot, true, false, label + " — vazio");
        }
        if (stateRepository.isSlotCorrupted(slot)) {
            return new SlotSummary(slot, false, true, label + " — save corrompido");
        }
        GameStates state = stateRepository.loadStateFromSlot(slot);
        if (state == null) {
            return new SlotSummary(slot, false, true, label + " — save corrompido");
        }
        int items = state.getPlayerInventoryItems() == null ? 0 : state.getPlayerInventoryItems().size();
        String date = (state.getSaveTimestamp() == null || state.getSaveTimestamp().isBlank())
                ? "sem data" : state.getSaveTimestamp();
        return new SlotSummary(slot, false, false, String.format(
                "%s — Cap. %d (%s) — Energia %d%%, R$ %.2f, Sangue %s, Itens %d — %s",
                label, state.getCurrentChapterId(),
                state.getCurrentSceneId() == null ? "N/A" : state.getCurrentSceneId(),
                state.getPlayerEnergy(), state.getPlayerMoney(), state.getPlayerBloodType(), items, date));
    }

    /** Cria um novo jogo (Capítulo 1) e já grava no slot manual e no autosave, como o console faz. */
    public LoadedGame newGame(String bloodType, int slot) {
        if (slot < 1 || slot > MANUAL_SLOTS) {
            throw new IllegalArgumentException("Slot inválido: " + slot);
        }
        Player player = new Player(bloodType);

        GameStates state = new GameStates();
        state.setCurrentChapterId(1);
        state.setCurrentSceneId("Quarto");
        state.setCurrentDialogId("d1.1");
        state.setSaveTimestamp(LocalDateTime.now().format(DATE_FORMATTER));
        playerService.populateStateFromPlayer(state, player);

        stateRepository.saveStateToSlot(state, slot);
        stateRepository.saveState(state);
        return new LoadedGame(player, state);
    }

    /** Carrega um slot; também o copia para o autosave (o autosave é sempre "a partida atual"). */
    public LoadedGame load(int slot) {
        SlotSummary summary = summary(slot);
        if (!summary.loadable()) {
            throw new IllegalStateException(summary.text());
        }
        GameStates state = stateRepository.loadStateFromSlot(slot);
        Player player = playerService.createPlayerFromState(state, itemRepository);
        stateRepository.saveState(state);
        return new LoadedGame(player, state);
    }

    public boolean delete(int slot) {
        return slot >= 1 && slot <= MANUAL_SLOTS && stateRepository.deleteSlot(slot);
    }
}
