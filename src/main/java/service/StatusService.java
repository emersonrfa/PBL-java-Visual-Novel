package service;

import model.NPC;
import model.Player;
import model.Vampire;
import repository.NpcRepository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Monta a tela de status da protagonista (somente leitura): atributos, relações com os NPCs
 * (afinidade e suspeita), missões e decisões tomadas. Não desenha nada; a tela só exibe o resultado.
 *
 * Quem Emily ainda não encontrou aparece como "???" (afinidade e suspeita zeradas), para o painel
 * não entregar personagens que a história ainda não apresentou.
 */
public class StatusService {

    public record NpcStatus(String name, String role, boolean known, boolean vampire,
                            int affinity, int suspicion, int suspicionLimit) {}

    public record MissionStatus(String title, boolean done) {}

    public record PlayerStatus(int chapter, int energy, double money, String bloodType,
                               List<NpcStatus> npcs, List<MissionStatus> missions, List<String> decisions) {}

    /** Flag → texto da decisão (na ordem em que aparecem na história). */
    private static final Map<String, String> DECISIONS = new LinkedHashMap<>();
    static {
        DECISIONS.put("NOTICIAS", "Leu as notícias antes de sair de casa");
        DECISIONS.put("ARTHUR_GENTIL", "Foi gentil com Arthur");
        DECISIONS.put("JANELAARTHUR", "Chamou Arthur pela janela");
        DECISIONS.put("FLERTOU_PEDRO", "Flertou com Santo Pedro");
        DECISIONS.put("FOCO_MISSAO_1", "Manteve o foco na missão (1)");
        DECISIONS.put("FOCO_MISSAO_2", "Manteve o foco na missão (2)");
        DECISIONS.put("FOCO_MISSAO_3", "Manteve o foco na missão (3)");
        DECISIONS.put("DISTRACAO_VAMP_1", "Distraiu-se com um vampiro (1)");
        DECISIONS.put("DISTRACAO_VAMP_2", "Distraiu-se com um vampiro (2)");
        DECISIONS.put("DISTRACAO_VAMP_3", "Distraiu-se com um vampiro (3)");
    }

    private static final int DEFAULT_SUSPICION_LIMIT = 100;

    private final NpcRepository npcRepository;

    public StatusService(NpcRepository npcRepository) {
        this.npcRepository = npcRepository;
    }

    public PlayerStatus build(Player player, AffinityTracker affinity, int chapter) {
        return new PlayerStatus(chapter, player.getEnergy(), player.getMoney(), player.getBloodType(),
                npcs(affinity), missions(player), decisions(player));
    }

    private List<NpcStatus> npcs(AffinityTracker affinity) {
        List<NpcStatus> list = new ArrayList<>();
        for (Map.Entry<String, NPC> entry : npcRepository.getAllNpcs().entrySet()) {
            String key = entry.getKey();
            NPC npc = entry.getValue();
            int aff = affinity.getAffinity(key);
            int sus = affinity.getSuspicion(key);
            boolean known = aff != 0 || sus != 0;
            boolean vampire = npc instanceof Vampire;
            int limit = vampire ? ((Vampire) npc).getSuspicionThreshold() : DEFAULT_SUSPICION_LIMIT;
            list.add(known
                    ? new NpcStatus(npc.getName(), npc.getRole(), true, vampire, aff, sus, limit)
                    : new NpcStatus("???", "Ainda não conhecido", false, vampire, 0, 0, limit));
        }
        return list;
    }

    private List<MissionStatus> missions(Player player) {
        List<MissionStatus> list = new ArrayList<>();
        list.add(new MissionStatus("Encontrar um emprego", player.getFlag("MISSAO_ACEITA")));
        if (player.getFlag("MISSAO_ACEITA")) {
            list.add(new MissionStatus("Conseguir o convite para a mansão Nobilis", player.getFlag("CONVITE_MANSAO_OBTIDO")));
        }
        if (player.getFlag("CONVITE_MANSAO_OBTIDO")) {
            list.add(new MissionStatus("Explorar a mansão e reunir informações para a Ordem", player.getFlag("MANSAO_EXPLORADA")));
        }
        return list;
    }

    private List<String> decisions(Player player) {
        List<String> list = new ArrayList<>();
        DECISIONS.forEach((flag, text) -> {
            if (player.getFlag(flag)) list.add(text);
        });
        return list;
    }
}
