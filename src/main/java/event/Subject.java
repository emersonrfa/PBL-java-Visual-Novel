package event;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * O lado "observado" do padrão Observer: guarda a lista de observadores e avisa todos a cada evento.
 *
 * - A lista é copy-on-write: um observador pode se remover durante o aviso sem quebrar o laço.
 * - Um observador que lança exceção não impede os demais nem derruba o jogo; o erro vai para stderr.
 */
public class Subject<E> {
    private final List<Observer<E>> observers = new CopyOnWriteArrayList<>();

    public void addObserver(Observer<E> observer) {
        if (observer != null && !observers.contains(observer)) {
            observers.add(observer);
        }
    }

    public void removeObserver(Observer<E> observer) {
        observers.remove(observer);
    }

    public int observerCount() {
        return observers.size();
    }

    public void publish(E event) {
        for (Observer<E> observer : observers) {
            try {
                observer.onEvent(event);
            } catch (RuntimeException e) {
                System.err.println("> Observador falhou ao tratar " + event + ": " + e);
            }
        }
    }
}
