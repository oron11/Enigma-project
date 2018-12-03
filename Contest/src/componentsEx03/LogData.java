package componentsEx03;

import componentsEx03.LogCandidates.AliesStatus;

import java.util.List;

public class LogData {

    final private List<SingleChatEntry> entries;
    final private int version;
    private AliesStatus aliesStatus;

    public LogData(List<SingleChatEntry> entries, int version, AliesStatus aliesStatus) {
        this.entries = entries;
        this.version = version;
        this.aliesStatus = aliesStatus;
    }

    public LogData(List<SingleChatEntry> entries, int version) {
        this.entries = entries;
        this.version = version;
    }
}