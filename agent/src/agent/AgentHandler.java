package agent;

import commonclasses.*;
import enigma.emachine.EnigmaMachine;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class AgentHandler extends Thread {
    private Agent agent;
    private Socket socket;
    private boolean isKilled;
    private ObjectInputStream reader;
    private ObjectOutputStream writer;
    private final Object socketWriteLock;

    public AgentHandler(String host, int port) throws IOException {
        this.socket = new Socket(host, port);
        reader = new ObjectInputStream(socket.getInputStream());
        writer = new ObjectOutputStream(socket.getOutputStream());
        socketWriteLock = new Object();
    }

    public void run() {
        System.out.println("Agent handler is starting to work, trying to get initialize message from the decryption manager.");
        try {
            getInitializeMessageAndInitializeAgent();
            System.out.println("Agent handler got initialize message from the decryption manager and initialize agent successfully.");
            Thread.currentThread().setName("AgentHandler " + agent.getAgentId());
            System.out.println("Agent handler is starting to execute commands from the Decryption manager.");
            executeInstructionsFromDm();
        }
        catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    private void getInitializeMessageAndInitializeAgent() throws Exception {
        try {
            Message message = (Message)reader.readObject();
            if(message.getFunctionNumToActivate() == Message.Function.cancelAgent) {
                throw new DecryptionManagerIsFullException((String)message.getParam());
            }
            if(message.getFunctionNumToActivate() == Message.Function.stopAndKillAgent) {
                killAgentThread();
                agent.stopProcess();
                throw new Exception("Dm send finishing message Agent is shutting down.");
            }

            if(message.getFunctionNumToActivate() != Message.Function.initializeAgent) {
                throw new IOException("Something went wrong with initialize commonclasses");
            }

            initializeAgent((AgentInitializeParams)message.getParam());

        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void initializeAgent(AgentInitializeParams agentInitializeParams) {
        int id = agentInitializeParams.getId();
        Object pauseLock = new Object();
        Trie trie = agentInitializeParams.getTrie();
        EnigmaMachine enigmaMachine = agentInitializeParams.getEnigmaMachine();
        String encodedString = agentInitializeParams.getEncodedString();

        this.agent = new Agent(id, trie, pauseLock, encodedString, enigmaMachine.clone(), this);
        agent.start();
    }

    private void executeInstructionsFromDm() {
        try {
            while (!isKilled) {
                Message message = (Message) reader.readObject();
                executeCommandFromMessage(message);
            }
        }
        catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        finally {
            try {
                reader.close();
                writer.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendToDmAgentFinishedMissionsBatch() {
        Message message = new Message(Message.Function.agentFinishedMissionsBatch);
        try {
            synchronized (socketWriteLock) {
                writer.writeObject(message);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void executeCommandFromMessage(Message message) throws IOException {
        switch (message.getFunctionNumToActivate()) {
            case getAgentStatus:                      { sendAgentStatus(message); break;}
            case setAgentToPauseProcess:              { agent.setAgentToPauseProcess(); break;}
            case setAgentToContinueProcess:           { awakeAgent(); break;}
            case addMissionToQueue:                   { agent.addMissionToQueue((Mission)message.getParam()); break;}
            case stopAndKillAgent:                    { killAgentThread(); agent.stopProcess(); break;}
            case dmFinishedToDeliverMissionsBatch:    { dmFinishedToDeliverMissionsBatch(); break;}
        }
    }

    private void dmFinishedToDeliverMissionsBatch() {
        agent.setDmFinishedToDeliverMissionsBatch();
        if(agent.isAgentWaitingForEmptyQueue()) {
            agent.interrupt();
        }
    }

    private void awakeAgent() {
        synchronized (agent.getPauseLock()) {
            agent.setAgentToContinueProcess();
            agent.getPauseLock().notify();
        }
    }

    private void sendAgentStatus(Message message) throws IOException {
        AgentStatus agentStatus = new AgentStatus();
        agentStatus.setAgentId(agent.getAgentId());
        agentStatus.setCounterMissionsDone(agent.getCounterMissionsDone());
        agentStatus.setCounterMissionInQueue(agent.getCounterMissionsInQueue());
        agentStatus.setStringCandidateList(agent.getStringCandidateList());

        message.setParam(agentStatus);
        synchronized (socketWriteLock) {
            writer.writeObject(message);
        }
    }

    private void killAgentThread() {
        agent.killAgent();
        agent.interrupt();
        isKilled = true;
    }
}
