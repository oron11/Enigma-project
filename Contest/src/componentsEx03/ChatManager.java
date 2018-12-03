package componentsEx03;

import java.util.ArrayList;
import java.util.List;

public class ChatManager {
    private final List<SingleChatEntry> chatDataList;
    private final Object chatDataListLock;

    public ChatManager() {
        chatDataList = new ArrayList<>();
        chatDataListLock = new Object();
    }

    public void addChatString(String chatString, String username) {
        synchronized (chatDataListLock) {
            chatDataList.add(new SingleChatEntry(chatString, username));
        }
    }

    public List<SingleChatEntry> getChatEntries(int fromIndex){
        if (fromIndex <= 0) {
            fromIndex = 0;
        }
        else if(fromIndex > chatDataList.size()) {
            fromIndex = chatDataList.size() - 1;
        }

        synchronized (chatDataListLock) {
            return chatDataList.subList(fromIndex, chatDataList.size());
        }
    }

    public int getVersion() {
        return chatDataList.size();
    }

}
