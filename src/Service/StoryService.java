package Service;


import Model.Chapter;
import Model.Scene;
import Model.Dialogue;

public class StoryService {

    // metodo que estava dentro de scene
    public Dialogue findDialogueById(Scene scene, String dialogueId) {
        if (dialogueId == null) return null;

        for (Dialogue d : scene.getDialogues()) {
            if (d.getId().equals(dialogueId)) {
                return d;
            }
        }
        return null;
    }

    // metodo que estava dentro de Chapter
    public Scene findSceneByDialogueId(Chapter chapter, String dialogueId) {
        if (dialogueId == null) return null;

        for (Scene scene : chapter.getScenes()) {
            if (findDialogueById(scene, dialogueId) != null) {
                return scene;
            }
        }
        return null;
    }
}
