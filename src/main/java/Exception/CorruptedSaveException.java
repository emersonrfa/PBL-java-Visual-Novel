package Exception;

public class CorruptedSaveException extends GamePersistenceException {
    public CorruptedSaveException(String message) {
        super(message);
    }

    public CorruptedSaveException(String message, Throwable cause) {
        super(message, cause);
    }
}