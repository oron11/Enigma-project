package enigma.emachine.exceptions;

public class MinimumDefinedException extends RuntimeException {
    private int min;
    private String enigmaTool;
    private final String EXCEPTION_MESSAGE = "Can't define the enigma machine with less than %d %s.";

    public MinimumDefinedException(int min, String enigmaTool) {
        this.min = min;
        this.enigmaTool = enigmaTool;
    }

    @Override
    public String getMessage() {
        return String.format(EXCEPTION_MESSAGE, min, enigmaTool);
    }
}
