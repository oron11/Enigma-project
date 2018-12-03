package componentsEx03;

import commonclasses.StringCandidate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ContestManager {
    private Uboat uboat;
    private Map<String, Alies> mapAlies;
    private List<ParticipantDisplay> allParticipantDisplays;

    private Battlefield battlefield;
    private boolean isActive;
    private final Object mapAliesLock;
    private ChatManager logManager;
    private ChatManager chatManager;
    private boolean isContestExpiredDueAliesLogout;
    private final Object winnerLock;

    private String nicknameWinner;
    private boolean isContestHasAWinner;

    private ContestInfo contestInfo;

    public ContestManager(Battlefield battlefield, Uboat uboat) {
        this.battlefield = battlefield;
        this.uboat = uboat;
        mapAliesLock = new Object();
        winnerLock = new Object();

        contestInfo = new ContestInfo(battlefield.getBattlefieldName(), uboat.getNickname(), battlefield.getLevelDifficultyMission().toString(), battlefield.getRoundsCount(), battlefield.getAliesCount() );
        resetContest();
    }

    private void resetContest() {
        mapAlies = new HashMap<>();
        allParticipantDisplays = new ArrayList<>();
        allParticipantDisplays.add(new ParticipantDisplay(uboat.getNickname(), null));
        contestInfo.reset();
        isContestHasAWinner = false;
        nicknameWinner = null;
        isContestExpiredDueAliesLogout = false;

        isActive = false;
        logManager = new ChatManager();
        chatManager = new ChatManager();
        logManager.addChatString(String.format("%s has opened a new contest: %s.", uboat.getNickname(), battlefield.getBattlefieldName()), uboat.getNickname());
    }

    public Uboat getUboat() {
        return uboat;
    }

    public Battlefield getBattlefield() {
        return battlefield;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean addAliesToTheContest(Alies alies) {
        synchronized (mapAliesLock) {
            if (!isActive && mapAlies.size() < battlefield.getAliesCount()) {
                alies.setContestManager(this);
                mapAlies.put(alies.getNickname(), alies);
                allParticipantDisplays.add(alies.getParticipantDisplay());

                alies.initializeDecryption(uboat, battlefield.getLevelDifficultyMission());
                contestInfo.setCurrentAliesNumber(mapAlies.size());
                logManager.addChatString(String.format("%s has joined to the contest.", alies.getNickname()), alies.getNickname());
                return true;
            }

            return false;
        }
    }

    private void removeAliesFromTheContest(Alies alies) {
        synchronized (mapAliesLock) {
            mapAlies.remove(alies.getNickname());
            makeNewParticipantsDisplayListWithout(alies.getNickname());
            alies.stopProcess();
            contestInfo.setCurrentAliesNumber(mapAlies.size());
        }
    }

    private void makeNewParticipantsDisplayListWithout(String nickname) {
        List<ParticipantDisplay> newList = new ArrayList<>();
        ParticipantDisplay participantDisplayToRemove = null;

        for(ParticipantDisplay participantDisplay : allParticipantDisplays) {
            if(participantDisplay.getNickname().equals(nickname)) {
                participantDisplayToRemove = participantDisplay;
                break;
            }
        }

        for(ParticipantDisplay participantDisplay1 : allParticipantDisplays) {
            if(!participantDisplay1.equals(participantDisplayToRemove)) {
                newList.add(participantDisplay1);
            }
        }

        allParticipantDisplays = newList;
    }

    private List<ParticipantDisplay> getParticipantsList() {
        synchronized (mapAliesLock) {
            return allParticipantDisplays;
        }
    }

    public ContestInfo getContestInfo() {
        return contestInfo;
    }

    public void uploadMessageChatToServer(String nickname, String userMessageToUpload) {
        chatManager.addChatString(userMessageToUpload, nickname);
    }

    private boolean checkIfAliesInTheContest(Alies alies) {
        synchronized (mapAliesLock) {
            return mapAlies.containsKey(alies.getNickname());
        }
    }

    private void checkIfAllPlayersAreReadyAndStartContest() {
        if(!isActive) {
            synchronized (this) {
                if (!isActive) {
                    if (checkIfAllPlayersAreReady()) {
                        isActive = true;
                        contestInfo.changeContestInfoToActive();
                        logManager.addChatString("all players are ready.", uboat.getNickname());
                        logManager.addChatString("The contest has started!", uboat.getNickname());
                        startProcess();
                    }
                }
            }
        }
    }

    private boolean checkIfAllPlayersAreReady() {
        int counterParticipantsReady = 0;
        for(ParticipantDisplay participantDisplay : allParticipantDisplays) {
            if(participantDisplay.isReady()) {
                counterParticipantsReady++;
            }
        }

        return counterParticipantsReady == battlefield.getAliesCount() + 1;
    }

    private void startProcess() {
        String messageAfterEncode = uboat.getMessageAfterEncode();
        mapAlies.forEach((nickname, alies) -> alies.startProcess(messageAfterEncode, logManager, uboat.getStartSecretCode()));
        uboat.startProcess(logManager);
    }

    public void setAliesParameters(Alies alies, int missionSize, int agentsNumberCounter) throws Exception{
        if (!checkIfAliesInTheContest(alies)) {
            throw new Exception("Error: Could not find your alies team in the contest: " + battlefield.getBattlefieldName() + ".");
        }

        alies.setParameters(missionSize, agentsNumberCounter);
        logManager.addChatString(String.format("%s is ready to start.", alies.getNickname()), alies.getNickname());
        checkIfAllPlayersAreReadyAndStartContest();
    }

    public void encodeMessage(String messageToEncode) {
        uboat.encodeMessage(messageToEncode);
        logManager.addChatString(String.format("%s has encoded message.", uboat.getNickname()), uboat.getNickname());
        allParticipantDisplays.get(0).setReady();
        logManager.addChatString(String.format("%s is ready to start.", uboat.getNickname()), uboat.getNickname());
        checkIfAllPlayersAreReadyAndStartContest();
    }

    public void addSuccessSecretCodeLogMessage() {
        logManager.addChatString(String.format("%s has set the secret code for the enigma machine.", uboat.getNickname()), uboat.getNickname());
    }

    public boolean logoutUserAndReturnIfNeedToCloseContest(String nickname) {
        if(nickname.equals(uboat.getNickname())) {
            finishContest();
            sendLogMessageInAnyStatusOfContest(String.format("The uboat: %s logged out, the contest is finished.", uboat.getNickname()));
            return true;
        }
        else {
            Alies alies = getAlies(nickname);
            if(alies == null) {
                return false;
            }
            else {
                removeAliesFromTheContest(alies);
                alies.killDm();
                sendLogMessageInAnyStatusOfContest(String.format("The alies: %s has left the contest.", alies.getNickname()));

                if(isActive && mapAlies.size() == 0) {
                    isContestExpiredDueAliesLogout = true;
                }

               return false;
            }
        }
    }

    private void sendLogMessageInAnyStatusOfContest(String logMessage) {
        if(isActive) {
            sendLogMessageIfContestActive(logMessage);
        }
        else {
            logManager.addChatString(logMessage, uboat.getNickname());
        }
    }

    private Alies getAlies(String nickname) {
        synchronized (mapAliesLock) {
            return mapAlies.get(nickname);
        }
    }

    public LogData chatRefresh(int chatVersion) {
        return new LogData(chatManager.getChatEntries(chatVersion), chatManager.getVersion());
    }

    public LogData logRefresh(String nickname, int chatVersion) {
        if(!isActive) {
            return new LogData(logManager.getChatEntries(chatVersion), logManager.getVersion());
        }
        else {
            if(nickname.equals(uboat.getNickname())) {
                return uboat.getLogData(chatVersion);
            }
            else {
                Alies alies = getAlies(nickname);
                if(alies == null) {
                    return null;
                }
                else {
                    return alies.getLogData(chatVersion);
                }
            }
        }
    }

    public void addStringCandidateToLogUboatAndCheckWin(String nickname, StringCandidate stringCandidate, String messageLog) {
        synchronized (winnerLock) {
            if (!isContestHasAWinner && uboat.addStringCandidateToLogUboatAndCheckWin(stringCandidate, messageLog)) {
                finishContest();
                nicknameWinner = nickname;
                String winningMessage = String.format("%s has won the contest!", nicknameWinner);
                sendLogMessageIfContestActive(winningMessage);
                isContestHasAWinner = true;
            }
        }
    }

    private void sendLogMessageIfContestActive(String logMessage) {
        uboat.addLogMessage(logMessage);
        mapAlies.forEach((nick, alies) -> alies.addLogMessage(logMessage));
    }

    private void finishContest() {
        mapAlies.forEach((s, alies) -> alies.stopProcess());
    }

    public RefreshAnswer getRefreshAnswer(String nickname) {
        RefreshAnswer refreshAnswer = new RefreshAnswer(getParticipantsList() , isActive, isContestHasAWinner, nicknameWinner);

        if(isContestHasAWinner) {
            makeNewParticipantsDisplayListWithout(nickname);
            if(allParticipantDisplays.size() == 0 && nickname.equals(uboat.getNickname()) ) {
                resetContest();
            }
        }
        else if(isContestExpiredDueAliesLogout && nickname.equals(uboat.getNickname())) {
            //Only uboat reset here
            resetContest();
        }

        return refreshAnswer;
    }
}
