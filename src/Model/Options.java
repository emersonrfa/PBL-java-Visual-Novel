package Model;

public class Options {
    private String id;
    private String text;
    private String nextDialogueId;
    private int affinityCost;
    private String requiredItem;
    private int suspectionCost;
    private int maxSuspectionAllowed;

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

    // exemplo: Dialogue d
    // d.addOption(new Options("texto", "id").setEnergyChange(-x));
    // d.addOption(new Options("text", "id").setMoneyChange(-y));

    public int getEnergyChange() { return energyChange; }
    public double getMoneyChange() { return moneyChange; }

    public String getText() { return text; }
    public String getNextDialogueId() { return nextDialogueId; }
}
