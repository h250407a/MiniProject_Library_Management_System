// Core service coordinating items, members, loans, CSV storage, and stream queries.
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

public class Library {
    private final Map<String, LibraryItem> items = new HashMap<>();
    private final Map<String, Member> members = new HashMap<>();
    private final List<Loan> loans = new ArrayList<>();
    private int loanCounter = 1000;

    private final Path itemsFile = Path.of("items.csv");
    private final Path membersFile = Path.of("members.csv");
    private final Path loansFile = Path.of("loans.csv");

    public Library() {
        loadData();
    }

    public void addItem(LibraryItem item) {
        if (items.containsKey(item.getId())) {
            throw new IllegalArgumentException("Item with ID " + item.getId() + " already exists.");
        }
        items.put(item.getId(), item);
    }

    public void registerMember(Member member) {
        if (members.containsKey(member.getMemberId())) {
            throw new IllegalArgumentException("Member with ID " + member.getMemberId() + " already registered.");
        }
        members.put(member.getMemberId(), member);
    }

    public Loan borrow(String memberId, String itemId)
            throws ItemNotAvailableException, MemberLimitExceededException {
        // 1. Look up member and item
        Member member = members.get(memberId);
        if (member == null) {
            throw new IllegalArgumentException("Member not found with ID: " + memberId);
        }

        LibraryItem item = items.get(itemId);
        if (item == null) {
            throw new IllegalArgumentException("Library item not found with ID: " + itemId);
        }

        // 2. Check item availability
        if (!item.isAvailable()) {
            throw new ItemNotAvailableException("Item '" + item.getTitle() + "' is currently checked out.");
        }

        // 3. Check member borrowing limit (maximum 3 loans)
        if (!member.canBorrow()) {
            throw new MemberLimitExceededException("Member '" + member.getName() + "' has reached the maximum 3 loans limit.");
        }

        // 4. Create Loan record, mark item unavailable, and update member
        String loanId = "LN-" + (++loanCounter);
        Loan loan = new Loan(loanId, member, item, LocalDate.now());
        item.setAvailable(false);
        member.addLoan(loan);
        loans.add(loan);

        return loan;
    }

    public void returnItem(String memberId, String itemId) {
        Member member = members.get(memberId);
        if (member == null) {
            throw new IllegalArgumentException("Member not found with ID: " + memberId);
        }

        LibraryItem item = items.get(itemId);
        if (item == null) {
            throw new IllegalArgumentException("Library item not found with ID: " + itemId);
        }

        // Find active loan matching this member and item
        Loan activeLoan = member.getActiveLoans().stream()
            .filter(l -> l.getItem().getId().equals(itemId) && !l.isReturned())
            .findFirst()
            .orElseThrow(() -> new IllegalArgumentException("No active loan found for member " + memberId + " with item " + itemId));

        activeLoan.markReturned();
        item.setAvailable(true);
        member.removeLoan(activeLoan);
    }

    // Stream query: Search items by title substring (case-insensitive, sorted by title)
    public List<LibraryItem> searchByTitle(String text) {
        return items.values().stream()
            .filter(i -> i.getTitle().toLowerCase().contains(text.toLowerCase()))
            .sorted(Comparator.comparing(LibraryItem::getTitle))
            .collect(Collectors.toList());
    }

    // Stream query: Filter active overdue loans
    public List<Loan> listOverdueItems() {
        return loans.stream()
            .filter(Loan::isOverdue)
            .collect(Collectors.toList());
    }

    public Collection<LibraryItem> getAllItems() {
        return Collections.unmodifiableCollection(items.values());
    }

    public Collection<Member> getAllMembers() {
        return Collections.unmodifiableCollection(members.values());
    }

    public List<Loan> getAllLoans() {
        return Collections.unmodifiableList(loans);
    }

    // Persist all data to CSV files
    public void saveData() {
        try {
            // 1. Save Items
            List<String> itemLines = items.values().stream().map(LibraryItem::toCsv).collect(Collectors.toList());
            Files.write(itemsFile, itemLines);

            // 2. Save Members
            List<String> memberLines = members.values().stream().map(Member::toCsv).collect(Collectors.toList());
            Files.write(membersFile, memberLines);

            // 3. Save Loans
            List<String> loanLines = loans.stream().map(Loan::toCsv).collect(Collectors.toList());
            Files.write(loansFile, loanLines);

            System.out.println("System state saved successfully to CSV files.");
        } catch (IOException e) {
            System.err.println("Error saving library state: " + e.getMessage());
        }
    }

    // Reload persisted state from CSV files
    public void loadData() {
        try {
            // Load Items
            if (Files.exists(itemsFile)) {
                for (String line : Files.readAllLines(itemsFile)) {
                    if (line.isBlank()) continue;
                    String[] parts = line.split(",");
                    String type = parts[0];
                    String id = parts[1];
                    String title = parts[2];
                    if (type.equals("BOOK")) {
                        Book b = new Book(id, title, parts[3], parts[4]);
                        b.setAvailable(Boolean.parseBoolean(parts[5]));
                        items.put(id, b);
                    } else if (type.equals("DVD")) {
                        Dvd d = new Dvd(id, title, parts[3], Integer.parseInt(parts[4]));
                        d.setAvailable(Boolean.parseBoolean(parts[5]));
                        items.put(id, d);
                    }
                }
            }

            // Load Members
            if (Files.exists(membersFile)) {
                for (String line : Files.readAllLines(membersFile)) {
                    if (line.isBlank()) continue;
                    String[] parts = line.split(",");
                    Member m = new Member(parts[0], parts[1], parts[2]);
                    members.put(m.getMemberId(), m);
                }
            }

            // Load Loans
            if (Files.exists(loansFile)) {
                for (String line : Files.readAllLines(loansFile)) {
                    if (line.isBlank()) continue;
                    String[] parts = line.split(",");
                    String loanId = parts[0];
                    String memId = parts[1];
                    String itemId = parts[2];
                    LocalDate issue = LocalDate.parse(parts[3]);
                    LocalDate due = LocalDate.parse(parts[4]);
                    boolean returned = Boolean.parseBoolean(parts[5]);

                    Member member = members.get(memId);
                    LibraryItem item = items.get(itemId);

                    if (member != null && item != null) {
                        Loan loan = new Loan(loanId, member, item, issue, due, returned);
                        loans.add(loan);
                        if (!returned) {
                            try {
                                member.addLoan(loan);
                            } catch (MemberLimitExceededException ignored) {}
                        }
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Failed to load existing library state: " + e.getMessage());
        }
    }
}
