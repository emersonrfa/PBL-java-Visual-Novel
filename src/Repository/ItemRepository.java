package Repository;

import Model.Item;
import java.util.HashMap;
import java.util.Map;

public class ItemRepository {
    private Map<String, Item> items;

    public ItemRepository(){
        this.items = new HashMap<>();
    }

    public void loadItems() {
        items.put("Celular", new Item("Celular de Emily", "Um celular popular e moderno, útil para " +
                "diversas coisas.", "Normal", 0, 0.0));
        items.put("Colar de Prata", new Item("Colar de Prata", "Um colar bonito e elegante.",
                "Normal", 0, 45.0));
    }

    public Item getItem(String key) {
        return items.get(key);
    }
}
