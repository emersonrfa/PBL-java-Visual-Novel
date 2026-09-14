package Model;
import java.util.Scanner;

public class Player extends Character {
    private String bloodType;
    private int energy;
    private int suspectionLevel;
    private Boolean isReveled;

    //constructor method
    public Player(Scanner scanner){
        String name = "Emily";
        int age =22;
        // Limpa o '\n' pendente deixado pelo nextInt()
        String description = "aaa";
        String gender = "Feminino";
        super(name,gender,age,description);
        this.bloodType="default";
        this.isReveled=false;
        this.suspectionLevel=0;

    }

    public String getBloodType() {
        return bloodType;
    }

    public void setBloodType(String bloodType) {
        this.bloodType = bloodType;
    }

    public int getEnergy() {
        return energy;
    }

    public int ConsumeEnergy(int amount){
        int Energy=getEnergy();
        return Energy - amount;
    }

    // setSuspectionLevel does the same thing as modifySuspection
    public void setSuspectionLevel(int suspectionLevel) {
        this.suspectionLevel = suspectionLevel;
    }
    // getsuspectionLevel does the same thing as checkExposure
    public int getSuspectionLevel() {
        return suspectionLevel;
    }




}

