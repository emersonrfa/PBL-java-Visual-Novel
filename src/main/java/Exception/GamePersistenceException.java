package Exception;

public class GamePersistenceException extends Exception {
    public GamePersistenceException(String message) {
        super(message);
    }

    public GamePersistenceException(String message, Throwable cause) {
        super(message, cause); // Preserva a causa técnica conforme exigido na rubrica
    }
}
