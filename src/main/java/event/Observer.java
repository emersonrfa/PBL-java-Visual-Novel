package event;

/**
 * Padrão Observer: quem quer saber do que acontece (tela, HUD, som, log) implementa isto e se
 * registra num {@link Subject}, sem que o emissor conheça ou dependa de quem está ouvindo.
 */
@FunctionalInterface
public interface Observer<E> {
    void onEvent(E event);
}
