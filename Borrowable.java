// Interface defining borrowing and return contracts for library entities.
public interface Borrowable {
    void borrow(Member member) throws ItemNotAvailableException, MemberLimitExceededException;
    void giveBack();
}
