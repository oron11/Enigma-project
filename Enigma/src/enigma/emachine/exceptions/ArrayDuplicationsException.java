package enigma.emachine.exceptions;

public class ArrayDuplicationsException extends RuntimeException{
    private String arrayName;
    private final String EXCEPTION_MESSAGE = "%s can't include duplications.";

    public ArrayDuplicationsException(String arrayName) {
        this.arrayName = arrayName;
    }

    @Override
    public String getMessage() {
        return String.format(EXCEPTION_MESSAGE, arrayName);
    }
}
