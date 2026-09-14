package Model;

import java.util.HashMap;
import java.util.Map;

public class StoryState {
    private Map<String,Boolean> flags = new HashMap<>();
    private Chapter currentChapter;
    private Scene currentScene;//acho que esse atributo deveria estar em chapter

    public void setFlag(String flagName, boolean value) {
        this.flags.put(flagName, value);
    }

    public boolean hasFlag(String flagName){
        return this.flags.get(flagName);
    }
}
