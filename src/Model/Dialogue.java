package Model;

public class Dialogue {
    private String id;
    private String speaker;
    private String text;
    private String expression;

    //constructor method
    public Dialogue(String id, String speaker, String text, String expression) {
        this.id = id;
        this.speaker = speaker;
        this.text = text;
        this.expression = expression;
    }
}
