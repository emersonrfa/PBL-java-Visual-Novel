package Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameStates {

    private String saveName;
    private int currentChapterId;
    private String currentDialogId;
    private String currentSceneId;
    private Map<String, Boolean> storyFlags;

    // Relacionamentos (Afinidades e Suspeitas)
    private Map<String, Integer> npcAffinities;
    private Map<String, Integer> npcSuspicions;

    // Atributos do Jogador
    private String playerBloodType;
    private double playerMoney;
    private int playerEnergy;
    private List<String> playerInventoryItems;

    @JsonProperty("saveTimestamp")
    private String saveTimestamp;

    public GameStates() {
        this.saveName = "Emily";
        this.currentChapterId = 1;
        this.currentDialogId = "d1.1";
        this.currentSceneId = "Quarto";
        this.storyFlags = new HashMap<>();
        this.npcAffinities = new HashMap<>();
        this.npcSuspicions = new HashMap<>();
        this.playerBloodType = "A+";
        this.playerMoney = 20.0;
        this.playerEnergy = 100;
        this.playerInventoryItems = new ArrayList<>();
        this.saveTimestamp = "";
    }

    public String getSaveName() {
        return saveName;
    }

    public void setSaveName(String saveName) {
        this.saveName = saveName;
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

    public Map<String, Integer> getNpcAffinities() {
        return npcAffinities;
    }

    public void setNpcAffinities(Map<String, Integer> npcAffinities) {
        this.npcAffinities = npcAffinities;
    }

    public Map<String, Integer> getNpcSuspicions() {
        return npcSuspicions;
    }

    public void setNpcSuspicions(Map<String, Integer> npcSuspicions) {
        this.npcSuspicions = npcSuspicions;
    }

    public String getPlayerBloodType() {
        return playerBloodType;
    }

    public void setPlayerBloodType(String playerBloodType) {
        this.playerBloodType = playerBloodType;
    }

    public double getPlayerMoney() {
        return playerMoney;
    }

    public void setPlayerMoney(double playerMoney) {
        this.playerMoney = playerMoney;
    }

    public int getPlayerEnergy() {
        return playerEnergy;
    }

    public void setPlayerEnergy(int playerEnergy) {
        this.playerEnergy = playerEnergy;
    }

    public List<String> getPlayerInventoryItems() {
        return playerInventoryItems;
    }

    public void setPlayerInventoryItems(List<String> playerInventoryItems) {
        this.playerInventoryItems = playerInventoryItems;
    }

    @JsonProperty("saveTimestamp")
    public String getSaveTimestamp() {
        return saveTimestamp;
    }

    @JsonProperty("saveTimestamp")
    public void setSaveTimestamp(String saveTimestamp) {
        this.saveTimestamp = saveTimestamp;
    }
}