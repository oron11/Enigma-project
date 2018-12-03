package decryption;

public class ParticipatedAgent {

    private int agentId;
    private long missionNumberOfAgent;

    public ParticipatedAgent(int agentId, long missionNumberOfAgent) {
        this.agentId = agentId;
        this.missionNumberOfAgent = missionNumberOfAgent;
    }

    public int getAgentId() {
        return agentId;
    }

    public long getMissionNumberOfAgent() {
        return missionNumberOfAgent;
    }
}
