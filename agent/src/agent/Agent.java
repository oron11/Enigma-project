package agent;

import commonclasses.Mission;
import commonclasses.StringCandidate;
import commonclasses.Trie;
import enigma.emachine.EnigmaMachine;
import enigma.emachine.Secret;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;

public class Agent extends Thread{
    private EnigmaMachine enigmaMachine;
    private Trie dictionaryTrie;
    private final int id;
    private BlockingQueue<Mission> missionsToDo;
    private boolean isKilled;
    private List<StringCandidate> stringCandidateList;
    private final Object stringCandidateListLock;

    private String encodedString;
    private Mission currentMissionToDo;
    private boolean stopProcess;
    private boolean isDmFinishedToDeliverMissionsBatch;
    private boolean isNeedToPauseProcess;
    private final Object pauseLock;
    private final Object currentMissionDoneLock;
    private long counterMissionsDone;
    private AgentHandler agentHandler;

    public Agent(int id, Trie trie, Object pauseLock, String encodedString, EnigmaMachine enigmaMachine, AgentHandler agentHandler) {
        this.dictionaryTrie = trie;
        this.id = id;
        this.isKilled = false;
        this.stopProcess = false;
        this.pauseLock = pauseLock;
        this.encodedString = encodedString;
        this.enigmaMachine = enigmaMachine;
        this.agentHandler = agentHandler;

        counterMissionsDone = 0;
        isNeedToPauseProcess = false;
        isDmFinishedToDeliverMissionsBatch = false;
        missionsToDo = new LinkedBlockingDeque();
        stringCandidateList = new ArrayList<>();
        currentMissionDoneLock = new Object();
        stringCandidateListLock = new Object();
    }

    public int getAgentId() {return id;}

    public void stopProcess() {stopProcess = true;}

    public boolean isAgentWaitingForEmptyQueue() {
        if(currentMissionToDo == null && missionsToDo.size() == 0) {
            return true;
        }
        return false;
    }

    public long getCounterMissionsDone() { return counterMissionsDone; }

    public void setAgentToPauseProcess() {isNeedToPauseProcess = true;}

    public void setAgentToContinueProcess() {isNeedToPauseProcess = false;}

    public void addMissionToQueue(Mission mission) {missionsToDo.add(mission);}

    public void setDmFinishedToDeliverMissionsBatch() { isDmFinishedToDeliverMissionsBatch = true;}

    public Object getPauseLock() { return pauseLock; }

    public List<StringCandidate> getStringCandidateList() {
        synchronized (stringCandidateListLock) {
            if(stringCandidateList.size() == 0) {
                return stringCandidateList;
            }
            else {
                List<StringCandidate> answer = stringCandidateList;
                stringCandidateList = new ArrayList<>();
                return answer;
            }
        }
    }

    public long getCounterMissionsInQueue() {
        synchronized (currentMissionDoneLock) {
            return missionsToDo.size();
        }
    }

    /*public String getMissionProgress() {
        synchronized (currentMissionDoneLock) {
            if (currentMissionToDo == null) {
                return "This agent currently doesn't working on a mission.";
            } else {
                return String.format("%s Number missions left: %d.", currentMissionToDo.getMissionRepresentation(), missionsToDo.size());
            }
        }
    }*/

    public void killAgent() {
        isKilled = true;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("Agent " + id);
        System.out.println(Thread.currentThread().getName() +" started to work.");

        while(!isKilled) {
            try {
                if(isDmFinishedToDeliverMissionsBatch && missionsToDo.size() == 0) {
                    System.out.println(Thread.currentThread().getName() +" finished to work on missions batch, sending alert message to dm.");
                    agentHandler.sendToDmAgentFinishedMissionsBatch();
                    isDmFinishedToDeliverMissionsBatch = false;
                }

                currentMissionToDo = missionsToDo.take();
                startDecryptionProcess();

            } catch (InterruptedException ignored) { }
        }
        System.out.println(Thread.currentThread().getName() +" finished to work on decryption process.");
    }

    private void startDecryptionProcess() {
        if(counterMissionsDone == 0)
            System.out.println(Thread.currentThread().getName() +" Started to work on first mission: " + currentMissionToDo.getMissionRepresentation());
        long currentIndex = currentMissionToDo.getStartingIndex();
        int missionSize = currentMissionToDo.getMissionSize();
        Secret secret = currentMissionToDo.getStartSecretCode();
        int i=0;

        while(i < missionSize && !stopProcess) {
            boolean isAllWordsFromDictionary = true;
            secret.setRotorsPositionAccordingToIndex(currentIndex, enigmaMachine.getAlphabet());
            enigmaMachine.initFromSecret(secret);
            StringBuilder resDecodeMessage = new StringBuilder(encodedString.length());
            String currentEncodedString = encodedString;

            while(!currentEncodedString.isEmpty() && !stopProcess) {   // if after stop doesn't need anything it's okay
                String wordAfterDecode = enigmaMachine.processTillComma(currentEncodedString);
                if(wordAfterDecode.equals(" ") || !dictionaryTrie.search(wordAfterDecode.split(" ")[0])) {
                    isAllWordsFromDictionary = false;
                    break;
                }

                resDecodeMessage.append(wordAfterDecode);
                currentEncodedString = currentEncodedString.substring(wordAfterDecode.length());
            }

            if(isAllWordsFromDictionary && resDecodeMessage.length() > 0) {
                synchronized (stringCandidateListLock) {
                    stringCandidateList.add(new StringCandidate(secret.getCodeSpecification(false), resDecodeMessage.toString(), id));
                }
                System.out.println("The output from agent " + id + " is: " + resDecodeMessage.toString());
            }

            pauseProcessIfNeed();
            currentIndex++;
            i++;
        }

        endMission();
    }

    private void endMission() {
        if(!stopProcess) {
            counterMissionsDone++;
        }

        synchronized (currentMissionDoneLock) {
            currentMissionToDo = null;
        }
    }

    private void pauseProcessIfNeed() {
        if(isNeedToPauseProcess) {
            synchronized (pauseLock) {
                while (isNeedToPauseProcess && !isKilled) {
                    try {
                        System.out.println("Agent id: "+ id + " gonna pause... ");
                        pauseLock.wait();
                    } catch (InterruptedException ignored) { }
                }
                System.out.println("Agent id: "+ id + " woke up. ");
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Agent agent = (Agent) o;
        return id == agent.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

     /* @Override
    public void run() {
        //new Thread(() -> executeInstructionsFromDm() ).start();

        Thread.currentThread().setName("Agent " + id);
        synchronized (lock) {
            while (true) {
                while ((!readyToStart && !isKilled) || stopProcess) {
                    try {
                        // System.out.println("Agent id: "+ id + " gonna wait... ");
                        lock.wait();
                    } catch (InterruptedException e) { }
                }
                //  counterAwaking++;
                if(isKilled)
                    break;
                //System.out.println("Agent id: "+ id + " awakened " + counterAwaking + " times");
                startDecryptionProcess();
                // System.out.println("Agent id: "+ id + " finished the " + counterAwaking + " time, going to wait");
            }
        }
    }*/
}
