package Model;

import java.util.HashMap;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;

public class Chapter {
    private String id;
    private int chapterNumber;
    private String title;
    private String initialSceneld;
    private List<Scene> scenes;

    public Chapter(int chapterNumber, String title) {

        this.chapterNumber = chapterNumber;
        this.title = title;
        this.scenes = new ArrayList<>();
    }

    public void addScene(Scene scene) {
        this.scenes.add(scene);
    }


    // métod para buscar próximo dialogo com o nextdialogueid e identificar qnd achou um
    public Scene getSceneByDialogueId(String dialogueId) {
        if (dialogueId == null) return null;
        for (Scene scene : this.scenes) { // busca o dialogo com aquela id dentro do conjunto de dialogos daquela cena utilizando GetDialoguebyid
            if (scene.getDialogueById(dialogueId) != null) {
                return scene;
            }
        }
        return null;
    }

    public int getChapterNumber() { return chapterNumber; }
    public String getTitle() { return title; }
    public List<Scene> getScenes() { return scenes; }
}
