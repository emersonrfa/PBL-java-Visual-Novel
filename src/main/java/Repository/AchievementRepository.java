package Repository;

import Model.Achievement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class AchievementRepository {
    private Map<String, Achievement> achievements;

    public AchievementRepository() {
        this.achievements = new LinkedHashMap<>();
        loadAchievements();
    }

    private void loadAchievements() {
        // Conquistas de Início e Progresso
        achievements.put("ACH_PRIMEIROS_PASSOS", new Achievement("ACH_PRIMEIROS_PASSOS",
                "Primeiros Passos", "Iniciou a jornada de Emily e sobreviveu ao prólogo."));

        achievements.put("ACH_BOM_DIA", new Achievement("ACH_BOM_DIA",
                "Pronta para o Dia", "Tomou café da manhã e preparou-se para sair de casa."));

        // Conquistas de Relacionamento e Diálogo
        achievements.put("ACH_AMIGO_VELHO", new Achievement("ACH_AMIGO_VELHO",
                "Reencontro Amigável", "Tratou Arthur cordialmente no mural da rua."));

        achievements.put("ACH_CURIOSA", new Achievement("ACH_CURIOSA",
                "Aparências Noturnas", "Observou a vizinhança pela janela durante a noite."));

        // Conquistas de Finais e Segredos
        achievements.put("ACH_PRIMEIRO_FIM", new Achievement("ACH_PRIMEIRO_FIM",
                "Destino Selado", "Descobriu o seu primeiro desfecho na história."));

        achievements.put("ACH_MESTRE_FALA", new Achievement("ACH_MESTRE_FALA",
                "Completista da Corte", "Desbloqueou todos os finais disponíveis.", true));
    }

    public Achievement getAchievement(String id) {
        return achievements.get(id);
    }

    public List<Achievement> getAllAchievements() {
        return new ArrayList<>(achievements.values());
    }
}