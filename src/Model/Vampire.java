package Model;

public class Vampire extends NPC {
    private String bloodPreferece;
    private String suspectionThresHold;

    //constructor method
    public Vampire(String role, String defaultLocation, String name, String description, String gender, int age) {
        super(role, defaultLocation, name, description, gender, age);
    }

    public void setBloodPreferece(String bloodPreferece) {
        this.bloodPreferece = bloodPreferece;
    }

    public void setSuspectionThresHold(String suspectionThresHold) {
        this.suspectionThresHold = suspectionThresHold;
    }
}
