package Model;

public class Character {
    private String name;
    private String gender;
    private int age;
    private String description;
    private String grup;

    //constructor method
    public Character(String name,String gender,int age,String description) {
        this.age=age;
        this.name=name;
        this.description=description;
        this.gender=gender;
        this.grup="default";
    }



}
