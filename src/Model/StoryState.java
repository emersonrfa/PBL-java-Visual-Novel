package Model;

import java.util.HashMap;
import java.util.Map;

public class StoryState {
    private Map<String,Boolean> flegs= new HashMap<>();
    private Chapter currentChepter;
    private Scene currentScene;//acho que esse atributo deveria estar em chapter

    public void setFlag(String flagName, boolean value) {
        this.flegs.put(flagName, value);
    }

    public boolean hasFlag(String flagName){
        return this.flegs.get(flagName);
    }
}
