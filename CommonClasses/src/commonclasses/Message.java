package commonclasses;

import java.io.Serializable;

public class Message <T extends Serializable> implements Serializable{
    public enum Function implements Serializable {
        initializeAgent,
        getAgentStatus,
        setAgentToPauseProcess,
        setAgentToContinueProcess,
        addMissionToQueue,
        stopAndKillAgent,
        cancelAgent,
        dmFinishedToDeliverMissionsBatch,
        agentFinishedMissionsBatch
    }

    private Function functionNumToActivate;
    private T param;

    public Message(Function functionNumToActivate) {
        this.functionNumToActivate = functionNumToActivate;
    }

    public Message(Function functionNumToActivate, T param) {
        this.functionNumToActivate = functionNumToActivate;
        this.param = param;
    }

    public void setParam(T param) {
        this.param = param;
    }

    public T getParam() {
        return param;
    }

    public Function getFunctionNumToActivate() { return functionNumToActivate; }
}
