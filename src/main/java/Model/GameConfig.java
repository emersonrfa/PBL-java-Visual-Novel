package Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GameConfig {
    private int textSpeedMs;
    private int textSize;
    private boolean confirmChoices;
    private boolean turnMusic;
    private boolean turnAutoSave;

    public GameConfig() {
        this.textSpeedMs = 10;
        this.textSize = 12;
        this.confirmChoices = false;
        this.turnAutoSave = false;
        this.turnMusic = false;
    }

    public GameConfig(int textSpeedMs, int textSize, boolean confirmChoices, boolean turnAutoSave, boolean turnMusic) {
        this.textSpeedMs = textSpeedMs;
        this.textSize = textSize;
        this.confirmChoices = confirmChoices;
        this.turnAutoSave = turnAutoSave;
        this.turnMusic = turnMusic;
    }

    public int getTextSize() {
        return textSize;
    }

    public int getTextSpeedMs() {
        return textSpeedMs;
    }

    public boolean isConfirmChoices() {
        return confirmChoices;
    }

    public boolean isTurnMusic() {
        return turnMusic;
    }

    public boolean isTurnAutoSave() {
        return turnAutoSave;
    }

    public void setTextSize(int textSize) {
        this.textSize = textSize;
    }

    public void setTextSpeedMs(int textSpeedMs) {
        this.textSpeedMs = textSpeedMs;
    }

    public void setConfirmChoices(boolean confirmChoices) {
        this.confirmChoices = confirmChoices;
    }

    public void setTurnMusic(boolean turnMusic) {
        this.turnMusic = turnMusic;
    }

    public void setTurnAutoSave(boolean turnAutoSave) {
        this.turnAutoSave = turnAutoSave;
    }
}
