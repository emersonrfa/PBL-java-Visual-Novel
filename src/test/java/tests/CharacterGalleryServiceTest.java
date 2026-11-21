package tests;

import org.junit.jupiter.api.Test;
import repository.NpcRepository;
import service.AssetResolver;
import service.CharacterGalleryService;
import service.CharacterGalleryService.GalleryEntry;

import java.util.List;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/** A galeria lista Emily e os NPCs com descrição e só os sprites que existem de verdade. */
public class CharacterGalleryServiceTest {

    private static CharacterGalleryService serviceWith(Set<String> existingFiles) {
        return new CharacterGalleryService(new NpcRepository(), new AssetResolver(existingFiles::contains));
    }

    private static GalleryEntry named(List<GalleryEntry> entries, String name) {
        return entries.stream().filter(e -> e.name().contains(name)).findFirst().orElseThrow();
    }

    @Test
    public void emilyComesFirstFollowedByAllNpcs() {
        List<GalleryEntry> entries = serviceWith(Set.of()).entries();

        assertEquals("Emily", entries.get(0).name());
        assertEquals("Protagonista", entries.get(0).role());
        assertEquals(1 + new NpcRepository().getAllNpcs().size(), entries.size());
    }

    @Test
    public void everyEntryHasDescriptionAndRole() {
        for (GalleryEntry entry : serviceWith(Set.of()).entries()) {
            assertFalse(entry.description() == null || entry.description().isBlank(), entry.name() + " sem descrição");
            assertFalse(entry.role() == null || entry.role().isBlank(), entry.name() + " sem papel");
        }
    }

    @Test
    public void vampiresAreMarked() {
        List<GalleryEntry> entries = serviceWith(Set.of()).entries();

        assertTrue(named(entries, "Gabriel").vampire());
        assertFalse(named(entries, "Arthur").vampire());
        assertFalse(entries.get(0).vampire());
    }

    @Test
    public void listsOnlySpritesThatExistWithDefaultFirst() {
        List<GalleryEntry> entries = serviceWith(Set.of(
                "/images/characters/emily/default.png",
                "/images/characters/emily/sorrindo.png",
                "/images/characters/emily/serio.jpg")).entries();

        List<service.CharacterGalleryService.SpriteEntry> sprites = entries.get(0).sprites();

        assertEquals(3, sprites.size());
        assertEquals("Padrão", sprites.get(0).label());
        assertEquals("/images/characters/emily/default.png", sprites.get(0).path());
        assertEquals("Sorrindo", sprites.get(1).label());
        assertEquals("Sério", sprites.get(2).label());
    }

    @Test
    public void characterWithOnlyTheDefaultSpriteShowsOnlyIt() {
        List<GalleryEntry> entries = serviceWith(Set.of("/images/characters/arthur/default.png")).entries();

        GalleryEntry arthur = named(entries, "Arthur");

        assertEquals(1, arthur.sprites().size());
        assertEquals("Padrão", arthur.sprites().get(0).label());
    }

    @Test
    public void characterWithoutImagesHasNoSprites() {
        GalleryEntry arthur = named(serviceWith(Set.of()).entries(), "Arthur");

        assertFalse(arthur.hasSprites());
    }

    @Test
    public void spriteFolderFollowsTheRepositoryKeyEvenForSurnamedNames() {
        // "Raphael Domus" usa a pasta "raphael" (a chave do repositório e do roteiro), não "raphael_domus"
        GalleryEntry raphael = named(serviceWith(Set.of("/images/characters/raphael/default.png")).entries(), "Raphael");

        assertEquals(1, raphael.sprites().size());
    }

    @Test
    public void everyCharacterFolderInTheProjectMatchesAGalleryEntry() throws Exception {
        // pega pastas com nome errado (ex.: "rafael" em vez de "raphael"): o sprite nunca seria encontrado
        List<GalleryEntry> entries = new CharacterGalleryService(new NpcRepository(), new AssetResolver()).entries();
        java.nio.file.Path dir = java.nio.file.Path.of("src/main/resources/images/characters");
        if (!java.nio.file.Files.isDirectory(dir)) return; // rodando fora da raiz do projeto

        try (var folders = java.nio.file.Files.list(dir)) {
            for (java.nio.file.Path folder : (Iterable<java.nio.file.Path>) folders.filter(java.nio.file.Files::isDirectory)::iterator) {
                String name = folder.getFileName().toString();
                boolean used = entries.stream().anyMatch(e -> e.hasSprites()
                        && e.sprites().get(0).path().startsWith("/images/characters/" + name + "/"));
                boolean isNpcOrEmily = used || name.equals("atendente"); // atendente: só fala, não é NPC da galeria
                assertTrue(isNpcOrEmily, "pasta de sprite sem personagem correspondente: " + name);
            }
        }
    }
}
