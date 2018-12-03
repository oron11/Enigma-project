package enigma.emachine.exceptions;

public class IdAlreadyExistsException extends RuntimeException {
    private int id;
    private String enigmaTool;
    private final String EXCEPTION_MESSAGE = "There is duplication in id: %d in %s's array, Can't define same %s.";

    public IdAlreadyExistsException(int id, String enigmaTool) {
        this.id = id;
        this.enigmaTool = enigmaTool;
    }

    @Override
    public String getMessage() {
        return String.format(EXCEPTION_MESSAGE, id, enigmaTool, enigmaTool);
    }
}


