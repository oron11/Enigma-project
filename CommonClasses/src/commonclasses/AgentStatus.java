package commonclasses;

import java.io.Serializable;
import java.util.List;

public class AgentStatus implements Serializable{
    private StringCandidate[] stringCandidateArray;
    private long counterMissionsDone;
    private long counterMissionInQueue;
    private int agentId;

    public StringCandidate[] getStringCandidateArray() {
        return stringCandidateArray;
    }

    public long getCounterMissionsDone() {
        return counterMissionsDone;
    }

    public long getCounterMissionsInQueue() {
        return counterMissionInQueue;
    }

    public int getAgentId() {
        return agentId;
    }

    public void setStringCandidateList(List<StringCandidate> stringCandidateList) {
        this.stringCandidateArray = stringCandidateList.toArray(new StringCandidate[stringCandidateList.size()]);
    }

    public void setCounterMissionsDone(long counterMissionsDone) {
        this.counterMissionsDone = counterMissionsDone;
    }

    public void setCounterMissionInQueue(long counterMissionInQueue) {
        this.counterMissionInQueue = counterMissionInQueue;
    }

    public void setAgentId(int agentId) {
        this.agentId = agentId;
    }
}
