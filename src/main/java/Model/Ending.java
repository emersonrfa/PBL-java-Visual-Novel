package Model;

public class Ending {
    private String id;
    private String title;
    private String description;

    public Ending(String id,String description,String title){
        this.description = description;
        this.id= id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

}
