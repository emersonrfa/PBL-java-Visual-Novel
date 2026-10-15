package Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.HashMap;
import java.util.Map;

// classe responsavel por guardar os atributos do jogo, exclusivamente do jogo
@JsonIgnoreProperties(ignoreUnknown = true)
public class GameStates {

    private int currentChapterId;
    private String currentDialogId;
    private String currentSceneId;
    private Map<String, Boolean> storyFlags;

    // construtor vazio quer dizer novo jogo
    public GameStates() {
        this.currentChapterId = 1;
        this.currentDialogId = "d1.1";
        this.currentSceneId = "Quarto";
        this.storyFlags = new HashMap<>();
    }

    public GameStates(int currentChapterId, String currentDialogId, String currentSceneId, Map<String, Boolean> storyFlags) {
        this.currentChapterId = currentChapterId;
        this.currentDialogId = currentDialogId;
        this.currentSceneId = currentSceneId;
        this.storyFlags = (storyFlags != null) ? storyFlags : new HashMap<>();
    }

    public int getCurrentChapterId() {
        return currentChapterId;
    }

    public void setCurrentChapterId(int currentChapterId) {
        this.currentChapterId = currentChapterId;
    }

    public String getCurrentDialogId() {
        return currentDialogId;
    }

    public void setCurrentDialogId(String currentDialogId) {
        this.currentDialogId = currentDialogId;
    }

    public String getCurrentSceneId() {
        return currentSceneId;
    }

    public void setCurrentSceneId(String currentSceneId) {
        this.currentSceneId = currentSceneId;
    }

    public void setFlag(String flagName, boolean value) {
        if (this.storyFlags == null) {
            this.storyFlags = new HashMap<>();
        }
        this.storyFlags.put(flagName, value);
    }

    public boolean hasFlag(String flagName) {
        if (this.storyFlags == null) {
            return false;
        }
        return this.storyFlags.getOrDefault(flagName, false);
    }

    public Map<String, Boolean> getStoryFlags() {
        return storyFlags;
    }

    public void setStoryFlags(Map<String, Boolean> storyFlags) {
        this.storyFlags = storyFlags;
    }
}