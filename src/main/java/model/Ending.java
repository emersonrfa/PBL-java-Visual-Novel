package model;

public class Ending {
    /** Prefixo que identifica, num próximo-diálogo, um código de final (ex.: FINAL_VAMPIRA). */
    public static final String CODE_PREFIX = "FINAL_";

    private String id;
    private String title;
    private String description;

    public Ending(String id,String description,String title){
        this.description = description;
        this.id= id;
        this.title = title;
    }

    public String getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

}
