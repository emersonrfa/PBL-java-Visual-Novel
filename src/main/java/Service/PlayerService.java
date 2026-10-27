package Service;

import Model.GameStates;
import Model.Item;
import Model.Options;
import Model.Player;
import Repository.ItemRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class PlayerService {

    public void consumeEnergy(Player player, int amount) {
        player.consumeEnergy(amount);
    }

    public boolean validatePlayerRequirements(Player player, Options option) {
        if (option.getMoneyChange() < 0 && player.getMoney() < Math.abs(option.getMoneyChange())) {
            return false;
        }
        if (option.getRequiredFlag() != null && !player.getFlag(option.getRequiredFlag())) {
            return false;
        }
        if (option.getEnergyChange() < 0 && player.getEnergy() < Math.abs(option.getEnergyChange())) {
            return false;
        }
        if (option.getRequiredItemName() != null && !player.getInventory().hasItem(option.getRequiredItemName())) {
            return false;
        }
        return true;
    }

    /**
     * Aplica as consequências no Player e retorna as mensagens geradas para a View exibir.
     */
    public List<String> applyOptionConsequences(Player player, Options option) {
        List<String> feedbackMessages = new ArrayList<>();

        if (option.getEnergyChange() < 0) {
            player.consumeEnergy(Math.abs(option.getEnergyChange()));
        } else if (option.getEnergyChange() > 0) {
            player.addEnergy(option.getEnergyChange());
        }

        if (option.getMoneyChange() < 0) {
            player.spendMoney(Math.abs(option.getMoneyChange()));
        } else if (option.getMoneyChange() > 0) {
            player.addMoney(option.getMoneyChange());
        }

        if (option.getFlagToSet() != null && !option.getFlagToSet().trim().isEmpty()) {
            player.setFlag(option.getFlagToSet(), true);
        }

        if (option.getRequiredItemName() != null && option.isConsumeRequiredItem()) {
            player.getInventory().removeItem(option.getRequiredItemName());
            feedbackMessages.add("[-1 " + option.getRequiredItemName() + " (Utilizado)]");
        }

        for (Item item : option.getItemsToGive()) {
            player.getInventory().addItem(item);
            feedbackMessages.add("[+ " + item.getName() + "]");
        }

        return feedbackMessages;
    }

    /**
     * Sobrecarga para quando ainda não há AffinityTracker (ex: Novo Jogo na Main).
     */
    public void populateStateFromPlayer(GameStates state, Player player) {
        populateStateFromPlayer(state, player, null);
    }

    /**
     * Popula um objeto GameStates com os dados atuais do Player e do AffinityTracker.
     */
    public void populateStateFromPlayer(GameStates state, Player player, AffinityTracker tracker) {
        if (state == null || player == null) return;
        if (state.getSaveName() == null || state.getSaveName().trim().isEmpty()) {
            state.setSaveName("Emily");
        }
        state.setPlayerBloodType(player.getBloodType());
        state.setPlayerMoney(player.getMoney());
        state.setPlayerEnergy(player.getEnergy());
        state.setStoryFlags(new HashMap<>(player.getFlags()));
        if (player.getInventory() != null) {
            state.setPlayerInventoryItems(player.getInventory().getItemNames());
        }
        if (tracker != null) {
            state.setNpcAffinities(tracker.getNpcAffinities());
            state.setNpcSuspicions(tracker.getNpcSuspicions());
        }
    }

    public void restoreAffinityFromState(GameStates savedState, AffinityTracker tracker) {
        if (savedState == null || tracker == null) return;
        tracker.setNpcAffinities(savedState.getNpcAffinities());
        tracker.setNpcSuspicions(savedState.getNpcSuspicions());
    }

    /**
     * Reconstrói uma instância de Player a partir de um GameStates salvo.
     */
    public Player createPlayerFromState(GameStates savedState, ItemRepository itemRepository) {
        String bloodType = (savedState.getPlayerBloodType() != null)
                ? savedState.getPlayerBloodType()
                : "A+";
        Player player = new Player(bloodType);
        player.setMoney(savedState.getPlayerMoney());
        player.setEnergy(savedState.getPlayerEnergy());

        if (savedState.getPlayerInventoryItems() != null && itemRepository != null) {
            for (String itemName : savedState.getPlayerInventoryItems()) {
                Item item = itemRepository.findByName(itemName);
                if (item != null) {
                    player.getInventory().addItem(item);
                }
            }
        }

        if (savedState.getStoryFlags() != null) {
            for (var entry : savedState.getStoryFlags().entrySet()) {
                player.setFlag(entry.getKey(), entry.getValue());
            }
        }

        return player;
    }
}