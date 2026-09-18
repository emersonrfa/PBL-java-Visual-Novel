package Service;

import java.util.HashMap;
import java.util.Map;

public class AffinityTracker {
    //The Integer is necessary because the map function only accepts objects
    private Map<String, Integer> npcAffinities;

    public AffinityTracker() {
        this.npcAffinities = new HashMap<>();
    }

    public void modifyAffinity(String npcName, int amount) {
        int current = getAffinity(npcName);
        npcAffinities.put(npcName, current + amount);
    }

    public int getAffinity(String npcName) {
        return npcAffinities.getOrDefault(npcName, 0); // vai retornar 0 se nunca interagiu com este
    }
}
