package tests;

import model.Chapter;
import model.Choice;
import model.Dialogue;
import model.Player;
import model.StoryScene;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import repository.AchievementRepository;
import repository.ChapterCatalog;
import repository.ItemRepository;
import repository.StoryRepository;
import repository.UnlockedAchievementsRepository;
import service.AchievementService;
import service.AffinityTracker;
import service.ChoiceEvaluator;
import service.PlayerService;
import service.StoryService;

import java.nio.file.Path;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Percorre TODOS os caminhos possíveis da campanha (capítulos registrados, em sequência) usando as
 * mesmas regras do jogo e garante que nenhum jogador fica preso sem opções disponíveis.
 * Também cobre as correções da revisão dos capítulos 3 e 4.
 */
public class CampaignIntegrityTest {
    @TempDir
    Path tempDir;

    private final ChapterCatalog catalog = new StoryRepository().buildCatalog();
    private final PlayerService playerService = new PlayerService();

    private AchievementService achievements;

    @BeforeEach
    public void setUp() {
        achievements = new AchievementService(new AchievementRepository(), new UnlockedAchievementsRepository(tempDir.toString()));
    }

    // ------------------------------------------------------------------ exploração completa

    private record Node(int chapter, String dialogueId, Player player, AffinityTracker tracker) {}

    /**
     * Chave de estado "equivalente para as regras de bloqueio": o que decide se uma escolha está
     * disponível (flags, itens, sinal da afinidade e suspeita acima do limite usado no jogo).
     * Energia e dinheiro ficam de fora porque, com os valores atuais, nunca bloqueiam sozinhos.
     */
    private static String key(Node n) {
        TreeMap<String, String> npcs = new TreeMap<>();
        n.tracker().getNpcAffinities().forEach((k, v) -> npcs.put(k, v >= 0 ? "+" : "-"));
        n.tracker().getNpcSuspicions().forEach((k, v) -> npcs.merge(k, v > 55 ? "S" : "s", String::concat));
        return n.chapter() + "|" + n.dialogueId() + "|" + new TreeMap<>(n.player().getFlags()) + "|"
                + n.player().getInventory().getItemNames().stream().sorted().toList() + "|" + npcs;
    }

    private static Player copy(Player p) {
        Player n = new Player(p.getBloodType());
        n.setEnergy(p.getEnergy());
        n.setMoney(p.getMoney());
        p.getFlags().forEach(n::setFlag);
        p.getInventory().getItems().forEach(n.getInventory()::addItem);
        return n;
    }

    private static AffinityTracker copy(AffinityTracker t) {
        AffinityTracker n = new AffinityTracker();
        n.setNpcAffinities(t.getNpcAffinities());
        n.setNpcSuspicions(t.getNpcSuspicions());
        return n;
    }

    @Test
    public void everyPathOfTheCampaignHasAnAvailableChoiceAndEveryDialogueIsReachable() {
        Set<String> seen = new HashSet<>();
        Set<String> visited = new HashSet<>();
        Deque<Node> stack = new ArrayDeque<>();
        int first = catalog.numbers().iterator().next();
        stack.push(new Node(first, catalog.get(first).getScenes().get(0).getDialogues().get(0).getId(),
                new Player("O+"), new AffinityTracker()));

        while (!stack.isEmpty()) {
            Node node = stack.pop();
            int chapterNumber = node.chapter();
            String id = node.dialogueId();
            if (id == null || id.isBlank()) { // fim do capítulo: segue para o próximo registrado
                Integer next = catalog.numbers().stream().filter(n -> n > node.chapter()).findFirst().orElse(null);
                if (next == null) continue;
                chapterNumber = next;
                id = catalog.get(next).getScenes().get(0).getDialogues().get(0).getId();
            }
            if (id.startsWith(model.Ending.CODE_PREFIX)) continue;

            Node current = new Node(chapterNumber, id, node.player(), node.tracker());
            if (!seen.add(key(current))) continue;

            Chapter chapter = catalog.get(chapterNumber);
            StoryScene scene = StoryService.findSceneByDialogueId(chapter, id);
            assertNotNull(scene, "capítulo " + chapterNumber + ": id inexistente '" + id + "'");
            Dialogue dialogue = scene.getDialogueById(id);
            visited.add(chapterNumber + ":" + id);

            ChoiceEvaluator evaluator = new ChoiceEvaluator(playerService, current.tracker());
            boolean anyAvailable = false;
            for (Choice choice : dialogue.getChoices()) {
                if (!evaluator.evaluate(current.player(), choice).available()) continue;
                anyAvailable = true;
                Player p = copy(current.player());
                AffinityTracker t = copy(current.tracker());
                playerService.applyChoiceConsequences(p, choice, t);
                stack.push(new Node(chapterNumber, choice.getNextDialogueId(), p, t));
            }
            assertTrue(anyAvailable, "jogador preso no capítulo " + chapterNumber + ", diálogo '" + id
                    + "': todas as escolhas estão bloqueadas (afinidades: " + current.tracker().getNpcAffinities() + ")");
        }

        for (int number : catalog.numbers()) {
            for (StoryScene scene : catalog.get(number).getScenes()) {
                for (Dialogue d : scene.getDialogues()) {
                    assertTrue(visited.contains(number + ":" + d.getId()),
                            "diálogo inalcançável por jogo real: capítulo " + number + " '" + d.getId() + "'");
                }
            }
        }
    }

    // ------------------------------------------------------------------ regressões da revisão

    @Test
    public void negativeAffinityDoesNotBlockChoicesWithoutAnAffinityRequirement() {
        AffinityTracker tracker = new AffinityTracker();
        tracker.modifyAffinity("Arthur", -5); // grosseria com Arthur no capítulo 2
        Choice choice = new Choice("Aceitar o café", "d1.4.1").setTargetNpc("Arthur").setAffinityChange(5);

        assertTrue(new ChoiceEvaluator(playerService, tracker).evaluate(new Player("O+"), choice).available());
    }

    @Test
    public void explicitAffinityRequirementStillBlocks() {
        AffinityTracker tracker = new AffinityTracker();
        tracker.modifyAffinity("Luna", 5);
        Choice choice = new Choice("Pedido especial", "x").setTargetNpc("Luna").setRequiredAffinity(20);

        assertFalse(new ChoiceEvaluator(playerService, tracker).evaluate(new Player("O+"), choice).available());
    }

    @Test
    public void choiceCanAffectASecondNpc() {
        AffinityTracker tracker = new AffinityTracker();
        Choice choice = new Choice("Elogiar a arquitetura", "d2.6.3")
                .setTargetNpc("Luna").setAffinityChange(5)
                .alsoAffect("Miguel", 0, 10);

        playerService.applyChoiceConsequences(new Player("O+"), choice, tracker);

        assertEquals(5, tracker.getAffinity("Luna"));
        assertEquals(10, tracker.getSuspicion("Miguel"));
    }

    @Test
    public void everyRequiredItemAndFlagExistsSomewhereInTheStory() {
        ItemRepository items = new ItemRepository();
        Set<String> itemNames = new HashSet<>();
        items.getAllItems().values().forEach(i -> itemNames.add(i.getName().toLowerCase()));
        Set<String> flagsSet = new HashSet<>();
        for (int number : catalog.numbers())
            for (StoryScene s : catalog.get(number).getScenes())
                for (Dialogue d : s.getDialogues())
                    for (Choice c : d.getChoices())
                        if (c.getFlagToSet() != null) flagsSet.add(c.getFlagToSet());

        for (int number : catalog.numbers()) {
            for (StoryScene s : catalog.get(number).getScenes()) {
                for (Dialogue d : s.getDialogues()) {
                    for (Choice c : d.getChoices()) {
                        if (c.getRequiredItemName() != null) {
                            assertTrue(itemNames.contains(c.getRequiredItemName().toLowerCase()),
                                    "item exigido inexistente em " + number + ":" + d.getId() + " -> " + c.getRequiredItemName());
                        }
                        if (c.getRequiredFlag() != null) {
                            assertTrue(flagsSet.contains(c.getRequiredFlag()),
                                    "flag exigida nunca é definida em " + number + ":" + d.getId() + " -> " + c.getRequiredFlag());
                        }
                    }
                }
            }
        }
    }

    @Test
    public void storyFlagsUnlockTheirAchievements() {
        assertEquals("ACH_CONTRATADA", achievements.evaluateFlagTrigger("MISSAO_ACEITA").getId());
        assertEquals("ACH_GALANTEADORA", achievements.evaluateFlagTrigger("FLERTOU_PEDRO").getId());
        assertEquals("ACH_DISFARCE_PERFEITO", achievements.evaluateFlagTrigger("CONVITE_MANSAO_OBTIDO").getId());
        assertEquals("ACH_HOSPEDE_NOBILIS", achievements.evaluateFlagTrigger("MANSAO_EXPLORADA").getId());
        assertNull(achievements.evaluateFlagTrigger("MANSAO_EXPLORADA"), "só dispara na primeira vez");
    }
}
