package tests;

import model.Choice;
import model.Item;
import model.Player;
import org.junit.jupiter.api.Test;
import service.AffinityTracker;
import service.ConsequenceReport;
import service.ConsequenceReport.Change;
import service.ConsequenceReport.Kind;
import service.PlayerService;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ConsequenceReportTest {
    private final PlayerService service = new PlayerService();

    @Test
    public void reportsEnergyAndMoneyWithBeforeAndAfter() {
        Player player = new Player("A+");
        player.setEnergy(50);
        player.setMoney(20.0);
        Choice choice = new Choice("x", "d2").setEnergyChange(30).setMoneyChange(-5.0);

        ConsequenceReport report = service.applyChoiceConsequences(player, choice, new AffinityTracker());

        assertEquals(80, player.getEnergy());
        assertEquals(15.0, player.getMoney(), 0.001);

        List<Change> energy = report.getChanges(Kind.ENERGY);
        assertEquals(1, energy.size());
        assertEquals(50.0, energy.get(0).before(), 0.001);
        assertEquals(80.0, energy.get(0).after(), 0.001);
        assertEquals(30.0, energy.get(0).delta(), 0.001);

        List<Change> money = report.getChanges(Kind.MONEY);
        assertEquals(1, money.size());
        assertEquals(-5.0, money.get(0).delta(), 0.001);
        assertEquals(2, report.messages().size());
    }

    @Test
    public void energyAlreadyAtMaximumProducesNoChange() {
        Player player = new Player("A+"); // começa com energia máxima
        Choice choice = new Choice("x", "d2").setEnergyChange(50);

        ConsequenceReport report = service.applyChoiceConsequences(player, choice, new AffinityTracker());

        assertTrue(report.isEmpty());
    }

    @Test
    public void flagIsRecordedButNotShownToThePlayer() {
        Player player = new Player("A+");
        Choice choice = new Choice("x", "d2").setFlagToSet("SEGREDO");

        ConsequenceReport report = service.applyChoiceConsequences(player, choice, new AffinityTracker());

        assertTrue(player.getFlag("SEGREDO"));
        assertEquals(1, report.getChanges(Kind.FLAG).size());
        assertEquals("SEGREDO", report.getChanges(Kind.FLAG).get(0).subject());
        assertTrue(report.messages().isEmpty());
    }

    @Test
    public void itemsGainedAndConsumedAreReported() {
        Player player = new Player("A+");
        Item key = new Item("Chave", "Uma chave", "Normal", 0, 0.0);
        Item coat = new Item("Casaco", "Um casaco", "Normal", 0, 0.0);
        player.getInventory().addItem(key);
        Choice choice = new Choice("x", "d2").setRequiredItem("Chave", true).setItemToGive(coat);

        ConsequenceReport report = service.applyChoiceConsequences(player, choice, new AffinityTracker());

        assertEquals(List.of("[-1 Chave (Utilizado)]", "[+ Casaco]"), report.messages());
        assertEquals(1, report.getChanges(Kind.ITEM_USED).size());
        assertEquals(1, report.getChanges(Kind.ITEM_GAINED).size());
    }

    @Test
    public void affinityAndSuspicionAreAppliedToTrackerAndReported() {
        Player player = new Player("A+");
        AffinityTracker tracker = new AffinityTracker();
        Choice choice = new Choice("x", "d2").setTargetNpc("Arthur").setAffinityChange(10).setSuspicionChange(5);

        ConsequenceReport report = service.applyChoiceConsequences(player, choice, tracker);

        assertEquals(10, tracker.getAffinity("Arthur"));
        assertEquals(5, tracker.getSuspicion("Arthur"));
        assertEquals(10.0, report.getChanges(Kind.AFFINITY).get(0).after(), 0.001);
        assertEquals(5.0, report.getChanges(Kind.SUSPICION).get(0).after(), 0.001);
    }

    @Test
    public void choiceWithoutConsequencesProducesEmptyReport() {
        ConsequenceReport report = service.applyChoiceConsequences(
                new Player("A+"), new Choice("d2"), new AffinityTracker());

        assertTrue(report.isEmpty());
        assertTrue(report.messages().isEmpty());
    }
}
