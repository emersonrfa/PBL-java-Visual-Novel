package service;


import model.Chapter;
import model.StoryScene;
import model.Dialogue;

public class StoryService {

    // metodo para encontrar o proximo dialogo dentro da mesma cena pelo id do proximo dialogo
    public static Dialogue findDialogueById(StoryScene scene, String dialogueId) {
        if (dialogueId == null) return null;

        for (Dialogue d : scene.getDialogues()) {
            if (d.getId().equals(dialogueId)) {
                return d;
            }
        }
        return null;
    }

    // metodo utilizado para procurar a proxima cena pelo id do proximo dialogo
    public static StoryScene findSceneByDialogueId(Chapter chapter, String dialogueId) {
        return chapter.findSceneByDialogueId(dialogueId);
    }
}
