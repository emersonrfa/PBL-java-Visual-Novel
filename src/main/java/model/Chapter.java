package model;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class Chapter {
    private int chapterNumber;
    private String title;
    private List<StoryScene> scenes;
    private final Set<String> pendingDialogueIds = new LinkedHashSet<>();

    public Chapter(int chapterNumber, String title) {
        this.chapterNumber = chapterNumber;
        this.title = title;
        this.scenes = new ArrayList<>();
    }

    public void addScene(StoryScene scene) {
        this.scenes.add(scene);
    }

    /**
     * Marca ids de diálogo que ainda serão escritos (roteiro em andamento). O validador não
     * trata referências a eles como erro; quando o roteiro for escrito, basta remover a marcação.
     */
    public void markPending(String... dialogueIds) {
        pendingDialogueIds.addAll(java.util.Arrays.asList(dialogueIds));
    }

    public Set<String> getPendingDialogueIds() { return java.util.Collections.unmodifiableSet(pendingDialogueIds); }

    public int getChapterNumber() { return chapterNumber; }

    public String getTitle() { return title; }

    public List<StoryScene> getScenes() { return scenes; }

}



