package Exception;

public class SaveSlotNotFoundException extends GamePersistenceException {
    private final int slot;

    public SaveSlotNotFoundException(int slot) {
        super("Nenhum arquivo de save encontrado no Slot " + (slot == 0 ? "Auto-Save" : slot) + ".");
        this.slot = slot;
    }

    public int getSlot() {
        return slot;
    }
}