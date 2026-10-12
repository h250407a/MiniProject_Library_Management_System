// Models a physical Book item with a 14-day loan period.
public class Book extends LibraryItem {
    private final String author;
    private final String isbn;

    public Book(String id, String title, String author, String isbn) {
        super(id, title);
        if (author == null || author.isBlank()) throw new IllegalArgumentException("Author cannot be empty");
        if (isbn == null || isbn.isBlank()) throw new IllegalArgumentException("ISBN cannot be empty");
        this.author = author.trim();
        this.isbn = isbn.trim();
    }

    @Override
    public int loanPeriodDays() {
        return 14; // Standard book loan period
    }

    public String getAuthor() { return author; }
    public String getIsbn() { return isbn; }

    @Override
    public String toCsv() {
        return String.format("BOOK,%s,%s,%s,%s,%b", getId(), getTitle(), author, isbn, isAvailable());
    }

    @Override
    public String toString() {
        return String.format("[Book] %s by %s (ISBN: %s) | Status: %s",
                getTitle(), author, isbn, (isAvailable() ? "Available" : "Checked Out"));
    }
}
