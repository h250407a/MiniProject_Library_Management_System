// Models a library Member with loan capacity tracking enforcing a 3-item ceiling.
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Member {
    public static final int MAX_LOANS = 3;
    private final String memberId;
    private String name;
    private String email;
    private final List<Loan> activeLoans = new ArrayList<>();

    public Member(String memberId, String name, String email) {
        if (memberId == null || memberId.isBlank()) throw new IllegalArgumentException("Member ID required");
        if (name == null || name.isBlank()) throw new IllegalArgumentException("Member name required");
        if (email == null || !email.contains("@")) throw new IllegalArgumentException("Valid email required");

        this.memberId = memberId.trim();
        this.name = name.trim();
        this.email = email.trim();
    }

    public String getMemberId() { return memberId; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    public boolean canBorrow() {
        return activeLoans.size() < MAX_LOANS;
    }

    public void addLoan(Loan loan) throws MemberLimitExceededException {
        if (!canBorrow()) {
            throw new MemberLimitExceededException(
                "Member " + name + " has reached the maximum borrowing limit of " + MAX_LOANS + " items."
            );
        }
        activeLoans.add(loan);
    }

    public void removeLoan(Loan loan) {
        activeLoans.remove(loan);
    }

    public List<Loan> getActiveLoans() {
        return Collections.unmodifiableList(activeLoans);
    }

    public String toCsv() {
        return String.format("%s,%s,%s", memberId, name, email);
    }

    @Override
    public String toString() {
        return String.format("Member[%s]: %s (%s) | Active Loans: %d/%d",
                memberId, name, email, activeLoans.size(), MAX_LOANS);
    }
}
