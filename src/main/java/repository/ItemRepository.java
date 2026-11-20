package repository;

import model.Item;
import java.util.HashMap;
import java.util.Map;

public class ItemRepository {
    private Map<String, Item> items;

    public ItemRepository() {
        this.items = new HashMap<>();
        loadItems(); // Garante que a base de itens já esteja carregada ao instanciar
    }

    public void loadItems() {
        // Itens iniciais / cotidianos
        items.put("Celular", new Item("Celular de Emily", "Um celular popular e moderno, útil para diversas coisas.", "Normal", 0, 0.0));
        items.put("Colar de Prata", new Item("Colar de Prata", "Um colar bonito e elegante.", "Normal", 0, 45.0));
        items.put("Chave de Casa", new Item("Chave de Casa", "Sua preciosa chave para seu lar.", "Normal", 0, 0.0));
        items.put("Casaco Simples", new Item("Casaco Simples", "Seu antigo casaco para andar na rua em um dia de frio.", "Normal", 0, 0.0));

        // Itens da Loja de Conveniência (Capítulo 2)
        items.put("Energetico Lunar", new Item("Energético Lunar", "Bebida energética de edição noturna. Restaura disposição rapidamente.", "Consumivel", 35, 4.0));
        items.put("Pastilha de Alho", new Item("Pastilha de Alho", "Pastilha extremamente forte. Útil para emergências, mas arriscada perto de vampiros.", "Utilidade", 0, 3.0));
        items.put("Amuleto de Prata Falsa", new Item("Amuleto de Prata Falsa", "Bijuteria prateada convincente. Pode intimidar criaturas desatentas.", "Equipamento", 0, 6.0));

        // Itens da Missão de Infiltração (Capítulos 2 e 3)
        items.put("Colar de Ocultacao", new Item("Colar de Ocultação Sombria", "Artefato mágico entregue por Santo Pedro que mascara o calor, odor e batimento humano de Emily.", "Artefato", 0, 0.0));
        items.put("Lentes Carmesim", new Item("Lentes de Contato Carmesim", "Lentes especiais que deixam os olhos de Emily com um brilho vermelho vampírico natural.", "Disfarce", 0, 0.0));
        items.put("Cartao do Bar", new Item("Cartão do Bar Eclipse", "Cartão com o endereço de um bar suspeito frequentado por vampiros recrutadores.", "Pista", 0, 0.0));
        items.put("Carta de Convite", new Item("Carta de Convite da Mansão", "Envelope preto lacrado com cera carmesim entregue por Gabriel e Cassian, com o mapa e autorização para a Mansão Nobilis.", "Chave", 0, 0.0));
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