package model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Chapter {
    private int chapterNumber;
    private String title;
    private List<StoryScene> scenes;
    private Map<String, StoryScene> sceneByDialogueId; // índice preguiçoso, ver findSceneByDialogueId
    private int indexedScenes;
    private int indexedDialogues;
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

    /**
     * Cena que contém o diálogo, ou {@code null}. Usa um índice id → cena montado na primeira
     * consulta (em vez de varrer todas as cenas a cada diálogo) e refeito se cenas/diálogos
     * forem acrescentados depois.
     */
    public StoryScene findSceneByDialogueId(String dialogueId) {
        if (dialogueId == null) return null;
        ensureIndex();
        return sceneByDialogueId.get(dialogueId);
    }

    public Dialogue findDialogueById(String dialogueId) {
        StoryScene scene = findSceneByDialogueId(dialogueId);
        return scene == null ? null : scene.getDialogueById(dialogueId);
    }

    private void ensureIndex() {
        int dialogueCount = 0;
        for (StoryScene scene : scenes) {
            dialogueCount += scene.getDialogues().size();
        }
        if (sceneByDialogueId != null && indexedScenes == scenes.size() && indexedDialogues == dialogueCount) {
            return;
        }
        Map<String, StoryScene> index = new HashMap<>();
        for (StoryScene scene : scenes) {
            for (Dialogue dialogue : scene.getDialogues()) {
                index.putIfAbsent(dialogue.getId(), scene); // repetido: vale o primeiro, como na varredura
            }
        }
        sceneByDialogueId = index;
        indexedScenes = scenes.size();
        indexedDialogues = dialogueCount;
    }

}



