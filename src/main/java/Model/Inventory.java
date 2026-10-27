package Model;

import java.util.List;
import java.util.ArrayList;

public class Inventory {
    private List<Item> items;

    public Inventory() {
        this.items = new ArrayList<>();
    }

    public void addItem(Item item) {
        if (item != null) {
            this.items.add(item);
        }
    }

    public boolean removeItem(String itemName) {
        return this.items.removeIf(item -> item.getName().equalsIgnoreCase(itemName));
    }

    public boolean hasItem(String itemName) {
        for (Item item : items) {
            if (item.getName().equalsIgnoreCase(itemName)) {
                return true;
            }
        }
        return false;
    }

    public List<Item> getItems() {
        return items;
    }

    // Retorna apenas a lista de nomes dos itens para salvar no JSON
    public List<String> getItemNames() {
        List<String> names = new ArrayList<>();
        for (Item item : this.items) {
            names.add(item.getName());
        }
        return names;
    }
}