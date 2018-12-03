package commonclasses;

import java.io.Serializable;

public class StringCandidate implements Serializable {
    private String codeFoundedAt;
    private String stringCandidate;
    private int agentId;

    public StringCandidate(String codeFoundedAt, String stringCandidate, int agentId) {
        this.codeFoundedAt = codeFoundedAt;
        this.stringCandidate = stringCandidate;
        this.agentId = agentId;
    }

    public String getCodeFoundedAt() {
        return codeFoundedAt;
    }

    public String getStringCandidate() {
        return stringCandidate;
    }

    public int getAgentId() {
        return agentId;
    }
}
