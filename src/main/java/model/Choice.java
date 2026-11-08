package model;

public class Choice {
    /** Texto reservado das escolhas "apenas avançar" (diálogo linear). */
    public static final String CONTINUE_TEXT = "...";

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
    private java.util.List<Item> itemsToGive = new java.util.ArrayList<>();
    private String requiredItemName = null;
    private boolean consumeRequiredItem = false;

    // atributos pros relacionamentos
    private int requiredAffinity = 0;
    private int maxSuspicionAllowed = 100;
    private int suspicionChange = 0;

    // Construtor completo: texto exibido + próximo diálogo
    public Choice(String text, String nextDialogueId) {
        this.text = text;
        this.nextDialogueId = nextDialogueId;
    }

    // Construtor para apenas avançar texto
    public Choice(String nextDialogueId) {
        this(CONTINUE_TEXT, nextDialogueId);
    }

    // setters e getters

    public Choice setEnergyChange(int amount) {
        this.energyChange = amount;
        return this;
    } // SET ENERGIA MANIPULADA

    public Choice setMoneyChange(double amount) {
        this.moneyChange = amount;
        return this;
    } // SET DINHEIRO MANIPULADO

    public Choice setAffinityChange(int amount) {
        this.affinityChange = amount;
        return this;
    } // SET MUDANÇA DE AFINIDADE

    public Choice setRequiredFlag(String flag) {
        this.requiredFlag = flag;
        return this;
    } // SET FLAG REQUISITADA

    public Choice setFlagToSet(String flag) {
        this.flagToSet = flag;
        return this;
    } // SET FLAG ALTERADA

    public java.util.List<Item> getItemsToGive() {
        return itemsToGive;
    }

    public Choice setItemToGive(Item item) {
        if (item != null) {
            this.itemsToGive.add(item);
        }
        return this;
    }

    public String getTargetNpc() { return targetNpc; } // GET NPC AFETADO

    public String getFlagToSet() { return flagToSet; } // GET FLAG ALTERADA

    public String getRequiredFlag() { return requiredFlag; } // GET FLAG NECESSARIA

    public int getAffinityChange() { return affinityChange; } // GET MUDANÇA DE AFINIDADE

    public Choice setTargetNpc(String npcName) {
        this.targetNpc = npcName;
        return this;
    } // SET NPC AFETADO

    public Choice setRequiredAffinity(int amount) {
        this.requiredAffinity = amount;
        return this;
    } // SET AFINIDADE NECESSARIA

    public Choice setMaxSuspicionAllowed(int amount) {
        this.maxSuspicionAllowed = amount;
        return this;
    } // SET MAXIMO DE SUSPEITA PERMITIDO

    public Choice setSuspicionChange(int amount) {
        this.suspicionChange = amount;
        return this;
    } // GET MUDANÇA DE SUSPEITA

    public int getSuspicionChange() { return suspicionChange; } // GET MUDANÇA DE SUSPEITA

    public int getRequiredAffinity() { return requiredAffinity; } // GET AFINIDADE NECESSARIA

    public int getMaxSuspicionAllowed() { return maxSuspicionAllowed; } // GET MAXIMO DE SUSPEITA PERMITIDO

    // meotodo para verificar se opção esta disponivel

    // exemplo: Dialogue d
    // d.addChoice(new Choice("texto", "id").setEnergyChange(-x));
    // d.addChoice(new Choice("text", "id").setMoneyChange(-y));

    public int getEnergyChange() { return energyChange; }

    public double getMoneyChange() { return moneyChange; }

    public String getText() { return text; }

    /** Verdadeiro para a escolha "apenas avançar" criada por {@link #Choice(String)}. */
    public boolean isContinue() { return CONTINUE_TEXT.equals(text); }

    public String getNextDialogueId() { return nextDialogueId; }

    // para iventario e itens:

    public String getRequiredItemName() { return requiredItemName; }
    public boolean isConsumeRequiredItem() { return consumeRequiredItem; }

    public Choice setRequiredItem(String itemName, boolean consume) { //  seta um item necessario pra opcao
        this.requiredItemName = itemName;
        this.consumeRequiredItem = consume;
        return this;
    }
}
