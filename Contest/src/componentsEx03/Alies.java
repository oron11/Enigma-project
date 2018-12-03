package componentsEx03;

import commonclasses.AgentStatus;
import commonclasses.StringCandidate;
import componentsEx03.LogCandidates.AgentDisplay;
import componentsEx03.LogCandidates.AliesStatus;
import componentsEx03.LogCandidates.LogCandidatesManager;
import decryption.ConnectedAgentsNumber;
import decryption.DecryptionManager;
import enigma.emachine.Secret;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Alies {
    private DecryptionManager decryptionManager;
    private String nickname;

    private ParticipantDisplay participantDisplay;
    private ConnectedAgentsNumber connectedAgentsNumber;

    //private long maxMissionSize;
    private long possibilitiesNumber;
    private int currentMissionSize;
    private ContestManager contestManager;

    //For status update
    private boolean isNeedToFinishUpdateAliesStatusThread;
    private Thread updateAliesStatusThread;
    private LogCandidatesManager logCandidatesManager;
    private AliesStatus aliesStatus;
    private int currentAgentsNumber;
    private AgentDisplay[] allAgentsDisplay;
    //

    public Alies(String nickname) {
        this.nickname = nickname;
        this.connectedAgentsNumber = new ConnectedAgentsNumber();
        this.participantDisplay = new ParticipantDisplay(nickname, connectedAgentsNumber);
        decryptionManager = new DecryptionManager(connectedAgentsNumber);
    }

    public void setContestManager(ContestManager contestManager) {
        this.contestManager = contestManager;
    }

    public String getNickname() {
        return nickname;
    }

    public ParticipantDisplay getParticipantDisplay() {
        return participantDisplay;
    }

    public void stopProcess() {
        decryptionManager.presentFinalDataOfDecryptionProcess();
        isNeedToFinishUpdateAliesStatusThread = true;
        participantDisplay.reset();
        if(updateAliesStatusThread != null){
            updateAliesStatusThread.interrupt();
        }
    }

    public void initializeDecryption(Uboat uboat, DecryptionManager.LevelDifficultyMission levelDifficultyMission) {
        decryptionManager.setTrie(uboat.getDictionaryTrie());
        decryptionManager.setEnigmaMachine(uboat.getEnigmaMachine().clone());
        decryptionManager.setLevelDifficultyMission(levelDifficultyMission);
        //maxMissionSize = decryptionManager.getMaxMissionSize(4); //Need to change
        possibilitiesNumber = decryptionManager.getPossibilitiesNumberAccordingToLevel(levelDifficultyMission);
        new Thread(()-> decryptionManager.startDecryptionMission()).start();
    }

    public void startProcess(String userMessage, ChatManager logManager, Secret secretCode) {
        decryptionManager.flagToStartProcess(userMessage, currentMissionSize, secretCode);
        this.logCandidatesManager = new LogCandidatesManager(logManager);
        startUpdateAliesStatusThread();
    }

    private void startUpdateAliesStatusThread() {
        isNeedToFinishUpdateAliesStatusThread = false;
        updateAliesStatusThread = new Thread(this::updateAliesStatus);
        updateAliesStatusThread.start();
    }

    private void updateAliesStatus() {
        Thread.currentThread().setName("Update status Allies: " + nickname);
        currentAgentsNumber = decryptionManager.getCurrentProcessAgentsNumber();
        allAgentsDisplay = new AgentDisplay[currentAgentsNumber];
        for(int i=0; i< currentAgentsNumber; i++) {
            allAgentsDisplay[i] = new AgentDisplay();
        }

        aliesStatus = new AliesStatus(allAgentsDisplay);

        while(true) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException ignored) { }
            if(isNeedToFinishUpdateAliesStatusThread)
                break;
            updateStatus();
        }
    }

    private void updateStatus() {
        List<AgentStatus> agentStatusList = decryptionManager.getDecryptionStatus();
        if(isNeedToFinishUpdateAliesStatusThread)
            return;

        List<StringCandidate> allStringCandidates = new ArrayList<>();
        long counterMissionsDone = 0;

        for(int i=0; i< currentAgentsNumber; i++) {
            AgentStatus agentStatus = agentStatusList.get(i);
            counterMissionsDone += agentStatus.getCounterMissionsDone();
            allStringCandidates.addAll(Arrays.asList(agentStatus.getStringCandidateArray()));
            allAgentsDisplay[agentStatus.getAgentId() -1].setAgentDisplay(agentStatus.getCounterMissionsDone(), agentStatus.getCounterMissionsInQueue());
        }

        aliesStatus.setProgressPercentage(decryptionManager.getProgressPercentTillNow(counterMissionsDone));
        allStringCandidates.forEach((stringCandidate ->  {
            String messageLog = String.format("Candidate: %s, code: %s, agent: %d.", stringCandidate.getStringCandidate(), stringCandidate.getCodeFoundedAt(), stringCandidate.getAgentId());
            logCandidatesManager.addChatString(messageLog);
            contestManager.addStringCandidateToLogUboatAndCheckWin(nickname, stringCandidate, nickname + "- " + messageLog);
        }));
    }

    public LogData getLogData(int logVersion) {
        return new LogData(logCandidatesManager.getChatEntries(logVersion), logCandidatesManager.getVersion(), aliesStatus);
    }

    public InitializeMessage getInitializeMessageForSizeMission() {
        return new InitializeMessage(possibilitiesNumber, decryptionManager.getPort(), nickname);
    }

    public void setParameters(int missionSize, int agentsNumberCounter)throws Exception {
        if(missionSize > possibilitiesNumber) {
            throw new Exception("Mission size is not in the right range has to be less than: " + possibilitiesNumber + ".");
        }
        else if(missionSize <= 0) {
            throw new Exception("Mission size has to be positive number.");
        }
        else {
            if(agentsNumberCounter < 1 ) {
                throw new Exception("Agents number has to be at least 1.");
            }
            else {
                this.currentMissionSize = missionSize;
                participantDisplay.setReady();
                decryptionManager.signalToCancelNextAgents();
            }
        }
    }

    public void addLogMessage(String message) {
        logCandidatesManager.addChatString(message);
    }

    public void killDm() {
        decryptionManager.killMessageManager();
    }

    public class InitializeMessage {
        private long possibilitiesNumber;
        private int port;
        private String nickname;

        private InitializeMessage(long possibilitiesNumber, int port, String nickname) {
            this.possibilitiesNumber = possibilitiesNumber;
            this.port = port;
            this.nickname = nickname;
        }
    }
}
