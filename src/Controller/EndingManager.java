package Controller;

import Service.AffinityTracker;
import Model.Ending;
import Model.Player;
import Model.StoryState;

import java.util.ArrayList;
import java.util.List;

public class EndingManager {
    private List<Ending> avaliableEndings = new ArrayList<>();

    public Ending evaluateConditions(StoryState state, AffinityTracker afinity){
        //tem que ver
        return null;
    }

    public void triggerEnding(Ending ending){
        //tem que ver
    }

    public boolean checkGameOver(Player player){
        //tem que ver
        return false;
    }
}
