package Model;

import java.util.ArrayList;
import java.util.List;

public class Chapter {
    private String id;
    private int chapterNumber;
    private String title;
    private String initialSceneId;
    private List<Scene> scenes;

    public Chapter(int chapterNumber, String title) {
        this.chapterNumber = chapterNumber;
        this.title = title;
        this.scenes = new ArrayList<>();
    }

    public void addScene(Scene scene) {
        this.scenes.add(scene);
    }

    public int getChapterNumber() { return chapterNumber; }
    public String getTitle() { return title; }
    public List<Scene> getScenes() { return scenes; }

    public void setScenes(List<Scene> scenes) {
        this.scenes = scenes;
    }

    public Scene getSceneByDialogueId(String dialogueId) {
        if (dialogueId == null) return null;
        for (Scene scene : this.scenes) {
            if (scene.getDialogueById(dialogueId) != null) {
                return scene;
            }
        }
        return null;
    }
}




