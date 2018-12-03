package enigma.emachine.exceptions;

public class IllegalFormatSecretCodeException extends RuntimeException {
    private final String EXCEPTION_MESSAGE = "User input number of rotors: %d isn't matched to number of rotors defined in the enigma machine: %d";
    int userNumberRotors;
    int machineNumberRotors;

    public IllegalFormatSecretCodeException(int userNumberRotors, int machineNumberRotors) {
        this.userNumberRotors = userNumberRotors;
        this.machineNumberRotors = machineNumberRotors;
    }

    @Override
    public String getMessage() {
        return String.format(EXCEPTION_MESSAGE,userNumberRotors, machineNumberRotors);
    }
}
