package Model;
import java.util.Scanner;

public class Player extends Character {
    private String bloodType;
    private int energy;
    private double money;
    private Inventory inventory;

    //constructor method
    public Player(String bloodType) {
        super("Emily", "Feminino", 22, "Protagonista");
        this.energy = 100;
        this.money = 20.0;
        this.inventory = new Inventory();
        this.bloodType = bloodType;
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

    public int ConsumeEnergy(int amount){
        int Energy = getEnergy();
        return Energy - amount;
    }
}