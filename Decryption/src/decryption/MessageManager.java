package decryption;

import commonclasses.AgentInitializeParams;
import commonclasses.Message;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;

public class MessageManager extends Thread{
    private List<Socket> sockets;
    private ServerSocket serverSocket;
    private List<ObjectInputStream> objectInputStreams;
    private List<ObjectOutputStream> objectOutputStreams;
    private boolean isUserReady;

    private final String host = "localhost";
    private int port = -1;
    private int connectedAgentsToSocket;
    private static final Object portLock = new Object();

    private int counterAgentsFinished;
    private AgentInitializeParams agentInitializeParams;
    private DecryptionManager decryptionManager;
    private final Object counterAgentsFinishedLock;
    private boolean stopProcess;

    private final Object sendAgentsInitializeMessageLock;
    private boolean isNeedToSendAgentsInitializeMessage;
    private int counterAgentsGotInitializeMessage;

    public MessageManager(DecryptionManager decryptionManager) {
        connectedAgentsToSocket = 0;
        this.decryptionManager = decryptionManager;
        counterAgentsFinishedLock = new Object();
        isUserReady = false;

        sendAgentsInitializeMessageLock = new Object();
        isNeedToSendAgentsInitializeMessage = false;
        counterAgentsGotInitializeMessage = 0;
    }

    public void setAgentInitializeParams(AgentInitializeParams agentInitializeParams) {
        this.agentInitializeParams = agentInitializeParams;
        synchronized (sendAgentsInitializeMessageLock) {
            isNeedToSendAgentsInitializeMessage = true;
            sendAgentsInitializeMessageLock.notifyAll();
        }
    }

    public void signalToCancelNextAgents(){
        isUserReady = true;
        stopProcess = false;
    }

    public int getCounterAgentsGotInitializeMessage() { return counterAgentsGotInitializeMessage; }

    public int getConnectedAgentsToSocket() { return connectedAgentsToSocket; }

    public int getPort() {
        synchronized (portLock) {
            while (port == -1) {
                try {
                    portLock.wait();
                } catch (InterruptedException ignored) { }
            }
        }

        return port;
    }

    public void run() {
        Thread.currentThread().setName("Accepting sockets thread");
        initializeAgentsSockets();
    }

    private void initializeAgentsSockets() {
        findFreePort();
        initializeSockets();
        connectedAgentsToSocket = 0;
        counterAgentsFinished = 0;

        while(true) {
            try {
                Socket socket = serverSocket.accept();
                if(isUserReady) {
                    Message message = new Message<String>( Message.Function.cancelAgent, "Alies is ready! Agents will not enter while the contest is active." + System.lineSeparator() + "Your services are not required, You won't get missions.");
                    try {
                        ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                        objectOutputStream.writeObject(message);
                    } catch (IOException e) {
                            e.printStackTrace();
                    }
                }
                else {
                    sockets.add(socket);
                    objectOutputStreams.add(new ObjectOutputStream(socket.getOutputStream()));
                    objectInputStreams.add(new ObjectInputStream(socket.getInputStream()));
                    connectedAgentsToSocket++;
                    new Thread(() -> sendAgentInitializeMessage(connectedAgentsToSocket)).start();
                    new Thread(() -> readAgentsMessages(connectedAgentsToSocket)).start();

                    decryptionManager.incrementNumberOfCurrentAgentsOnProcess();
                }
            }
            catch (SocketException e) {
                break;
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private void sendAgentInitializeMessage(int id) {
        Thread.currentThread().setName("Agent " + id + " initializer message");

        synchronized (sendAgentsInitializeMessageLock) {
            while (!isNeedToSendAgentsInitializeMessage) {
                try {
                    System.out.println(Thread.currentThread().getName() + " is waiting for approve to initialize.");
                    sendAgentsInitializeMessageLock.wait();
                } catch (InterruptedException ignored) { }
                if(stopProcess)
                    return;
            }

            agentInitializeParams.setId(id);
            Message initializeMessage = new Message(Message.Function.initializeAgent, agentInitializeParams);
            sendMessageNoAnswerToAgentHandler(id - 1, initializeMessage);

            System.out.println(Thread.currentThread().getName() + " is did the initialize.");

            counterAgentsGotInitializeMessage++;
            if(counterAgentsGotInitializeMessage == connectedAgentsToSocket) {
                decryptionManager.flagApproveToStartAfterSendAllInitializeMessages();
                System.out.println(Thread.currentThread().getName() + " alerting dm to start");
            }
        }
    }

    private void sendCancelMessageForFutureConnectedAgents() {
        Message message = new Message<String>( Message.Function.cancelAgent, "Decryption manager has full capacity of agents!" + System.lineSeparator() + "Your services are not required, You won't get missions.");
        try {
            serverSocket = new ServerSocket(port);
        } catch (IOException e) {
            e.printStackTrace();
        }

        while (!stopProcess) {
            try {
                Socket socket = serverSocket.accept();
                ObjectOutputStream objectOutputStream = new ObjectOutputStream(socket.getOutputStream());
                objectOutputStream.writeObject(message);
            }
            catch (SocketException e) {
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendMessageNoAnswerToAgentHandler(int indexOfAgentHandler, Message message) {
        try {
            objectOutputStreams.get(indexOfAgentHandler).writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void closeSockets() {
        for(int i=0; i< sockets.size(); i++) {
            try {
                sockets.get(i).close();
                objectOutputStreams.get(i).close();
                objectOutputStreams.get(i).close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        connectedAgentsToSocket = 0;

        synchronized (sendAgentsInitializeMessageLock) {
            stopProcess = true;
            sendAgentsInitializeMessageLock.notifyAll();
        }

        initializeSockets();
        isUserReady = false;
        isNeedToSendAgentsInitializeMessage = false;
        counterAgentsFinished = 0;
        counterAgentsGotInitializeMessage = 0;

    }


    public void closeServerSocket() {
        try {
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void findFreePort() {
         port = -1;
        while(port == -1) {
            try {
                synchronized (portLock) {
                    serverSocket = new ServerSocket(0);
                    //serverSocket = new ServerSocket(2323);
                    port = serverSocket.getLocalPort();
                    portLock.notify();
                }
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void initializeSockets() {
        sockets = new ArrayList<>();
        objectInputStreams = new ArrayList<>();
        objectOutputStreams = new ArrayList<>();
    }

    private <T extends Serializable> void readAgentsMessages(int id) {
        Message message;
        Thread.currentThread().setName("Agent " + id + " Reader messages");
        ObjectInputStream objectInputStream = objectInputStreams.get(id -1);

        while(true) {
            try {
                message = (Message<T>) objectInputStream.readObject();
                if (!checkIfMessageIsAgentFinishedMessage(message)) {
                    decryptionManager.addAgentMessageToQueue(message);
                }
            }
            catch (IOException | ClassNotFoundException e) {
                //System.out.println("Thread " + currentThread().getName() + " finished.");
                break;
            }
        }
    }

    private boolean checkIfMessageIsAgentFinishedMessage(Message message) {
        if(message.getFunctionNumToActivate() == Message.Function.agentFinishedMissionsBatch) {
            synchronized (counterAgentsFinishedLock) {
                counterAgentsFinished++;
                if (counterAgentsFinished == decryptionManager.getAgentsNumberThatGotMissions()) {
                   // System.out.println(Thread.currentThread().getName() + " Alerting DM that all processes finished..");
                    synchronized (decryptionManager.getDmLock()) {
                        decryptionManager.setNextActionToContinueDeliverMissionsBatch();
                        decryptionManager.getDmLock().notify();
                    }
                    counterAgentsFinished = 0;
                }
                return true;
            }
        }
        else {
            return false;
        }
    }
}