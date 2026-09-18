package Model;

public class Options {
    private String id;
    private String text;
    private String nextDialogueId;
    private int affinityCost;
    private String requiredItem;
    private int suspectionCost;
    private int maxSuspectionAllowed;

    // atributos para controle de flags e afinidades:
    private String flagToSet = null; // flag alterada
    private String targetNpc = null; // npc afetado

    // nao precisava fazer o construtor completo toda vez, dava pra simplificar assim:

    private int energyChange = 0;
    private double moneyChange = 0.0;
    private int affinityChange = 0;
    private String requiredFlag = null; // isso é para determinar se ela conseguiu um item importante ou viu alguma coisa

    // construtor completo
    public Options(String id, String text, String nextDialogueId, String requiredItem,
                   int affinityCost, int suspectionCost, int maxSuspectionAllowed) {
        this.id = id;
        this.text = text;
        this.nextDialogueId = nextDialogueId;
        this.affinityCost = affinityCost;
        this.requiredItem = requiredItem;
        this.suspectionCost = suspectionCost;
        this.maxSuspectionAllowed = maxSuspectionAllowed;
    }

    // Construtor para opção sem pré requisitos de item ou suspeita
    public Options(String text, String nextDialogueId) {
        this("", text, nextDialogueId, "", 0, 0, 0);
    }

    // Construtor para apenas avançar texto
    public Options(String nextDialogueId) {
        this("", "...", nextDialogueId, "", 0, 0, 0);
    }

    // fiz com set pq descobri q vai facilitar no StoryBuilder

    public Options setEnergyChange(int amount) {
        this.energyChange = amount;
        return this;
    }
    public Options setMoneyChange(double amount) {
        this.moneyChange = amount;
        return this;
    }
    public Options setAffinityChange(int amount) {
        this.affinityChange = amount;
        return this;
    }

    // setters e getters de flag e afinidades:

    public Options setRequiredFlag(String flag) {
        this.requiredFlag = flag;
        return this;
    }

    public Options setFlagToSet(String flag) {
        this.flagToSet = flag;
        return this;
    }

    public Options setAffinityChange(String npcName, int amount) {
        this.targetNpc = npcName;
        this.affinityChange = amount;
        return this;
    }

    public String getTargetNpc() { return targetNpc; }
    public String getFlagToSet() { return flagToSet; }
    public String getRequiredFlag() { return requiredFlag; }
    public int getAffinityChange() { return affinityChange; }

    // meotodo para verificar se opção esta disponivel

    public boolean isAvailable(Player player) {
        if (this.moneyChange < 0 && player.getMoney() < Math.abs(this.moneyChange)) {
            return false;
        }

        if (this.requiredFlag != null && !player.getFlag(this.requiredFlag)) {
            return false;
        }

        if ((this.energyChange) < 0 && player.getEnergy() < Math.abs(this.energyChange)) {
            return false;
        }

        // dps criar logica do inventario

        return true;
    }

    // exemplo: Dialogue d
    // d.addOption(new Options("texto", "id").setEnergyChange(-x));
    // d.addOption(new Options("text", "id").setMoneyChange(-y));

    public int getEnergyChange() { return energyChange; }
    public double getMoneyChange() { return moneyChange; }

    public String getText() { return text; }
    public String getNextDialogueId() { return nextDialogueId; }
}
