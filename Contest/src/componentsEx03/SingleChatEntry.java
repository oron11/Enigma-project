package componentsEx03;

import java.text.SimpleDateFormat;
import java.util.Calendar;

public class SingleChatEntry {
    private final String chatString;
    private final String username;
    private final String time;

    public SingleChatEntry(String chatString, String username) {
        this.chatString = chatString;
        this.username = username;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        this.time = sdf.format(Calendar.getInstance().getTime());
    }

    public SingleChatEntry(String chatString) {
        this.chatString = chatString;
        username = null;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        this.time = sdf.format(Calendar.getInstance().getTime());
    }

    public String getChatString() {
        return chatString;
    }

    public String getTime() {
        return time;
    }

    public String getUsername() {
        return username;
    }

    @Override
    public String toString() {
        return (username != null ? username + ": " : "") + chatString;
    }

    public SingleChatEntry clone() {
        return new SingleChatEntry(chatString, username);
    }
}
