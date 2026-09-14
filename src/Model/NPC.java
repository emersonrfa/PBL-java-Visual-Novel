package Model;

public class NPC extends Character {
    private Boolean isVampire;
    private String role;
    private String isRomanceable;
    private String defaultLocation;


    //constructor method
    public NPC(String role,String defaultLocation,String name,String description,String gender,int age){
        this.defaultLocation=defaultLocation;
        this.role=role;
        super(name,gender,age,description);
    }

    public void setVampire(Boolean vampire) {
        isVampire = vampire;
    }
}
