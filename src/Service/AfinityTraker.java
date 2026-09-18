package Service;

import java.util.HashMap;
import java.util.Map;

public class AfinityTraker {
    //The Integer is necessary because the map function only accepts objects
    private Map<String, Integer> afinityMap = new HashMap<>();

    public void changeAfinity(String name, int afinity){
        afinityMap.put(name,afinity);
    }

    public int getafinity(String name){
        return afinityMap.get(name);
    }
}
