package Model;

import java.util.HashMap;
import java.util.Map;

public class Player extends Character {
    private String bloodType;
    private int energy;
    private double money;
    private Inventory inventory;

    private Map<String, Boolean> flags;


    public Player(String bloodType) {
        super("Emily", "Feminino", 22, "Protagonista");
        this.energy = 100;
        this.money = 20.0;
        this.inventory = new Inventory();
        this.bloodType = bloodType;
        this.flags = new HashMap<>();
    }

    public double getMoney() {
        return money;
    }

    public void addMoney(double amount) {
        this.money += amount;
    }

    public String getBloodType() {
        return bloodType;
    }

    public boolean spendMoney(double amount) {
        if (this.money >= amount) {
            this.money -= amount;
            return true;
        }
        return false; // ta liso
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public int getEnergy() {
        return energy;
    }

    public void addEnergy(int amount) {
        this.energy += amount;

        if (this.energy > 100)
            this.energy = 100; //encapsulamento
    }

    public int consumeEnergy(int amount){

        if (this.energy - amount < 0) {
            this.energy = 0;
            return 0;
        }
        else {
            this.energy -= amount;
            return this.energy - amount;
        }
    }

    public void setEnergy(int i) {
        this.energy = i;
    }

    // Métodos para as flags:

    public void setFlag(String flagName, boolean value) {
        this.flags.put(flagName, value);
    }

    public boolean getFlag(String flagName) {
        return this.flags.getOrDefault(flagName, false);
    }
}