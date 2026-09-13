package Model;

public class Options {
    private String id;
    private String title;
    private String affinityCost;
    private String requiredItem;
    private String nextDialogue;
    private int suspectionCost;
    private int maxSuspectionAllowed;

    //constructor method
    public Options(String id, String title, String affinityCost, String requiredItem,
                   String nextDialogue, int suspectionCost, int maxSuspectionAllowed) {
        this.id = id;
        this.title = title;
        this.affinityCost = affinityCost;
        this.requiredItem = requiredItem;
        this.nextDialogue = nextDialogue;
        this.suspectionCost = suspectionCost;
        this.maxSuspectionAllowed = maxSuspectionAllowed;
    }


}
