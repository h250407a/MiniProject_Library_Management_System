// Models a multimedia DVD item with a short 3-day loan duration.
public class Dvd extends LibraryItem {
    private final String director;
    private final int runtimeMinutes;

    public Dvd(String id, String title, String director, int runtimeMinutes) {
        super(id, title);
        if (director == null || director.isBlank()) throw new IllegalArgumentException("Director cannot be empty");
        if (runtimeMinutes <= 0) throw new IllegalArgumentException("Runtime must be positive");
        this.director = director.trim();
        this.runtimeMinutes = runtimeMinutes;
    }

    @Override
    public int loanPeriodDays() {
        return 3; // Media discs have restricted 3-day loan period
    }

    public String getDirector() { return director; }
    public int getRuntimeMinutes() { return runtimeMinutes; }

    @Override
    public String toCsv() {
        return String.format("DVD,%s,%s,%s,%d,%b", getId(), getTitle(), director, runtimeMinutes, isAvailable());
    }

    @Override
    public String toString() {
        return String.format("[DVD] %s dir. by %s (%d mins) | Status: %s",
                getTitle(), director, runtimeMinutes, (isAvailable() ? "Available" : "Checked Out"));
    }
}
