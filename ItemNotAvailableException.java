// Custom checked exception thrown when an item is already checked out.
public class ItemNotAvailableException extends Exception {
    public ItemNotAvailableException(String message) {
        super(message);
    }
}
