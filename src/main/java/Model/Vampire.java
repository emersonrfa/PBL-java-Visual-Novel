package Model;

import java.util.ArrayList;
import java.util.List;

public class Vampire extends NPC {
    private List<String> bloodPreferences;
    private int setSuspicionThreshold; // vai definir o quanto de suspeita o vampiro precisa para dar game over

    //constructor method
    public Vampire(String role, String defaultLocation, String name, String description, String gender, int age) {
        super(role, defaultLocation, name, description, gender, age);
        this.bloodPreferences = new ArrayList<>();
    }

    public void addBloodPreference(String bloodType) {
        if (bloodType != null && !this.bloodPreferences.contains(bloodType)) {
            this.bloodPreferences.add(bloodType);
        }
    }

    public void setBloodPreferences(List<String> preferences) {
        this.bloodPreferences = preferences;
    }

    // para verificar se o vampiro gosta desse tipo
    public boolean likesBloodType(String bloodType) {
        return this.bloodPreferences.contains(bloodType);
    }

    public int getSuspicionThreshold() {
        return setSuspicionThreshold;
    }

    public void setSuspicionThreshold(int setSuspicionThreshold) {
        this.setSuspicionThreshold = setSuspicionThreshold;
    }

}
