package componentsEx03;

import decryption.ConnectedAgentsNumber;

public class ParticipantDisplay {
    private String nickname;
    private ConnectedAgentsNumber connectedAgentsNumber;
    private boolean isReady;

    public ParticipantDisplay(String nickname, ConnectedAgentsNumber connectedAgentsNumber) {
        this.nickname = nickname;
        this.connectedAgentsNumber = connectedAgentsNumber;
        isReady = false;
    }

    public void setReady() {
        isReady = true;
    }

    public String getNickname() {
        return nickname;
    }

    public void reset() {
        connectedAgentsNumber.setConnectedAgentsNumber(0);
        isReady = false;
    }

    public boolean isReady() {
        return isReady;
    }
}
