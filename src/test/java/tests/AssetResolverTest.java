package tests;

import model.Item;
import model.StoryScene;
import org.junit.jupiter.api.Test;
import service.AssetResolver;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class AssetResolverTest {

    private static AssetResolver with(String... files) {
        Set<String> set = Set.of(files);
        return new AssetResolver(set::contains);
    }

    @Test
    public void slugRemovesAccentsAndPunctuation() {
        assertEquals("sala_de_estar", AssetResolver.slug("Sala de Estar!"));
        assertEquals("expressao_serena", AssetResolver.slug("  Expressão  Serena "));
        assertEquals("", AssetResolver.slug(null));
    }

    @Test
    public void backgroundUsesExplicitIdBeforeTitle() {
        StoryScene scene = new StoryScene("Quarto", "d");
        scene.setBackgroundId("quarto_noite");
        AssetResolver resolver = with("/images/backgrounds/quarto_noite.png", "/images/backgrounds/quarto.png");

        assertEquals("/images/backgrounds/quarto_noite.png", resolver.background(scene).orElseThrow());
    }

    @Test
    public void backgroundFallsBackToTitleThenDefaultThenEmpty() {
        StoryScene scene = new StoryScene("Quarto", "d");

        assertEquals("/images/backgrounds/quarto.jpg", with("/images/backgrounds/quarto.jpg").background(scene).orElseThrow());
        assertEquals("/images/backgrounds/default.png", with("/images/backgrounds/default.png").background(scene).orElseThrow());
        assertTrue(with().background(scene).isEmpty());
    }

    @Test
    public void spriteFollowsCharacterAndExpressionWithFallbacks() {
        AssetResolver resolver = with("/images/characters/arthur/envergonhado.png", "/images/characters/arthur/default.png");

        assertEquals("/images/characters/arthur/envergonhado.png", resolver.sprite("Arthur", "Envergonhado").orElseThrow());
        assertEquals("/images/characters/arthur/default.png", resolver.sprite("Arthur", "Raiva").orElseThrow());
        assertTrue(resolver.sprite("Emily", "Normal").isEmpty());
    }

    @Test
    public void narratorNeverHasASprite() {
        AssetResolver resolver = with("/images/characters/narrador/default.png");

        assertTrue(resolver.sprite("Narrador", "Nada").isEmpty());
        assertTrue(resolver.sprite(null, "Normal").isEmpty());
    }

    @Test
    public void itemImageFallsBackToDefault() {
        Item item = new Item("Poção de Cura", "d", "CONSUMABLE", 10, 5.0);

        assertEquals("/images/items/pocao_de_cura.png", with("/images/items/pocao_de_cura.png").itemImage(item).orElseThrow());
        assertEquals("/images/items/default.png", with("/images/items/default.png").itemImage(item).orElseThrow());
        assertTrue(with().itemImage(item).isEmpty());
    }
}
