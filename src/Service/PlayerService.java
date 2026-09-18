package Service;

//classe para acessar os dados de Plsyer

import Model.Player;

public class PlayerService {


    public void consumeEnergy(Player player, int amount) {
        int currentEnergy = player.getEnergy();
        int newEnergy = currentEnergy - amount;

        if (newEnergy < 0) {
            newEnergy = 0;
        }
        player.setEnergy(newEnergy);
    }
}
