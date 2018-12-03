package enigma.emachine.exceptions;

public class BattlefieldMinimumException extends RuntimeException {
    private int min;
    private int minFound;
    private String component;
    private final String EXCEPTION_MESSAGE = "Can't define the Battlefield with less than %d %s, Found in file: %d.";

    public BattlefieldMinimumException(int min, String component, int minFound) {
        this.min = min;
        this.component = component;
        this.minFound = minFound;
    }

    @Override
    public String getMessage() {
        return String.format(EXCEPTION_MESSAGE, min, component, minFound);
    }
}
