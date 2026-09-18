package Model;

public class Vampire extends NPC {
    private String bloodPreference;
    private String suspectionThreshold;

    //constructor method
    public Vampire(String role, String defaultLocation, String name, String description, String gender, int age) {
        super(role, defaultLocation, name, description, gender, age);
    }

    public void setBloodPreference(String bloodPreference) {
        this.bloodPreference = bloodPreference;
    }

    public void setSuspectionThreshold(String suspectionThreshold) {
        this.suspectionThreshold = Vampire.this.suspectionThreshold;
    }
}
