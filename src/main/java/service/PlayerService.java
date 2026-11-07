package service;

import model.GameStates;
import model.Item;
import model.Choice;
import model.Player;
import repository.ItemRepository;

import java.util.HashMap;

public class PlayerService {

    public void consumeEnergy(Player player, int amount) {
        player.consumeEnergy(amount);
    }

    public boolean validatePlayerRequirements(Player player, Choice option) {
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
     * Aplica todas as consequências de uma escolha (energia, dinheiro, flag, itens e relacionamento)
     * e devolve um relatório estruturado do que de fato mudou. Não imprime nada: a View decide
     * como exibir o relatório.
     *
     * @param tracker rastreador de afinidade/suspeita; pode ser {@code null} se não houver (ignora a parte de NPC)
     */
    public ConsequenceReport applyChoiceConsequences(Player player, Choice choice, AffinityTracker tracker) {
        ConsequenceReport report = new ConsequenceReport();

        int energyBefore = player.getEnergy();
        if (choice.getEnergyChange() < 0) {
            player.consumeEnergy(Math.abs(choice.getEnergyChange()));
        } else if (choice.getEnergyChange() > 0) {
            player.addEnergy(choice.getEnergyChange());
        }
        report.addEnergy(energyBefore, player.getEnergy());

        double moneyBefore = player.getMoney();
        if (choice.getMoneyChange() < 0) {
            player.spendMoney(Math.abs(choice.getMoneyChange()));
        } else if (choice.getMoneyChange() > 0) {
            player.addMoney(choice.getMoneyChange());
        }
        report.addMoney(moneyBefore, player.getMoney());

        if (choice.getFlagToSet() != null && !choice.getFlagToSet().trim().isEmpty()) {
            player.setFlag(choice.getFlagToSet(), true);
            report.addFlag(choice.getFlagToSet());
        }

        if (choice.getRequiredItemName() != null && choice.isConsumeRequiredItem()) {
            if (player.getInventory().removeItem(choice.getRequiredItemName())) {
                report.addItemUsed(choice.getRequiredItemName());
            }
        }

        for (Item item : choice.getItemsToGive()) {
            player.getInventory().addItem(item);
            report.addItemGained(item.getName());
        }

        if (tracker != null && choice.getTargetNpc() != null) {
            String npc = choice.getTargetNpc();

            int affinityBefore = tracker.getAffinity(npc);
            tracker.modifyAffinity(npc, choice.getAffinityChange());
            report.addAffinity(npc, affinityBefore, tracker.getAffinity(npc));

            int suspicionBefore = tracker.getSuspicion(npc);
            tracker.modifySuspicion(npc, choice.getSuspicionChange());
            report.addSuspicion(npc, suspicionBefore, tracker.getSuspicion(npc));
        }

        return report;
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