// Encapsulates a borrowing transaction with issue date, due date, and overdue calculation.
import java.time.LocalDate;

public class Loan {
    private final String loanId;
    private final Member member;
    private final LibraryItem item;
    private final LocalDate issueDate;
    private final LocalDate dueDate;
    private boolean returned;

    public Loan(String loanId, Member member, LibraryItem item, LocalDate issueDate) {
        if (loanId == null || loanId.isBlank()) throw new IllegalArgumentException("Loan ID required");
        if (member == null) throw new IllegalArgumentException("Member cannot be null");
        if (item == null) throw new IllegalArgumentException("Item cannot be null");
        if (issueDate == null) throw new IllegalArgumentException("Issue date required");

        this.loanId = loanId.trim();
        this.member = member;
        this.item = item;
        this.issueDate = issueDate;
        this.dueDate = issueDate.plusDays(item.loanPeriodDays());
        this.returned = false;
    }

    // Constructor for reloading existing loans from file
    public Loan(String loanId, Member member, LibraryItem item, LocalDate issueDate, LocalDate dueDate, boolean returned) {
        this.loanId = loanId;
        this.member = member;
        this.item = item;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returned = returned;
    }

    public String getLoanId() { return loanId; }
    public Member getMember() { return member; }
    public LibraryItem getItem() { return item; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getDueDate() { return dueDate; }
    public boolean isReturned() { return returned; }

    public void markReturned() {
        this.returned = true;
    }

    public boolean isOverdue() {
        return !returned && LocalDate.now().isAfter(dueDate);
    }

    public String toCsv() {
        return String.format("%s,%s,%s,%s,%s,%b",
                loanId, member.getMemberId(), item.getId(), issueDate, dueDate, returned);
    }

    @Override
    public String toString() {
        return String.format("Loan[%s]: Item '%s' loaned to %s (Due: %s, Overdue: %s)",
                loanId, item.getTitle(), member.getName(), dueDate, (isOverdue() ? "YES" : "NO"));
    }
}
