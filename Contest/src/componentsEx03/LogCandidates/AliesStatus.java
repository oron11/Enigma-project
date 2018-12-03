package componentsEx03.LogCandidates;

public class AliesStatus {
    private AgentDisplay[] agentDisplayArray;
    private int progressPercentage;

    public AliesStatus(AgentDisplay[] allAgentsDisplay) {
        this.agentDisplayArray = allAgentsDisplay;
    }

    public void setProgressPercentage(int progressPercentage) {
        this.progressPercentage = progressPercentage;
    }

    public AgentDisplay[] getAgentDisplayArray() {
        return agentDisplayArray;
    }

    public int getProgressPercentage() {
        return progressPercentage;
    }


}
