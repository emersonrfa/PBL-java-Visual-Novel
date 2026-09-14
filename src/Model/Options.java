package Model;

public class Options {
    private String id;
    private String text;
    private String nextDialogueId;
    private int affinityCost;
    private String requiredItem;
    private int suspectionCost;
    private int maxSuspectionAllowed;

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

    public String getText() { return text; }
    public String getNextDialogueId() { return nextDialogueId; }
}
