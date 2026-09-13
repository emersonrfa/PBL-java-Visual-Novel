package Model;

import java.util.HashMap;
import java.util.Map;

public class Scene {

    private Map<String,Dialogue> dialogueMap = new HashMap<>();

    public Dialogue getDialogue(String id) {
        return dialogueMap.get(id);
    }
}
