package enigma.emachine.exceptions;

public class NotAllIdHasFoundException extends RuntimeException {
    private int maxExpectedNumber;
    private String enigmaTool;
    private final String EXCEPTION_MESSAGE = "User didn't defined all id's in %s array starting from 1 to %d correctly.";

    public NotAllIdHasFoundException(String enigmaTool, int maxExpectedNumber ) {
        this.enigmaTool = enigmaTool;
        this.maxExpectedNumber = maxExpectedNumber;
    }

    @Override
    public String getMessage() {
        return String.format(EXCEPTION_MESSAGE, enigmaTool, maxExpectedNumber);
    }
}
