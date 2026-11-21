package service;

import model.NPC;
import model.Protagonist;
import model.Vampire;
import repository.NpcRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * Dados da Galeria de Personagens (somente leitura): quem são, a descrição e quais sprites existem
 * para cada um. Não desenha nada; a tela só exibe o resultado.
 *
 * Os sprites disponíveis são descobertos pelo {@link AssetResolver} testando as expressões que o
 * roteiro usa ({@link #EXPRESSIONS}) na pasta images/characters/&lt;personagem&gt;/. O sprite
 * {@code default} vem sempre primeiro. Uma expressão nova só aparece na galeria se o nome dela estiver
 * na lista abaixo.
 */
public class CharacterGalleryService {

    /** Uma imagem de personagem: rótulo para exibir ("Padrão", "Sorrindo"...) e caminho do recurso. */
    public record SpriteEntry(String label, String path) {}

    public record GalleryEntry(String name, String role, String description, int age,
                               boolean vampire, List<SpriteEntry> sprites) {
        public boolean hasSprites() {
            return !sprites.isEmpty();
        }
    }

    /** Expressões usadas no roteiro (e algumas comuns), em ordem de exibição. */
    public static final List<String> EXPRESSIONS = List.of(
            "Normal", "Sorrindo", "Animado", "Pensando", "Serio", "Surpresa", "Confusa", "Envergonhado",
            "Triste", "Cansada", "Assustada", "Entediada", "Bravo", "Feliz");

    private static final String EMILY_ROLE = "Protagonista";
    private static final String EMILY_DESCRIPTION = "Emily é uma jovem de 22 anos, formada em Biologia e Criaturas "
            + "Mágicas, que acaba de ficar sem o auxílio e precisa de um emprego. Cabelos castanhos ondulados, "
            + "olhar curioso e jeito observador. Para pagar as contas aceita um trabalho para a Ordem de Santa "
            + "Prata e se vê no meio de uma corte de vampiros.";
    private static final int EMILY_AGE = 22;

    private final NpcRepository npcRepository;
    private final AssetResolver assets;

    public CharacterGalleryService(NpcRepository npcRepository, AssetResolver assets) {
        this.npcRepository = npcRepository;
        this.assets = assets;
    }

    /** Emily primeiro; depois os NPCs na ordem de cadastro (mortais e, em seguida, vampiros). */
    public List<GalleryEntry> entries() {
        List<GalleryEntry> list = new ArrayList<>();
        list.add(new GalleryEntry(Protagonist.NAME, EMILY_ROLE, EMILY_DESCRIPTION, EMILY_AGE, false,
                spritesFor(Protagonist.NAME)));
        for (Map.Entry<String, NPC> entry : npcRepository.getAllNpcs().entrySet()) {
            NPC npc = entry.getValue();
            list.add(new GalleryEntry(npc.getName(), npc.getRole(), npc.getDescription(), npc.getAge(),
                    npc instanceof Vampire, spritesFor(entry.getKey())));
        }
        return list;
    }

    private static String label(String expression) {
        return "Serio".equals(expression) ? "Sério" : expression;
    }

    /** Sprites existentes do personagem (chave do repositório, ex.: "Santo Pedro"); padrão primeiro. */
    List<SpriteEntry> spritesFor(String characterKey) {
        List<SpriteEntry> sprites = new ArrayList<>();
        assets.sprite(characterKey, null).ifPresent(path -> sprites.add(new SpriteEntry("Padrão", path)));
        for (String expression : EXPRESSIONS) {
            Optional<String> path = assets.sprite(characterKey, expression);
            if (path.isPresent() && sprites.stream().noneMatch(s -> s.path().equals(path.get()))) {
                sprites.add(new SpriteEntry(label(expression), path.get()));
            }
        }
        return sprites;
    }
}
