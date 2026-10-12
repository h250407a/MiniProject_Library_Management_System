// Main interactive application providing menu options for the Library Management System.
import java.util.List;
import java.util.Scanner;

public class LibraryApp {
    private static final Scanner scanner = new Scanner(System.in);
    private static final Library library = new Library();

    public static void main(String[] args) {
        // Pre-populate with sample catalog if starting fresh
        if (library.getAllItems().isEmpty()) {
            seedSampleData();
        }

        System.out.println("=====================================================");
        System.out.println("  WELCOME TO THE UNIVERSITY LIBRARY MANAGEMENT SYSTEM ");
        System.out.println("  Student: Anesu B Kahanda | Reg Number: H250407A     ");
        System.out.println("=====================================================");

        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("Select an option (1-8): ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1" -> handleAddItem();
                case "2" -> handleRegisterMember();
                case "3" -> handleBorrow();
                case "4" -> handleReturn();
                case "5" -> handleListOverdue();
                case "6" -> handleSearch();
                case "7" -> handleDisplayAll();
                case "8" -> {
                    System.out.println("\nSaving library data and exiting...");
                    library.saveData();
                    System.out.println("Goodbye!");
                    running = false;
                }
                default -> System.out.println("Invalid selection. Please choose an option between 1 and 8.");
            }
            System.out.println();
        }
    }

    private static void printMenu() {
        System.out.println("----------------- MAIN MENU -----------------");
        System.out.println("1. Add Library Item (Book or DVD)");
        System.out.println("2. Register Library Member");
        System.out.println("3. Borrow Item");
        System.out.println("4. Return Item");
        System.out.println("5. List Overdue Items (Streams)");
        System.out.println("6. Search Items by Title (Streams)");
        System.out.println("7. Display All Items and Members");
        System.out.println("8. Save and Exit");
        System.out.println("---------------------------------------------");
    }

    private static void handleAddItem() {
        System.out.print("Choose item type (1 for Book, 2 for DVD): ");
        String type = scanner.nextLine().trim();
        System.out.print("Enter Item ID (e.g. B-105 or D-201): ");
        String id = scanner.nextLine().trim();
        System.out.print("Enter Title: ");
        String title = scanner.nextLine().trim();

        try {
            if ("1".equals(type)) {
                System.out.print("Enter Author: ");
                String author = scanner.nextLine().trim();
                System.out.print("Enter ISBN: ");
                String isbn = scanner.nextLine().trim();
                library.addItem(new Book(id, title, author, isbn));
                System.out.println("Book successfully added to library catalog.");
            } else if ("2".equals(type)) {
                System.out.print("Enter Director: ");
                String director = scanner.nextLine().trim();
                System.out.print("Enter Runtime (minutes): ");
                int runtime = Integer.parseInt(scanner.nextLine().trim());
                library.addItem(new Dvd(id, title, director, runtime));
                System.out.println("DVD successfully added to library catalog.");
            } else {
                System.out.println("Invalid item type selected.");
            }
        } catch (Exception e) {
            System.out.println("Error adding item: " + e.getMessage());
        }
    }

    private static void handleRegisterMember() {
        System.out.print("Enter Member ID (e.g. M-01): ");
        String id = scanner.nextLine().trim();
        System.out.print("Enter Full Name: ");
        String name = scanner.nextLine().trim();
        System.out.print("Enter Email Address: ");
        String email = scanner.nextLine().trim();

        try {
            library.registerMember(new Member(id, name, email));
            System.out.println("Member registered successfully.");
        } catch (Exception e) {
            System.out.println("Error registering member: " + e.getMessage());
        }
    }

    private static void handleBorrow() {
        System.out.print("Enter Member ID: ");
        String memberId = scanner.nextLine().trim();
        System.out.print("Enter Item ID to borrow: ");
        String itemId = scanner.nextLine().trim();

        try {
            Loan loan = library.borrow(memberId, itemId);
            System.out.println("Borrowing successful! " + loan);
        } catch (ItemNotAvailableException | MemberLimitExceededException e) {
            System.out.println("Borrowing Failed: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Operation Error: " + e.getMessage());
        }
    }

    private static void handleReturn() {
        System.out.print("Enter Member ID: ");
        String memberId = scanner.nextLine().trim();
        System.out.print("Enter Item ID to return: ");
        String itemId = scanner.nextLine().trim();

        try {
            library.returnItem(memberId, itemId);
            System.out.println("Item successfully returned and returned to available inventory.");
        } catch (Exception e) {
            System.out.println("Return failed: " + e.getMessage());
        }
    }

    private static void handleListOverdue() {
        System.out.println("--- Overdue Items ---");
        List<Loan> overdueList = library.listOverdueItems();
        if (overdueList.isEmpty()) {
            System.out.println("No overdue loans at this time.");
        } else {
            overdueList.forEach(System.out::println);
        }
    }

    private static void handleSearch() {
        System.out.print("Enter search keyword for item title: ");
        String query = scanner.nextLine().trim();
        List<LibraryItem> matches = library.searchByTitle(query);

        System.out.println("\n--- Search Results (" + matches.size() + " matches) ---");
        if (matches.isEmpty()) {
            System.out.println("No items match the title query: \"" + query + "\"");
        } else {
            matches.forEach(item -> System.out.println("- " + item));
        }
    }

    private static void handleDisplayAll() {
        System.out.println("\n--- All Catalog Items (" + library.getAllItems().size() + ") ---");
        library.getAllItems().forEach(System.out::println);

        System.out.println("\n--- Registered Members (" + library.getAllMembers().size() + ") ---");
        library.getAllMembers().forEach(System.out::println);
    }

    private static void seedSampleData() {
        library.addItem(new Book("B-101", "Introduction to Algorithms", "CLRS", "978-0262033848"));
        library.addItem(new Book("B-102", "Effective Java", "Joshua Bloch", "978-0134685991"));
        library.addItem(new Book("B-103", "Design Patterns", "Gang of Four", "978-0201633610"));
        library.addItem(new Dvd("D-201", "The Social Network", "David Fincher", 120));
        library.addItem(new Dvd("D-202", "The Matrix", "Wachowskis", 136));

        library.registerMember(new Member("M-01", "Anesu B Kahanda", "anesu.kahanda@hit.ac.zw"));
        library.registerMember(new Member("M-02", "Tendai Sithole", "tendai.sithole@hit.ac.zw"));
        library.registerMember(new Member("M-03", "Nyasha Moyo", "nyasha.moyo@hit.ac.zw"));
    }
}
