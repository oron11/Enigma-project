package componentsEx03;

public class ContestInfo {
    private ContestStatus status;
    private String gameName;
    private String ownerName;
    private String levelHardness;
    private int roundsNumber;
    private int aliesRequiredNumber;
    private int currentAliesNumber;

    public enum ContestStatus {
         Pending, Active
    }
    public void setCurrentAliesNumber(int currentAliesNumber) {
        this.currentAliesNumber = currentAliesNumber;
    }

    public ContestInfo(String gameName, String ownerName, String levelHardness, int roundsNumber, int aliesRequiredNumber) {
        this.gameName = gameName;
        this.ownerName = ownerName;

        this.levelHardness = levelHardness;
        this.roundsNumber = roundsNumber;
        this.aliesRequiredNumber = aliesRequiredNumber;
        currentAliesNumber = 0;
    }

    public void reset() {
        currentAliesNumber = 0;
        status = ContestStatus.Pending;
    }

    public void incCurrentAliesNumber() {
        currentAliesNumber++;
    }

    public void decCurrentAliesNumber() {
        currentAliesNumber--;
    }

    public void changeContestInfoToActive() {
        status = ContestStatus.Active;
    }
}
