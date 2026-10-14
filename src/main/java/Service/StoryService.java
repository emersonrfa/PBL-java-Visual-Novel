package Service;


import Model.Chapter;
import Model.Scene;
import Model.Dialogue;

public class StoryService {

    // metodo para encontrar o proximo dialogo dentro da mesma cena pelo id do proximo dialogo
    public static Dialogue findDialogueById(Scene scene, String dialogueId) {
        if (dialogueId == null) return null;

        for (Dialogue d : scene.getDialogues()) {
            if (d.getId().equals(dialogueId)) {
                return d;
            }
        }
        return null;
    }

    // metodo utilizado para procurar a proxima cena pelo id do proximo dialogo
    public static Scene findSceneByDialogueId(Chapter chapter, String dialogueId) {
        if (dialogueId == null) return null;

        for (Scene scene : chapter.getScenes()) {
            if (findDialogueById(scene, dialogueId) != null) {
                return scene;
            }
        }
        return null;
    }
}
