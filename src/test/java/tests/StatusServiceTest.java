package tests;

import model.Player;
import org.junit.jupiter.api.Test;
import repository.NpcRepository;
import service.AffinityTracker;
import service.StatusService;
import service.StatusService.NpcStatus;
import service.StatusService.PlayerStatus;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

/** A tela de status mostra atributos, relações, missões e decisões sem entregar quem ainda não apareceu. */
public class StatusServiceTest {
    private final StatusService service = new StatusService(new NpcRepository());

    private static Optional<NpcStatus> byName(PlayerStatus status, String name) {
        return status.npcs().stream().filter(n -> n.name().contains(name)).findFirst();
    }

    @Test
    public void reportsPlayerAttributes() {
        Player player = new Player("O+");
        player.setEnergy(70);
        player.setMoney(12.5);

        PlayerStatus status = service.build(player, new AffinityTracker(), 3);

        assertEquals(3, status.chapter());
        assertEquals(70, status.energy());
        assertEquals(12.5, status.money(), 0.001);
        assertEquals("O+", status.bloodType());
    }

    @Test
    public void listsAffinityAndSuspicionOfKnownNpcs() {
        AffinityTracker tracker = new AffinityTracker();
        tracker.modifyAffinity("Arthur", 15);
        tracker.modifyAffinity("Gabriel", 10);
        tracker.modifySuspicion("Gabriel", 25);

        PlayerStatus status = service.build(new Player("A+"), tracker, 1);

        NpcStatus arthur = byName(status, "Arthur").orElseThrow();
        assertTrue(arthur.known());
        assertEquals(15, arthur.affinity());
        assertFalse(arthur.vampire());

        NpcStatus gabriel = byName(status, "Gabriel").orElseThrow();
        assertEquals(10, gabriel.affinity());
        assertEquals(25, gabriel.suspicion());
        assertTrue(gabriel.vampire());
        assertTrue(gabriel.suspicionLimit() > 25);
    }

    @Test
    public void unmetNpcsAreHiddenBehindQuestionMarks() {
        PlayerStatus status = service.build(new Player("A+"), new AffinityTracker(), 1);

        assertFalse(status.npcs().isEmpty());
        for (NpcStatus npc : status.npcs()) {
            assertFalse(npc.known());
            assertEquals("???", npc.name());
        }
    }

    @Test
    public void missionsUnlockAsTheStoryProgresses() {
        Player player = new Player("A+");
        assertEquals(1, service.build(player, new AffinityTracker(), 1).missions().size());

        player.setFlag("MISSAO_ACEITA", true);
        List<StatusService.MissionStatus> afterAccept = service.build(player, new AffinityTracker(), 1).missions();
        assertEquals(2, afterAccept.size());
        assertTrue(afterAccept.get(0).done());
        assertFalse(afterAccept.get(1).done());

        player.setFlag("CONVITE_MANSAO_OBTIDO", true);
        List<StatusService.MissionStatus> afterInvite = service.build(player, new AffinityTracker(), 1).missions();
        assertEquals(3, afterInvite.size());
        assertTrue(afterInvite.get(1).done());
    }

    @Test
    public void decisionsListOnlyFlagsThePlayerSet() {
        Player player = new Player("A+");
        assertTrue(service.build(player, new AffinityTracker(), 1).decisions().isEmpty());

        player.setFlag("FLERTOU_PEDRO", true);
        player.setFlag("ARTHUR_GENTIL", true);

        List<String> decisions = service.build(player, new AffinityTracker(), 1).decisions();
        assertEquals(2, decisions.size());
        assertTrue(decisions.contains("Flertou com Santo Pedro"));
        assertTrue(decisions.contains("Foi gentil com Arthur"));
    }
}
