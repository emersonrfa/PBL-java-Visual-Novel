package ui.navigation;

import event.Observer;
import event.Subject;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * Estado da navegação entre telas (qual está aberta e o histórico para "Voltar"), sem nada de
 * JavaFX, e por isso testável. O {@link ScreenNavigator} observa este modelo e troca a tela visível.
 *
 * Outros interessados (ex.: trocar a música a cada tela) podem se registrar do mesmo modo.
 */
public class NavigationModel {

    /** {@code from} é nulo na primeira navegação. */
    public record NavigationEvent(ScreenId from, ScreenId to) {}

    private final Subject<NavigationEvent> events = new Subject<>();
    private final Deque<ScreenId> history = new ArrayDeque<>();
    private ScreenId current;

    public ScreenId current() {
        return current;
    }

    public boolean canGoBack() {
        return !history.isEmpty();
    }

    public void addObserver(Observer<NavigationEvent> observer) {
        events.addObserver(observer);
    }

    public void removeObserver(Observer<NavigationEvent> observer) {
        events.removeObserver(observer);
    }

    /** Abre a tela empilhando a atual no histórico (para "Voltar"). */
    public void goTo(ScreenId target) {
        if (target == current) return;
        if (current != null) history.push(current);
        change(target);
    }

    /** Abre a tela sem guardar a atual no histórico (ex.: sair do jogo para o menu). */
    public void replaceWith(ScreenId target) {
        if (target == current) return;
        change(target);
    }

    /** Abre a tela e esquece o histórico (ex.: voltar ao menu principal). */
    public void resetTo(ScreenId target) {
        history.clear();
        if (target == current) return;
        change(target);
    }

    /** Volta à tela anterior; devolve false se não há histórico. */
    public boolean back() {
        if (history.isEmpty()) return false;
        change(history.pop());
        return true;
    }

    private void change(ScreenId target) {
        ScreenId from = current;
        current = target;
        events.publish(new NavigationEvent(from, target));
    }
}
