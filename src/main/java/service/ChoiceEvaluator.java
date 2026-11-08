package service;

import model.Choice;
import model.Player;

/**
 * Decide se uma escolha está disponível para o jogador agora e, se não, por quê.
 * Antes essa lógica morava dentro do controller do console; agora qualquer tela (console ou JavaFX)
 * usa a mesma regra.
 */
public final class ChoiceEvaluator {

    /** {@code blockReason} é nulo quando disponível; quando bloqueada pode ser vazio (motivo desconhecido). */
    public record ChoiceAvailability(boolean available, String blockReason) {
        static ChoiceAvailability ok() {
            return new ChoiceAvailability(true, null);
        }
    }

    private final PlayerService playerService;
    private final AffinityTracker affinityTracker;

    public ChoiceEvaluator(PlayerService playerService, AffinityTracker affinityTracker) {
        this.playerService = playerService;
        this.affinityTracker = affinityTracker;
    }

    public ChoiceAvailability evaluate(Player player, Choice choice) {
        if (playerService.validatePlayerRequirements(player, choice) && isAffinityValid(choice)) {
            return ChoiceAvailability.ok();
        }
        return new ChoiceAvailability(false, reasonFor(player, choice));
    }

    private boolean isAffinityValid(Choice choice) {
        String npc = choice.getTargetNpc();
        if (npc == null) return true;
        return affinityTracker.getAffinity(npc) >= choice.getRequiredAffinity()
                && affinityTracker.getSuspicion(npc) <= choice.getMaxSuspicionAllowed();
    }

    private String reasonFor(Player player, Choice opt) {
        if (opt.getMoneyChange() < 0 && player.getMoney() < Math.abs(opt.getMoneyChange())) {
            return "[Trocos insuficientes]";
        }
        if (opt.getRequiredFlag() != null && !player.getFlag(opt.getRequiredFlag())) {
            return "[???]";
        }
        if (opt.getEnergyChange() < 0 && player.getEnergy() < Math.abs(opt.getEnergyChange())) {
            return "[Energia Insuficiente]";
        }
        if (opt.getRequiredItemName() != null && !player.getInventory().hasItem(opt.getRequiredItemName())) {
            return "[Requer: " + opt.getRequiredItemName() + "]";
        }
        if (opt.getTargetNpc() != null) {
            if (affinityTracker.getAffinity(opt.getTargetNpc()) < opt.getRequiredAffinity()) {
                return "[Afinidade Insuficiente com " + opt.getTargetNpc() + "]";
            }
            if (affinityTracker.getSuspicion(opt.getTargetNpc()) > opt.getMaxSuspicionAllowed()) {
                return "[Você é suspeita demais para esta opção]";
            }
        }
        return "";
    }
}
