// Custom checked exception thrown when a member exceeds the maximum 3 active loans limit.
public class MemberLimitExceededException extends Exception {
    public MemberLimitExceededException(String message) {
        super(message);
    }
}
