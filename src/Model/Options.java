package Model;

import Service.AffinityTracker;

public class Options {
    private String id;
    private String text;
    private String nextDialogueId;

    // atributos para controle de flags e afinidades:
    private String flagToSet = null; // flag alterada
    private String requiredFlag = null; // isso é para determinar se ela conseguiu um item importante ou viu alguma coisa

    // atributos de recursos
    private int energyChange = 0;
    private double moneyChange = 0.0;

    // atributos de afinidade
    private int affinityChange = 0;
    private String targetNpc = null; // npc afetado


    // atributos para manipulação de inventario
    private Item itemToGive = null;
    private String requiredItemName = null;
    private boolean consumeRequiredItem = false;

    // atributos pros relacionamentos
    private int requiredAffinity = 0;
    private int maxSuspicionAllowed = 100;
    private int suspicionChange = 0;

    // construtor completo
    public Options(String id, String text, String nextDialogueId) {
        this.id = id;
        this.text = text;
        this.nextDialogueId = nextDialogueId;
    }

    // Construtor para opção sem pré requisitos de item ou suspeita
    public Options(String text, String nextDialogueId) {
        this("", text, nextDialogueId);
    }

    // Construtor para apenas avançar texto
    public Options(String nextDialogueId) {
        this("", "...", nextDialogueId);
    }

    // setters e getters

    public Options setEnergyChange(int amount) {
        this.energyChange = amount;
        return this;
    } // SET ENERGIA MANIPULADA

    public Options setMoneyChange(double amount) {
        this.moneyChange = amount;
        return this;
    } // SET DINHEIRO MANIPULADO

    public Options setAffinityChange(int amount) {
        this.affinityChange = amount;
        return this;
    } // SET MUDANÇA DE AFINIDADE

    public Options setRequiredFlag(String flag) {
        this.requiredFlag = flag;
        return this;
    } // SET FLAG REQUISITADA

    public Options setFlagToSet(String flag) {
        this.flagToSet = flag;
        return this;
    } // SET FLAG ALTERADA

    public String getTargetNpc() { return targetNpc; } // GET NPC AFETADO

    public String getFlagToSet() { return flagToSet; } // GET FLAG ALTERADA

    public String getRequiredFlag() { return requiredFlag; } // GET FLAG NECESSARIA

    public int getAffinityChange() { return affinityChange; } // GET MUDANÇA DE AFINIDADE

    public Options setTargetNpc(String npcName) {
        this.targetNpc = npcName;
        return this;
    } // SET NPC AFETADO

    public Options setRequiredAffinity(int amount) {
        this.requiredAffinity = amount;
        return this;
    } // SET AFINIDADE NECESSARIA

    public Options setMaxSuspicionAllowed(int amount) {
        this.maxSuspicionAllowed = amount;
        return this;
    } // SET MAXIMO DE SUSPEITA PERMITIDO

    public Options setSuspicionChange(int amount) {
        this.suspicionChange = amount;
        return this;
    } // GET MUDANÇA DE SUSPEITA

    public int getSuspicionChange() { return suspicionChange; } // GET MUDANÇA DE SUSPEITA

    public int getRequiredAffinity() { return requiredAffinity; } // GET AFINIDADE NECESSARIA

    public int getMaxSuspicionAllowed() { return maxSuspicionAllowed; } // GET MAXIMO DE SUSPEITA PERMITIDO

    // meotodo para verificar se opção esta disponivel

    public boolean isAvailable(Player player, AffinityTracker tracker) {
        if (this.moneyChange < 0 && player.getMoney() < Math.abs(this.moneyChange)) {
            return false;
        }

        if (this.requiredFlag != null && !player.getFlag(this.requiredFlag)) {
            return false;
        }

        if (this.energyChange < 0 && player.getEnergy() < Math.abs(this.energyChange)) {
            return false;
        }

        if (this.requiredItemName != null && !player.getInventory().hasItem(this.requiredItemName)) {
            return false;
        }

        if (this.targetNpc != null) {
            if (tracker.getAffinity(this.targetNpc) < this.requiredAffinity)
                return false;
            if (tracker.getSuspicion(this.targetNpc) > this.maxSuspicionAllowed)
                return false;
        }

        return true;
    }

    // exemplo: Dialogue d
    // d.addOption(new Options("texto", "id").setEnergyChange(-x));
    // d.addOption(new Options("text", "id").setMoneyChange(-y));

    public int getEnergyChange() { return energyChange; }

    public double getMoneyChange() { return moneyChange; }

    public String getText() { return text; }

    public String getNextDialogueId() { return nextDialogueId; }

    // para iventario e itens:

    public Item getItemToGive() { return itemToGive; }
    public String getRequiredItemName() { return requiredItemName; }
    public boolean isConsumeRequiredItem() { return consumeRequiredItem; }

    public Options setItemToGive(Item item) { // seta um item para conceder na escolha da opção
        this.itemToGive = item;
        return this;
    }

    public Options setRequiredItem(String itemName, boolean consume) { //  seta um item necessario pra opcao
        this.requiredItemName = itemName;
        this.consumeRequiredItem = consume;
        return this;
    }
}
