package Model;

import java.util.List;
import java.util.ArrayList;

public class Inventory {
    private List<Item> items;

    public Inventory() {
        this.items = new ArrayList<>();
    }

    public void addItem(Item item) {
        this.items.add(item);
    }

    public boolean removeItem(String itemName) {
        return this.items.removeIf(item -> item.getName().equalsIgnoreCase(itemName));
        // comando procura por um item com base em uma condição (nesse caso pelo nome) e remove se achar
        // cabivel mudança se a gnt for fazer mais de 1 item do msm tipo (identido)
        // exemplo: tenho 2 chaves e usei 1, ou seja sobra 1

    }

    public boolean hasItem(String itemName) {

        // return this.items.stream().anyMatch(item -> item.getName().equalsIgnoreCase(itemName));

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
}
