package Model;

import java.util.HashMap;
import java.util.Map;

public class Chapter {
    private String id;
    private String title;
    private Map<String, Scene> sceneMap = new HashMap<>();
    private String initialSceneld;

    public Scene getScene(String sceneld) {
        return sceneMap.get(sceneld);
    }

    // tem que ver como saber se chegou na ultima cana
//    public boolean isCompleted(){
//
//    }
}
