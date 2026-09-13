package Model;

public class Ending {
    private String id;
    private String title;
    private String description;
    private String endingType;

    //constructor method
    public Ending(String id,String description,String endingType,String title){
        this.description=description;
        this.endingType=endingType;
        this.id=id;
        this.title=title;
    }

    public void displayEndingSummary(){
        // tem que ver
    }
}
