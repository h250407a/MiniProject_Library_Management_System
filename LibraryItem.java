// Abstract base class for all catalog items in the library.
public abstract class LibraryItem {
    private final String id;
    private final String title;
    private boolean available = true;

    protected LibraryItem(String id, String title) {
        if (id == null || id.isBlank()) throw new IllegalArgumentException("Item ID cannot be empty");
        if (title == null || title.isBlank()) throw new IllegalArgumentException("Title cannot be empty");
        this.id = id.trim();
        this.title = title.trim();
    }

    // Abstract method: each media type specifies its loan duration in days
    public abstract int loanPeriodDays();

    // Abstract method: formats item attributes to CSV line
    public abstract String toCsv();

    public String getId() { return id; }
    public String getTitle() { return title; }
    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    @Override
    public String toString() {
        return String.format("[%s] %s | Available: %s | Loan Period: %d days",
                id, title, (available ? "YES" : "NO"), loanPeriodDays());
    }
}
