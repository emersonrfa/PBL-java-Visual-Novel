package Service;

import Model.Player;
import Model.Options;

public class PlayerService {

    public void consumeEnergy(Player player, int amount) {
        int currentEnergy = player.getEnergy();
        int newEnergy = currentEnergy - amount;

        if (newEnergy < 0) {
            newEnergy = 0;
        }
        player.setEnergy(newEnergy);
    }

    public boolean validatePlayerRequirements(Player player, Options option) {
        if (option.getMoneyChange() < 0 && player.getMoney() < Math.abs(option.getMoneyChange())) {
            return false;
        }
        if (option.getRequiredFlag() != null && !player.getFlag(option.getRequiredFlag())) {
            return false;
        }
        if (option.getRequiredItemName() != null && !player.getInventory().hasItem(option.getRequiredItemName())) {
            return false;
        }
        return true;
    }

    public void applyOptionConsequences(Player player, Options option) {
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

        if (option.getFlagToSet() != null) {
            player.setFlag(option.getFlagToSet(), true);
        }

        if (option.getRequiredItemName() != null && option.isConsumeRequiredItem()) {
            player.getInventory().removeItem(option.getRequiredItemName());
            System.out.println("\n[-1 " + option.getRequiredItemName() + " (Utilizado)]");
        }

        if (option.getItemToGive() != null) {
            player.getInventory().addItem(option.getItemToGive());
            System.out.println("\n[+ " + option.getItemToGive().getName() + "]");
        }
    }
}
