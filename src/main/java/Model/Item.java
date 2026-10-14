package Model;

public class Item {
    private String name;
    private String description;
    private String type;
    private int effectValue; // valor que irá constar no momento de alterar um atributo (ex +10 energy)
    private double price; // isso se for um item compravel, se nao é zero

    public Item(String name, String description, String type, int effectValue, double price) {
        this.name = name;
        this.description = description;
        this.type = type;
        this.effectValue = effectValue;
        this.price = price;
    }



    // getters
    public String getName() {
        return name;
    }
    public double getPrice() {
        return price;
    }
    public int getEffectValue() {
        return effectValue;
    }
    public String getDescription() {
        return description;
    }
    public String getType() {
        return type;
    }
}

