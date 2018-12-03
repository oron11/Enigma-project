package componentsEx03.LogCandidates;

import componentsEx03.ChatManager;
import componentsEx03.SingleChatEntry;

import java.util.ArrayList;
import java.util.List;

public class LogCandidatesManager {
    private final List<SingleChatEntry> chatDataList;
    private final Object chatDataListLock;

    public LogCandidatesManager(ChatManager logManager) {
        List<SingleChatEntry> entries = logManager.getChatEntries(0);
        chatDataList = new ArrayList<SingleChatEntry>(entries.size());
        chatDataListLock = new Object();

        for (SingleChatEntry singleChatEntry : entries) {
            chatDataList.add(singleChatEntry.clone());
        }
    }

    public void addChatString(String chatString) {
        synchronized (chatDataListLock) {
            chatDataList.add(new SingleChatEntry(chatString));
        }
    }

    public List<SingleChatEntry> getChatEntries(int fromIndex){
        if (fromIndex < 0 || fromIndex >= chatDataList.size()) {
            fromIndex = 0;
        }

        synchronized (chatDataListLock) {
            return chatDataList.subList(fromIndex, chatDataList.size());
        }
    }

    public int getVersion() {
        return chatDataList.size();
    }
}
