
package Service;


import java.util.HashMap;
import java.util.Map;

public class AffinityTracker {
    private Map<String, Integer> npcAffinities;
    private Map<String, Integer> npcSuspicions;

    public AffinityTracker() {
        this.npcAffinities = new HashMap<>();
        this.npcSuspicions = new HashMap<>();
    }

    // métodos de afinidade

    public void modifyAffinity(String npcName, int amount) {
        int current = getAffinity(npcName);
        int newAffinity = current + amount;

        if (newAffinity < -20)
            newAffinity = -20;
        else if (newAffinity > 100)
            newAffinity = 100;

        //Passar apenas o newAffinity, pois ele já contém a soma correta
        npcAffinities.put(npcName, newAffinity);
    }

    public int getAffinity(String npcName) {
        return npcAffinities.getOrDefault(npcName, 0); // vai retornar 0 se nunca interagiu com este
    }

    // métodos de suspeita dos vampiros

    public int getSuspicion(String npcName) {
        return npcSuspicions.getOrDefault(npcName, 0);
    }

    public void modifySuspicion(String npcName, int amount) {
        int current = getSuspicion(npcName);
        int newSuspicion = current + amount;

        if (newSuspicion < 0)
            newSuspicion = 0;
        if (newSuspicion > 100)
            newSuspicion = 100;

        npcSuspicions.put(npcName, newSuspicion);
    }
}
