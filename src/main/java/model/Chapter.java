package model;

import java.util.ArrayList;
import java.util.List;

public class Chapter {
    private int chapterNumber;
    private String title;
    private List<StoryScene> scenes;

    public Chapter(int chapterNumber, String title) {
        this.chapterNumber = chapterNumber;
        this.title = title;
        this.scenes = new ArrayList<>();
    }

    public void addScene(StoryScene scene) {
        this.scenes.add(scene);
    }

    public int getChapterNumber() { return chapterNumber; }

    public String getTitle() { return title; }

    public List<StoryScene> getScenes() { return scenes; }

}



