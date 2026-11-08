package main;

import javafx.application.Application;

/**
 * Lançador separado de {@link App}. Uma classe que estende Application não pode ser a "main" de um
 * jar executável/exec:java com JavaFX no classpath ("runtime components are missing"); esta, que
 * não estende, pode. Use-a como Main-Class se um dia empacotar o jogo num jar.
 */
public final class Launcher {
    private Launcher() {}

    public static void main(String[] args) {
        Application.launch(App.class, args);
    }
}
