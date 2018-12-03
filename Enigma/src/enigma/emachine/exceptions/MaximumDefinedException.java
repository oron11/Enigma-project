package enigma.emachine.exceptions;

public class MaximumDefinedException extends RuntimeException {
    private int max;
    private String enigmaTool;
    private final String EXCEPTION_MESSAGE = "Can't define the enigma machine with more than %d %s.";

    public MaximumDefinedException(int max, String enigmaTool) {
        this.max = max;
        this.enigmaTool = enigmaTool;
    }

    @Override
    public String getMessage() {
        return String.format(EXCEPTION_MESSAGE, max, enigmaTool);
    }
}
