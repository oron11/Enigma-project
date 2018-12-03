package enigma.emachine.exceptions;

public class ItemIsNotFoundInArrayException extends RuntimeException {
    private String itemId;
    private String enigmaTool;
    private final String EXCEPTION_MESSAGE = "Item %s isn't found in %s array.";

    public ItemIsNotFoundInArrayException(String itemId, String enigmaTool) {
        this.itemId = itemId;
        this.enigmaTool = enigmaTool;
    }

    @Override
    public String getMessage() {
        return String.format(EXCEPTION_MESSAGE, itemId, enigmaTool);
    }
}
