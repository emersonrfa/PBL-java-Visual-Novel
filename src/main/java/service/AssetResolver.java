package service;

import model.Item;
import model.StoryScene;

import java.text.Normalizer;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Descobre, por convenção de nomes, qual imagem usar para fundos, sprites e itens.
 * Devolve caminhos de recurso do classpath (ex.: {@code /images/backgrounds/cozinha.png}) ou vazio
 * quando não existe arquivo, para a tela cair num visual padrão em vez de quebrar. Assim o jogo
 * roda hoje sem nenhuma imagem e passa a usá-las assim que forem colocadas nas pastas.
 *
 * <pre>
 * src/main/resources/images/
 *   backgrounds/&lt;cena&gt;.png                 fundo = background(id) da cena, senão o título da cena
 *   backgrounds/default.png                 fundo genérico (opcional)
 *   characters/&lt;personagem&gt;/&lt;expressão&gt;.png sprite; cai em default.png do personagem
 *   items/&lt;item&gt;.png                       imagem do item; cai em items/default.png
 * </pre>
 *
 * Nomes viram "slug": minúsculas, sem acentos, qualquer sequência fora de a-z/0-9 vira "_"
 * ("Sala de Estar" → sala_de_estar; "Emily" + "Surpresa" → characters/emily/surpresa.png).
 * Extensões aceitas: png, jpg, jpeg.
 */
public final class AssetResolver {
    public static final String BACKGROUNDS = "/images/backgrounds/";
    public static final String CHARACTERS = "/images/characters/";
    public static final String ITEMS = "/images/items/";
    public static final String DEFAULT_NAME = "default";

    private static final String[] EXTENSIONS = {"png", "jpg", "jpeg"};
    private static final String NARRATOR = "Narrador";

    private final Predicate<String> exists;

    /** Resolve contra o classpath da aplicação. */
    public AssetResolver() {
        this(path -> AssetResolver.class.getResource(path) != null);
    }

    /** Permite injetar a checagem de existência (testes). */
    public AssetResolver(Predicate<String> exists) {
        this.exists = exists;
    }

    public Optional<String> background(StoryScene scene) {
        if (scene == null) return Optional.empty();
        String id = scene.getBackgroundId() != null ? scene.getBackgroundId() : scene.getTitle();
        return firstExisting(BACKGROUNDS, slug(id)).or(() -> firstExisting(BACKGROUNDS, DEFAULT_NAME));
    }

    /** Sprite do personagem para a expressão; o narrador e a expressão "Nada" não têm sprite. */
    public Optional<String> sprite(String speaker, String expression) {
        if (speaker == null || speaker.isBlank() || NARRATOR.equalsIgnoreCase(speaker)) return Optional.empty();
        String dir = CHARACTERS + slug(speaker) + "/";
        if (expression != null && !expression.isBlank() && !"Nada".equalsIgnoreCase(expression)) {
            Optional<String> exact = firstExisting(dir, slug(expression));
            if (exact.isPresent()) return exact;
        }
        return firstExisting(dir, DEFAULT_NAME);
    }

    public Optional<String> itemImage(Item item) {
        if (item == null) return Optional.empty();
        return firstExisting(ITEMS, slug(item.getName())).or(() -> firstExisting(ITEMS, DEFAULT_NAME));
    }

    private Optional<String> firstExisting(String dir, String name) {
        if (name.isEmpty()) return Optional.empty();
        for (String ext : EXTENSIONS) {
            String path = dir + name + "." + ext;
            if (exists.test(path)) return Optional.of(path);
        }
        return Optional.empty();
    }

    /** "Sala de Estar!" → "sala_de_estar". */
    public static String slug(String name) {
        if (name == null) return "";
        String plain = Normalizer.normalize(name, Normalizer.Form.NFD).replaceAll("\\p{M}+", "");
        return plain.toLowerCase().replaceAll("[^a-z0-9]+", "_").replaceAll("^_+|_+$", "");
    }
}
