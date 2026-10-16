package Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameStates {

    private int currentChapterId;
    private String currentDialogId;
    private String currentSceneId;
    private Map<String, Boolean> storyFlags;

    // Atributos do Jogador
    private String playerBloodType;
    private double playerMoney;
    private int playerEnergy;
    private List<String> playerInventoryItems;

    @JsonProperty("saveTimestamp")
    private String saveTimestamp; // Data e hora do save

    // Construtor padrão (Novo jogo / Jackson)
    public GameStates() {
        this.currentChapterId = 1;
        this.currentDialogId = "d1.1";
        this.currentSceneId = "Quarto";
        this.storyFlags = new HashMap<>();
        this.playerBloodType = "A+";
        this.playerMoney = 0.0;
        this.playerEnergy = 100;
        this.playerInventoryItems = new ArrayList<>();
        this.saveTimestamp = "";
    }

    public GameStates(int currentChapterId, String currentDialogId, String currentSceneId,
                      Map<String, Boolean> storyFlags, String playerBloodType,
                      double playerMoney, int playerEnergy, List<String> playerInventoryItems,
                      String saveTimestamp) {
        this.currentChapterId = currentChapterId;
        this.currentDialogId = currentDialogId;
        this.currentSceneId = currentSceneId;
        this.storyFlags = (storyFlags != null) ? storyFlags : new HashMap<>();
        this.playerBloodType = playerBloodType;
        this.playerMoney = playerMoney;
        this.playerEnergy = playerEnergy;
        this.playerInventoryItems = (playerInventoryItems != null) ? playerInventoryItems : new ArrayList<>();
        this.saveTimestamp = (saveTimestamp != null) ? saveTimestamp : "";
    }

    // --- Progresso ---

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

    // --- Flags ---

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

    // --- Atributos do Player ---

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