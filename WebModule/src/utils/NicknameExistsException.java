package utils;

public class NicknameExistsException extends Exception {
    private final String EXCEPTION_MESSAGE = "Nickname %s already exists on the server.";
    private final String nickname;

    public NicknameExistsException(String nickname) {
        this.nickname = nickname;
    }

    @Override
    public String getMessage() {
        return String.format(EXCEPTION_MESSAGE, nickname);
    }
}
