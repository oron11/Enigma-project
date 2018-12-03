package componentsEx03;

import componentsEx03.ParticipantDisplay;

import java.util.List;

public class RefreshAnswer {
    private List<ParticipantDisplay> participantsList;
    private boolean isContestStarted;

    private boolean isContestHasAWinner;
    private String contestWinnerNickname;

    public RefreshAnswer(List<ParticipantDisplay> participantsList, boolean isContestStarted, boolean isContestHasAWinner, String contestWinnerNickname) {
        this.participantsList = participantsList;
        this.isContestStarted = isContestStarted;
        this.isContestHasAWinner = isContestHasAWinner;
        this.contestWinnerNickname = contestWinnerNickname;
    }

    public RefreshAnswer(List<ParticipantDisplay> participantsList, boolean isContestStarted) {
        this.participantsList = participantsList;
        this.isContestStarted = isContestStarted;
    }
}