package agent;

public class DecryptionManagerIsFullException extends Exception {
    private final String message;

    public DecryptionManagerIsFullException(String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
