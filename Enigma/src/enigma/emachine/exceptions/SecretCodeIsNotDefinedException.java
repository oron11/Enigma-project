package enigma.emachine.exceptions;

public class SecretCodeIsNotDefinedException extends RuntimeException{
    private final String EXCEPTION_MESSAGE = "Enigma machine can't execute the command: %s. Because secret code isn't defined.";
    private String commandDescription;

    public SecretCodeIsNotDefinedException(String commandDescription) {
        this.commandDescription = commandDescription;
    }

    @Override
    public String getMessage() {
        return String.format(EXCEPTION_MESSAGE, commandDescription);
    }
}
