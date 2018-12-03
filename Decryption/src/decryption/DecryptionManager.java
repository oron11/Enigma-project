package decryption;

import commonclasses.*;
import enigma.emachine.EnigmaMachine;
import enigma.emachine.Reflector;
import enigma.emachine.Rotor;
import enigma.emachine.Secret;

import java.util.*;
import java.util.concurrent.ConcurrentLinkedDeque;

public class DecryptionManager {
    private static final int MIN_AGENTS_NUM = 2;
    private static final int MAX_AGENTS_NUM = 50;

    private static long maxMissionsNumber;
    private final Object dmLock = new Object();

    private boolean isNeedToStartProcess;
    private String userMessage;
    private int currentMissionSize;

    private int agentsNumberUserEntered;
    private EnigmaMachine enigmaMachine;
    private Trie dictionaryTrie;
    private LevelDifficultyMission levelDifficultyMission;

    private MessageManager messageManager;
    private Queue<Message> agentsMessagesQueue;
    private final Object agentsMessageQueueLock;

    private boolean isNeedToCallContinue;
    private boolean isNeedToPauseProcess;
    private final Object pauseLock;

    private long allPossibilitiesNumberWithoutEasyLevel;
    private long maxOptionsPossible;
    private Queue<Mission> missionsToDo;
    private boolean stopProcess;
    private int nextAgentExecuting;
    private int currentProcessAgentsNumber;
    private ConnectedAgentsNumber connectedAgentsNumber;

    public int getCurrentProcessAgentsNumber() {
        return currentProcessAgentsNumber;
    }

    private List<Integer> agentsIdThatGotMissions;
    private boolean continueDeliverMissionsBatch;

    private Timer timer;
    private String decryptionProcessEndTimeString;
    private ActionForDm nextActionForDm;

    private List<AgentStatus> agentStatusList;
    private final Object agentStatusListLock;

    public void killMessageManager() {
        messageManager.closeServerSocket();
    }

    public enum LevelDifficultyMission {
        Easy, Medium, Hard, Insane
    }

    private enum ActionForDm {
        None, showData, pauseProcess, continueProcess, stopProcess
    }

    public DecryptionManager(ConnectedAgentsNumber connectedAgentsNumber) {
        nextActionForDm = ActionForDm.None;
        pauseLock = new Object[0];
        isNeedToPauseProcess = false;
        stopProcess = false;
        isNeedToCallContinue = false;
        this.currentProcessAgentsNumber = 0;

        agentStatusList = null;
        agentStatusListLock = new Object();
        agentsMessageQueueLock = new Object();
        this.connectedAgentsNumber = connectedAgentsNumber;
        messageManager = new MessageManager(this);
        messageManager.start();
    }

    public void setTrie(Trie dictionaryTrie) {
        this.dictionaryTrie = dictionaryTrie;
    }

    public void addAgentMessageToQueue(Message message) {
        synchronized (agentsMessageQueueLock) {
            agentsMessagesQueue.add(message);
            agentsMessageQueueLock.notify();
        }
    }

    public void setLevelDifficultyMission(LevelDifficultyMission levelDifficultyMission) {
        this.levelDifficultyMission = levelDifficultyMission;
    }

    public boolean isProcessStopped() {return stopProcess;}

    private String getDecryptionProcessEndTimeString() { return decryptionProcessEndTimeString; }

    /*public long getMaxMissionSize(int agentsNumberUserEntered) {
        this.agentsNumberUserEntered = agentsNumberUserEntered;
        return (long)Math.floor(maxOptionsPossible / (agentsNumberUserEntered - 1)) - 1;
    }*/

    public void signalToCancelNextAgents(){
        messageManager.signalToCancelNextAgents();
    }

    public void incrementNumberOfCurrentAgentsOnProcess() {
        currentProcessAgentsNumber++;
        this.connectedAgentsNumber.setConnectedAgentsNumber(currentProcessAgentsNumber);
    }

    public int getAgentsNumberThatGotMissions() {
        return agentsIdThatGotMissions.size();
    }

    public int getMinLevel() {
        return LevelDifficultyMission.Easy.ordinal() + 1;
    }

    public int getMaxLevel() {
        return LevelDifficultyMission.Insane.ordinal() + 1;
    }

    private void sendMessageWithNoAnswer(Message message) {
        for(int i=0; i< currentProcessAgentsNumber; i++) {
             messageManager.sendMessageNoAnswerToAgentHandler(i, message);
        }
    }

    public int getPort() {
        return messageManager.getPort();
    }

    private void killAllAgents() {
        sendMessageWithNoAnswer(new Message(Message.Function.stopAndKillAgent));
        messageManager.closeSockets();
        this.currentProcessAgentsNumber = 0;
    }

    public void signalPauseDecryptionProcess() {
        synchronized (dmLock) {
            /*if(nextActionForDm == ActionForDm.printFinishProcess)
                return;*/
            nextActionForDm = ActionForDm.pauseProcess;
            isNeedToPauseProcess = true;
            dmLock.notify();
        }
    }

    private void pauseDecryptionProcess() {
        sendMessageWithNoAnswer(new Message(Message.Function.setAgentToPauseProcess));
        timer.pauseTimer();
    }

    public void signalContinueDecryptionProcess() {
        synchronized (dmLock) {
           /*if(nextActionForDm == ActionForDm.printFinishProcess)
                return;*/
            synchronized (pauseLock) {
                nextActionForDm = ActionForDm.continueProcess;
                isNeedToPauseProcess = false;
                pauseLock.notifyAll();
            }
            dmLock.notify();
        }
    }

    private void continueDecryptionProcess() {
        sendMessageWithNoAnswer(new Message(Message.Function.setAgentToContinueProcess));
        timer.continueTimer();
    }

    private void signalStopProcesses() {
        synchronized (dmLock) {
            /*if(nextActionForDm == ActionForDm.printFinishProcess)
                return;*/
            synchronized (pauseLock) {
                nextActionForDm = ActionForDm.stopProcess;
                stopProcess = true;
                pauseLock.notify();
            }
            dmLock.notify();
        }
    }

    private void signalShowData() {
        synchronized (dmLock) {
            /*if(nextActionForDm == ActionForDm.printFinishProcess)
                return;*/
            synchronized (pauseLock) {
                pauseLock.notify();
                nextActionForDm = ActionForDm.showData;
            }
            dmLock.notify();
        }
    }

    private void stopProcesses() {
        //decryptionProcessEndTimeString = getTimeSpentOnMissionTillNow();
        //collectAgentStatusData();
        killAllAgents();
    }

    public Object getDmLock() {return dmLock;}

    public void setNextActionToContinueDeliverMissionsBatch() {continueDeliverMissionsBatch = true;}

    private String getTimeSpentOnMissionTillNow() { return timer.getTimeTillNow(); }

    private void startToCalcTimeForDecryptionProcess() { timer = new Timer(); }

    private void waitForCommands() {
        synchronized (dmLock) {
            while(!stopProcess) {
                try {
                    dmLock.wait();
                } catch (InterruptedException ignored) { }
                handleUICommand();

                if(continueDeliverMissionsBatch) {
                    continueDeliverMissionsBatch = false;
                    break;
                }
            }
        }
    }

    private void handleUICommand() {
        if(nextActionForDm == ActionForDm.pauseProcess && isNeedToPauseProcess) {
            pauseDecryptionProcess();
            isNeedToCallContinue = true;
        }
        else if(nextActionForDm == ActionForDm.continueProcess && isNeedToCallContinue) {
            continueDecryptionProcess();
            isNeedToCallContinue = false;
        }
        else if(nextActionForDm == ActionForDm.showData) {
            collectAgentStatusData();
        }

        nextActionForDm = ActionForDm.None;
    }

    private void finishProcess() {
        System.out.println("Decryption process has finished!");
        waitForCommands();
        stopProcesses();

       // System.out.println(presentFinalDataOfDecryptionProcessWithoutWait());
    }

    private void collectAgentStatusData() {
        synchronized (agentStatusListLock) {
            AgentStatus[] agentStatusArray = new AgentStatus[currentProcessAgentsNumber];
            int counterAgentsEnteredToArray = 0;
            synchronized (agentsMessageQueueLock) {
                sendMessageWithNoAnswer(new Message<AgentStatus>(Message.Function.getAgentStatus, new AgentStatus()));

                while (counterAgentsEnteredToArray < currentProcessAgentsNumber) {
                    try {
                        if(agentsMessagesQueue.isEmpty()) {
                            agentsMessageQueueLock.wait();
                        }
                    } catch (InterruptedException ignored) { }

                    Message message = agentsMessagesQueue.poll();
                    if(message.getFunctionNumToActivate() == Message.Function.getAgentStatus) {
                        AgentStatus agentStatus = (AgentStatus) message.getParam();
                        agentStatusArray[agentStatus.getAgentId() - 1] = agentStatus;
                        counterAgentsEnteredToArray++;
                    }
                    else {
                        throw new RuntimeException("Something went wrong with queue of agents messages.");
                    }
                }
            }
            agentStatusList = Arrays.asList(agentStatusArray);
            agentStatusListLock.notify();
        }
    }

    public String presentFinalDataOfDecryptionProcess() {
        signalStopProcesses();
       // waitForAgentsStatusListToUpdate();
       // return presentFinalDataOfDecryptionProcessWithoutWait();
        return null;
    }

    private String presentFinalDataOfDecryptionProcessWithoutWait() {
        List<StringCandidate> allStringCandidates = new ArrayList<>();
        List<ParticipatedAgent> participatedAgents = new ArrayList<>(currentProcessAgentsNumber);
        long counterMissionsDone = 0;

        for(int i=0; i< currentProcessAgentsNumber; i++) {
            AgentStatus agentStatus = agentStatusList.get(i);
            allStringCandidates.addAll(Arrays.asList(agentStatus.getStringCandidateArray()));
            participatedAgents.add(new ParticipatedAgent(agentStatus.getAgentId(), agentStatus.getCounterMissionsDone()));
            counterMissionsDone += agentStatus.getCounterMissionsDone();
        }
        agentStatusList = null;

        StringBuilder res = new StringBuilder();
        res.append("Final status of decryption process:").append(System.lineSeparator());
        res.append(System.lineSeparator());

        res.append(String.format("Time spent on the mission: %s." ,getDecryptionProcessEndTimeString()) );
        res.append(System.lineSeparator());

        res.append(String.format("Total missions number calculated during the process: %d.", counterMissionsDone ));
        res.append(System.lineSeparator());

        res.append("List of all agents participated in the decryption process(Agent id: number of missions calculated):").append(System.lineSeparator());
        for(ParticipatedAgent participatedAgent : participatedAgents) {
            res.append(String.format("%d. %d", participatedAgent.getAgentId(), participatedAgent.getMissionNumberOfAgent()));
            res.append(System.lineSeparator());
        }

        buildStringOfStringsCandidates(res, allStringCandidates,true);
        return res.toString();
    }

    private void waitForAgentsStatusListToUpdate() {
        synchronized (agentStatusListLock) {
            while (agentStatusList == null) {
                try {
                    agentStatusListLock.wait();
                } catch (InterruptedException ignored) { }
                if(stopProcess)
                    break;
            }
        }
    }

    public List<AgentStatus> getDecryptionStatus() {
        if(!stopProcess) {
            agentStatusList = null;
        }

        signalShowData();
        waitForAgentsStatusListToUpdate();
        return agentStatusList;
    }

    public String showCurrentDecryptionStatus() {
        long counterMissionsDone = 0;
        List<StringCandidate> allStringCandidates = new ArrayList<>();
        List<String> missionAgentString = new ArrayList<>(currentProcessAgentsNumber);

        signalShowData();
        waitForAgentsStatusListToUpdate();

        for(int i=0; i< currentProcessAgentsNumber; i++) {
            AgentStatus agentStatus = agentStatusList.get(i);
            counterMissionsDone += agentStatus.getCounterMissionsDone();
            allStringCandidates.addAll(Arrays.asList(agentStatus.getStringCandidateArray()));
            //missionAgentString.add(String.format("Agent id: %d, %s", agentStatus.getAgentId(), agentStatus.getMissionProgress()));
        }
        agentStatusList = null;

        StringBuilder res = new StringBuilder();
        res.append("Status of current decryption process:").append(System.lineSeparator());
        res.append(System.lineSeparator());

        res.append(String.format("Time spent till now on the mission: %s." ,getTimeSpentOnMissionTillNow()) );
        res.append(System.lineSeparator());

        res.append(String.format("Progress percent till now: %d", getProgressPercentTillNow(counterMissionsDone) ));
        res.append(System.lineSeparator());

        res.append("Status of current decryption process to all agents:").append(System.lineSeparator());
        for(int i=0; i< currentProcessAgentsNumber; i++) {
            res.append(missionAgentString.get(i));
            res.append(System.lineSeparator());
        }

        buildStringOfStringsCandidates(res, allStringCandidates, false);
        return res.toString();
    }

    private void buildStringOfStringsCandidates(StringBuilder res, List<StringCandidate> allStringsCandidates, boolean isProcessFinished) {
        if(allStringsCandidates.isEmpty()) {
            res.append("At this moment, there isn't any strings candidates to present." );
        }
        else {
            res.append("String decrypted candidates(Number: String candidate, Code founded at, Agent id):" );
            res.append(System.lineSeparator());
            int i = 1;
            if(isProcessFinished) {
                for (StringCandidate stringCandidate : allStringsCandidates) {
                    res.append(String.format("%d. %s %s %d.", i, stringCandidate.getStringCandidate(), stringCandidate.getCodeFoundedAt(), stringCandidate.getAgentId()));
                    res.append(System.lineSeparator());
                    i++;
                }
            }
            else {
                for (int k = allStringsCandidates.size() - 1; k >= 0; k--) {
                    StringCandidate stringCandidate = allStringsCandidates.get(k);
                    res.append(String.format("%d. %s %s %d.", i, stringCandidate.getStringCandidate(), stringCandidate.getCodeFoundedAt(), stringCandidate.getAgentId()));
                    res.append(System.lineSeparator());
                    i++;
                    if (i == 11) {
                        break;
                    }
                }
            }
        }

        res.append(System.lineSeparator());
    }

    private void pauseDecryptionProcessIfNeed() {
        if(isNeedToPauseProcess) {
            synchronized (pauseLock) {
                pauseDecryptionProcess();
                while (isNeedToPauseProcess && !stopProcess) {
                    try {
                        //System.out.println("Decryption manager gonna pause...");
                        pauseLock.wait();
                    } catch (InterruptedException ignored) { }
                    checkIfActionIsShowData();
                }
                if (!stopProcess) {
                    continueDecryptionProcess();
                }
                //System.out.println("Decryption manager woke up...");
            }
            nextActionForDm = ActionForDm.None;
        }

        checkIfActionIsShowData();
    }

    private void checkIfActionIsShowData() {
        if(nextActionForDm == ActionForDm.showData) {
            collectAgentStatusData();
            nextActionForDm = ActionForDm.None;
        }
    }

     public int getProgressPercentTillNow(long counterMissionsDone) {
        int res = (int)((counterMissionsDone * 100) / maxMissionsNumber);
        if(res == 0) { res = 1; }

        return res;
    }

    public void setEnigmaMachine(EnigmaMachine enigmaMachine) {
        this.enigmaMachine = enigmaMachine;
        maxOptionsPossible = (long)Math.pow(enigmaMachine.getAlphabet().length(), enigmaMachine.getRotorsCount());
    }

    private void createMissionsToDo(int currentMissionSize, Secret secret) {
        missionsToDo = new ConcurrentLinkedDeque<>();
        System.gc();

        for(int i=0; i < maxOptionsPossible; i+=currentMissionSize) {
            int nextMissionSize;
            if(i + currentMissionSize < maxOptionsPossible) {
                nextMissionSize = currentMissionSize;
            }
            else {
                nextMissionSize = (int)(maxOptionsPossible - i);
            }
            missionsToDo.add(new Mission(nextMissionSize, i, secret.clone(), enigmaMachine.getAlphabet()));
        }
    }

    public void startDecryptionMission() {
        startToCalcTimeForDecryptionProcess();
        resetDecryptionProcess();
        waitForApproveToStart();

        if(!stopProcess) {
            startTheProcess(currentMissionSize, userMessage);

            switch (levelDifficultyMission) {
                case Easy: {
                    startEasyLevelDecryption(enigmaMachine.getSecretCode(), currentMissionSize);
                    break;
                }
                case Medium: {
                    startMediumLevelDecryption(enigmaMachine.getSecretCode().getRotors(), enigmaMachine.getReflectors(), currentMissionSize);
                    break;
                }
                case Hard: {
                    startHardLevelDecryption(enigmaMachine.getSecretCode().getRotors(), enigmaMachine.getReflectors(), currentMissionSize);
                    break;
                }
                case Insane: {
                    startSuperLevelDecryption(enigmaMachine.getRotors(), enigmaMachine.getReflectors(), currentMissionSize);
                    break;
                }
            }
        }

        if(!stopProcess) {
            finishProcess();
        } else {
            stopProcesses();
        }
    }

    public void flagToStartProcess(String userMessage, int currentMissionSize, Secret secretCode) {
        synchronized (dmLock) {
            this.userMessage = userMessage;
            this.currentMissionSize = currentMissionSize;
            enigmaMachine.initFromSecret(secretCode);
            isNeedToStartProcess = true;
            dmLock.notify();
        }
    }

    private void startTheProcess(int currentMissionSize, String userMessage) {
        messageManager.setAgentInitializeParams(new AgentInitializeParams(dictionaryTrie, enigmaMachine, userMessage));
        isNeedToStartProcess = false;
        waitForApproveToStart();
        maxMissionsNumber = allPossibilitiesNumberWithoutEasyLevel * ((long)Math.ceil((calcFirstPositionRotors() / (double)currentMissionSize)));
    }

    public void flagApproveToStartAfterSendAllInitializeMessages() {
        synchronized (dmLock) {
            isNeedToStartProcess = true;
            dmLock.notify();
        }
    }

    private void waitForApproveToStart() {
        synchronized (dmLock) {
            while(!isNeedToStartProcess) {
                try {
                    System.out.println(Thread.currentThread().getName() + " is waiting for agents to connect enter to sleep.");
                    dmLock.wait();
                } catch (InterruptedException ignored) { }
                if(stopProcess)
                    break;
            }
        }
        System.out.println(Thread.currentThread().getName() + " is out of sleep starting to work.");
    }

    private void resetDecryptionProcess() {
        Thread.currentThread().setName("Decryption-Manager");
        System.out.println(Thread.currentThread().getName() + " Started to work");

        agentsMessagesQueue = new ConcurrentLinkedDeque<>();
        decryptionProcessEndTimeString = null;
        stopProcess = false;
        nextActionForDm = ActionForDm.None;
        isNeedToPauseProcess = false;
        isNeedToCallContinue = false;
        nextAgentExecuting = 0;
        agentStatusList = null;
        this.continueDeliverMissionsBatch = false;

        isNeedToStartProcess = false;
    }

    private void startSuperLevelDecryption(List<Rotor> rotors, List<Reflector> reflectors, int currentMissionSize) {
        int numberOfSubGroups = (int)calcNumberOfRotorsChoices();
        int i = 0;
        Rotor[][] allRotorsOptions = Combinations.getAllSubGroupRotorsCombinations(rotors, enigmaMachine.getRotorsCount(), numberOfSubGroups);
        for(Rotor[] arrayRotorSubGroup : allRotorsOptions) {
            List<Rotor> listRotorSubGroup = Arrays.asList(arrayRotorSubGroup);
            allRotorsOptions[i++] = null;
            if(stopProcess) break;
            startHardLevelDecryption(listRotorSubGroup, reflectors, currentMissionSize);
            if(stopProcess) break;
        }
    }

    private void startHardLevelDecryption(List<Rotor> rotors, List<Reflector> reflectors, int currentMissionSize) {
        long maxOptions = factorial(rotors.size());
        Permute permuter = new Permute(rotors);
        List<List<Rotor>> allPermutations = permuter.permute();

        /*permuter.print();
        if(maxOptions == allPermutations.size()) {
            System.out.println("All good");
        }*/

        for(int i=0; i< maxOptions; i++) {
            if(stopProcess) break;
            startMediumLevelDecryption(allPermutations.get(i),reflectors, currentMissionSize);
            allPermutations.set(i, null);
            if(stopProcess) break;
        }
    }

    private void startMediumLevelDecryption(List<Rotor> rotors, List<Reflector> reflectors, int currentMissionSize) {
        for(Reflector reflector: reflectors) {
            Secret secret = new Secret(rotors, reflector, true);
            if(stopProcess) break;
            startEasyLevelDecryption(secret, currentMissionSize);
            if(stopProcess) break;
        }
    }

    private void startEasyLevelDecryption(Secret secret, int currentMissionSize) {
        createMissionsToDo(currentMissionSize, secret);
        Mission currentMissionToDo = missionsToDo.poll();
        agentsIdThatGotMissions = new LinkedList<>();

        while (currentMissionToDo != null && !stopProcess) {
            pauseDecryptionProcessIfNeed();
            int nextAgentHandlerForExecutingMissionIndex = getNextAgent();
            messageManager.sendMessageNoAnswerToAgentHandler(nextAgentHandlerForExecutingMissionIndex, new Message<Mission>(Message.Function.addMissionToQueue, currentMissionToDo));
            currentMissionToDo = missionsToDo.poll();
        }

        if(!stopProcess) {
            sendMessageWithNoAnswer(new Message(Message.Function.dmFinishedToDeliverMissionsBatch));
            waitForCommands();
        }
    }

    /*private void startEasyLevelDecryption(Secret secret, int currentMissionSize) {
        createMissionsToDo(currentMissionSize, secret);
        Mission currentMissionToDo = missionsToDo.poll();
        agentsIdThatGotMissions = new LinkedList<>();
        int counterForBatch = 0;

        while (currentMissionToDo != null && !stopProcess) {
            pauseDecryptionProcessIfNeed();
            int nextAgentHandlerForExecutingMissionIndex = getNextAgent();
            messageManager.sendMessageNoAnswerToAgentHandler(nextAgentHandlerForExecutingMissionIndex, new Message<Mission>(Message.Function.addMissionToQueue, currentMissionToDo));
            currentMissionToDo = missionsToDo.poll();
            if(counterForBatch == 100 && missionsToDo.size() > 100) {
                sendMessageWithNoAnswer(new Message(Message.Function.dmFinishedToDeliverMissionsBatch));
                waitForCommands();
                counterForBatch = 0;
                agentsIdThatGotMissions = new LinkedList<>();
                System.gc();
            }
            counterForBatch++;
        }

        if(!stopProcess) {
            sendMessageWithNoAnswer(new Message(Message.Function.dmFinishedToDeliverMissionsBatch));
            waitForCommands();
        }
    }*/

    private int getNextAgent() {
        if(nextAgentExecuting == currentProcessAgentsNumber) {
            nextAgentExecuting = 0;
        }
        if(!agentsIdThatGotMissions.contains(nextAgentExecuting)) {
            agentsIdThatGotMissions.add(nextAgentExecuting);
        }

        return nextAgentExecuting++;
    }

    public static int getMinAgentsNum() {
        return MIN_AGENTS_NUM;
    }

    public static int getMaxAgentsNum() {
        return MAX_AGENTS_NUM;
    }

    /*public int getAgentsNumber() {
        return agentsNumber;
    }*/

    public long getPossibilitiesNumberAccordingToLevel(LevelDifficultyMission levelDifficultyMission) {
        long res = -1, firstPositionRotors = calcFirstPositionRotors();
        switch (levelDifficultyMission) {
            case Easy:   {res = firstPositionRotors; break;}
            case Medium: {res = firstPositionRotors * calcNumberReflectorsOptions(); break; }
            case Hard:   {res = firstPositionRotors * calcNumberReflectorsOptions() * calcNumberOfRotorsArrangement(); break; }
            case Insane:  {res = firstPositionRotors * calcNumberReflectorsOptions() * calcNumberOfRotorsArrangement() * calcNumberOfRotorsChoices(); break; }
        }
        allPossibilitiesNumberWithoutEasyLevel = res / firstPositionRotors;
        return res;
    }

    private LevelDifficultyMission convertIntToLevel(int levelDifficultyInMission) {
        return LevelDifficultyMission.values()[levelDifficultyInMission - 1];
    }

    private long calcFirstPositionRotors() {
        return (long) (int) Math.pow((double) enigmaMachine.getAlphabet().length(), (double) enigmaMachine.getRotorsCount());
    }

    private long calcNumberReflectorsOptions() {
        return enigmaMachine.getTotalReflectorsCount();
    }

    private long calcNumberOfRotorsArrangement() {
        return factorial(enigmaMachine.getRotorsCount());
    }

    private long calcNumberOfRotorsChoices() {
        return factorial(enigmaMachine.getTotalRotorsCount()) /
                ((factorial(enigmaMachine.getRotorsCount())) * (factorial(enigmaMachine.getTotalRotorsCount() - enigmaMachine.getRotorsCount())));
    }

    private long factorial(int num) {
        long factor = 1;
        for(int i=1; i<= num ; i++) {
            factor = factor * i;
        }

        return factor;
    }
}
