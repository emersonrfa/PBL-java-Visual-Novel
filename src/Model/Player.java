package Model;

//refatorei a classe player para tirar o scanner e a logica de consumir energia, a logica de consumir energia estara
//parte do service
public class Player extends Character {
    private String bloodType;
    private int energy;
    private int suspectionLevel;
    private Boolean isReveled;

    // Construtor parametrizado (limpo, apenas recebe dados)
    public Player(String name, String gender, int age, String description, int initialEnergy) {
        super(name, gender, age, description);
        this.bloodType = "default";
        this.isReveled = false;
        this.suspectionLevel = 0;
        this.energy = initialEnergy;
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

    // Apenas define a energia, sem fazer cálculos
    public void setEnergy(int energy) {
        this.energy = energy;
    }

    public int getSuspectionLevel() {
        return suspectionLevel;
    }

    public void setSuspectionLevel(int suspectionLevel) {
        this.suspectionLevel = suspectionLevel;
    }

    public Boolean getIsReveled() {
        return isReveled;
    }

    public void setIsReveled(Boolean isReveled) {
        this.isReveled = isReveled;
    }
}