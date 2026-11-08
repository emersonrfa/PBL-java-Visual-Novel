package service;

import model.Item;
import model.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Inventário somente para visualização: lista nome, descrição e imagem de cada item.
 * Usar um item acontece pelas opções de diálogo (Choice.setRequiredItem / setItemToGive), nunca aqui.
 */
public final class InventoryService {

    /** {@code imagePath} é nulo quando não há imagem (nem items/default.png). */
    public record InventoryEntry(String name, String description, String imagePath) {}

    private InventoryService() {}

    public static List<InventoryEntry> entries(Player player, AssetResolver assets) {
        List<InventoryEntry> entries = new ArrayList<>();
        for (Item item : player.getInventory().getItems()) {
            entries.add(new InventoryEntry(item.getName(), item.getDescription(),
                    assets.itemImage(item).orElse(null)));
        }
        return entries;
    }
}
