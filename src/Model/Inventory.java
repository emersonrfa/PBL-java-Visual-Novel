package Model;
import java.util.List;
import java.util.ArrayList;

public class Inventory {

    private List<Item> items = new ArrayList<>();

    public List<Item> getItems() {
        return items;
    }

    public void addItems(Item item){
        if(items!=null){
            items.add(item);
    }
}
}
