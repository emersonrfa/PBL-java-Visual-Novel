package Repository;

import Model.Item;
import java.util.HashMap;
import java.util.Map;

public class ItemRepository {
    private Map<String, Item> items;

    public ItemRepository() {
        this.items = new HashMap<>();
        loadItems(); // Garante que a base de itens já esteja carregada ao instanciar
    }

    public void loadItems() {
        items.put("Celular", new Item("Celular de Emily", "Um celular popular e moderno, útil para diversas coisas.", "Normal", 0, 0.0));
        items.put("Colar de Prata", new Item("Colar de Prata", "Um colar bonito e elegante.", "Normal", 0, 45.0));
        items.put("Chave de Casa", new Item("Chave de Casa", "Sua preciosa chave para seu lar.", "Normal", 0, 0.0));
        items.put("Casaco Simples", new Item("Casaco Simples", "Seu antigo casaco para andar na rua em um dia de frio", "Normal", 0, 0.0));
        // Adicione aqui outros itens do jogo conforme forem criados
    }

    /**
     * Busca um item pelo identificador de chave do repositório.
     */
    public Item getItem(String key) {
        if (key == null) return null;
        return items.get(key);
    }

    /**
     * Busca um item tanto pela chave quanto pelo nome cadastrado no objeto Item (ignora maiúsculas/minúsculas).
     * Ideal para reconstruir o inventário a partir das strings salvas no JSON.
     */
    public Item findByName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return null;
        }

        String search = name.trim();

        // 1. Tenta buscar direto pela chave no Map
        if (items.containsKey(search)) {
            return items.get(search);
        }

        // 2. Busca iterando pelas chaves e pelos nomes dos itens ignorando caixa alta/baixa
        for (Map.Entry<String, Item> entry : items.entrySet()) {
            if (entry.getKey().equalsIgnoreCase(search)) {
                return entry.getValue();
            }
            if (entry.getValue() != null && entry.getValue().getName().equalsIgnoreCase(search)) {
                return entry.getValue();
            }
        }

        return null;
    }

    public Map<String, Item> getAllItems() {
        return items;
    }
}