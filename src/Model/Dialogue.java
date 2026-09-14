package Model;

import java.util.ArrayList;
import java.util.List;

public class Dialogue {
    private String id;
    private String text;
    private String speaker;
    private String expression;
    private List<Options> options;

    // construtor completo
    public Dialogue(String id, String speaker, String text, String expression) {
        this.id = id;
        this.speaker = speaker;
        this.text = text;
        this.expression = expression;
        this.options = new ArrayList<>();
    }

    // dialogo com narrador (ambiente, sem conversante e etc)
    public Dialogue(String id, String text) {
        this(id, "Narrador", text, "Nada");
    }

    // dialogo de narrativa linear (para quando eh um dialogo atras do outro com "...")
    public Dialogue(String id, String text, String nextDialogueId) {
        this(id, "Narrador", text, "Nada");
        this.options.add(new Options(nextDialogueId));
    }

    // dialogo de personagem falando linearmente (pra qnd precisar botar conversante e expressao tbm)
    public Dialogue(String id, String speaker, String expression, String text, String nextDialogueId) {
        this(id, speaker, text, expression);
        this.options.add(new Options(nextDialogueId));
    }

    public void addOption(Options option) {
            this.options.add(option);
    }

    public List<Options> getOptions() { return options; }
    public String getExpression() { return expression; }
    public String getId() { return id; }
    public String getSpeaker() { return speaker; }
    public String getText() { return text; }
    public boolean hasOptions() { return !options.isEmpty(); }
}
