package Model;

public class Achievement {
    private String id;
    private String title;
    private String description;
    private boolean secret;

    public Achievement(String id, String title, String description) {
        this(id, title, description, false);
    }

    public Achievement(String id, String title, String description, boolean secret) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.secret = secret;
    }

    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public boolean isSecret() { return secret; }
}
