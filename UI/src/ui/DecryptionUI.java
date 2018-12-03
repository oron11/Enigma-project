package ui;

import decryption.DecryptionManager;
import decryption.MessageManager;
import enigma.emachine.EnigmaMachine;

import java.util.NoSuchElementException;
import java.util.Scanner;

public class DecryptionUI {
    private DecryptionManager decryptionManager;
    private Scanner scanner;
    private static final String[] commandsStrings =
            { "Show status of current decryption process" ,
                    "Pause Process",
                    "Continue Process",
                    "Stop process and return to main menu",
            };

    private boolean showPauseInSubMenu;

    public DecryptionUI(Scanner scanner) {
        this.scanner = scanner;
        this.showPauseInSubMenu = true;
    }

    public void setDecryptionManager(DecryptionManager decryptionManager) {
       this.decryptionManager = decryptionManager;
    }

    public void startDecryptionMission(EnigmaMachine enigmaMachine) {
        decryptionManager.setEnigmaMachine(enigmaMachine);
        String userMessage = getUserMessageToDecrypt();
        if(userMessage == null)
            return;

        this.showPauseInSubMenu = true;
        int levelDifficultyInMission = getUserLevelDifficultyInMission();
        long possibilitiesNumber = decryptionManager.getPossibilitiesNumberAccordingToLevel(null);
        int agentsNumberUserEntered = getAgentsNumberForMissionFromUser(possibilitiesNumber);
        int missionSize = getUserMissionSize(possibilitiesNumber, agentsNumberUserEntered);
        boolean userPermission = getUserPermissionToStartMission();

        if(userPermission) {
            Object connectedAgentLock = new Object();
           // new Thread(() -> {decryptionManager.startDecryptionMission(userMessage, levelDifficultyInMission, missionSize, connectedAgentLock); }).start();
            //waitForAgentsToConnect(connectedAgentLock, agentsNumberUserEntered);
            StartSecondaryDecryptionMenu();
        }

    }

     //Before the change:
    /*private void waitForAgentsToConnect(Object connectedAgentLock, int agentsNumber) {
        synchronized (connectedAgentLock) {
            while(MessageManager.getConnectedAgentsToSocket() < agentsNumber) {
                try {
                    System.out.println(String.format("System is waiting for total of: %d agents to connect, in order to start the decryption process.", agentsNumber));
                    System.out.println(String.format("Waiting for %d more agents to connect to the port: %d.", agentsNumber - MessageManager.getConnectedAgentsToSocket(), MessageManager.getPort()));
                    connectedAgentLock.wait();
                } catch (InterruptedException ignored) { }
            }
        }
    }*/

   /* private void waitForAgentsToConnect(Object connectedAgentLock, int agentsNumber) {
        synchronized (connectedAgentLock) {
            while(MessageManager.getConnectedAgentsToSocket() < 1) {
                try {
                    System.out.println(String.format("System is waiting for total of: %d agents to connect to the decryption process.", agentsNumber));
                    System.out.println(String.format("Waiting for 1 agent at least to connect to the port: %d.", MessageManager.getPort()));
                    connectedAgentLock.wait();
                } catch (InterruptedException ignored) { }
            }
        }
    }*/

    private void StartSecondaryDecryptionMenu() {
        int userChoice;

       do {
           if(decryptionManager.isProcessStopped()) {
               break;
           }

           printSecondaryMenu();
           userChoice = getFromUserSubMenuChoice();

           if(decryptionManager.isProcessStopped()) {
               break;
           }
           else {
               executeUserChoice(userChoice);
           }
        }while(userChoice != 3);

        System.out.println("Returning to main menu.");
    }

    private void executeUserChoice(int userChoice) {
        switch (userChoice) {
            case 1: { showCurrentDecryptionStatus(); break;}
            case 2: { pauseOrContinueDecryptionProcess(); break;}
            case 3: { stopProcess(); break;}
        }
    }

    private void stopProcess() {
        presentToUserExecuteCommand(commandsStrings[3]);
        System.out.println(decryptionManager.presentFinalDataOfDecryptionProcess());
        presentToUserSuccessMessageExecutingCommand(commandsStrings[3]);
    }

    private void pauseOrContinueDecryptionProcess() {
        if(showPauseInSubMenu) {
            presentToUserExecuteCommand(commandsStrings[1]);
            decryptionManager.signalPauseDecryptionProcess();
            showPauseInSubMenu = false;
            presentToUserSuccessMessageExecutingCommand(commandsStrings[1]);
        }
        else {
            presentToUserExecuteCommand(commandsStrings[2]);
            decryptionManager.signalContinueDecryptionProcess();
            showPauseInSubMenu = true;
            presentToUserSuccessMessageExecutingCommand(commandsStrings[2]);
        }
    }

    private void presentToUserExecuteCommand(String command) {
        System.out.println(String.format("---Executing the command: %s.", command));
    }

    private void presentToUserSuccessMessageExecutingCommand(String command) {
        System.out.println(String.format("Successfully executed the command: %s.", command));
    }

    private void showCurrentDecryptionStatus() {
        presentToUserExecuteCommand(commandsStrings[0]);
        System.out.println(decryptionManager.showCurrentDecryptionStatus());
    }

    private int getFromUserSubMenuChoice() {
        int minOption = 1;
        int maxOption = 3;
        String message = "Please enter your choice from above(1-3):";

        return UtilityInputFromUser.getFromUserNumber(scanner, message, minOption, maxOption);
    }

    private void printSecondaryMenu() {
        System.out.println("---Submenu decryption process---" + System.lineSeparator());
        System.out.println(String.format("%d. %s", 1, commandsStrings[0]));

        if(showPauseInSubMenu) {
            System.out.println(String.format("%d. %s", 2, commandsStrings[1]));
        }
        else {
            System.out.println(String.format("%d. %s", 2, commandsStrings[2]));
        }

        System.out.println(String.format("%d. %s", 3, commandsStrings[3]));
        System.out.println();
    }

    private boolean getUserPermissionToStartMission() {
        return UtilityInputFromUser.getBooleanChoiceFromUser(scanner,"Would you like to start the decryption process? Please press(Y/N)");
    }

   private String getUserMessageToDecrypt() {
        boolean isUserWantsToTryAgain = true;
        String userInput , resAfterDecryption = null;

        while(isUserWantsToTryAgain) {
            System.out.println("Please enter the message decryption to encode:");
            userInput = scanner.nextLine();
            try {
               // resAfterDecryption = decryptionManager.returnMessageValidationOfMessageToDecrypt(userInput);
                System.out.println("The encode of the message before is: " + resAfterDecryption);
                isUserWantsToTryAgain = false;
            }
            catch (NoSuchElementException e) {
                System.out.println(e.getMessage());
                isUserWantsToTryAgain = UtilityInputFromUser.getBooleanChoiceFromUser(scanner);
            }
        }

        return resAfterDecryption;
    }

    //Original:
   /* private int getAgentsNumberForMissionFromUser(long possibilitiesNumber) {
        int minAgentsNumber = decryptionManager.getMinAgentsNum();
        int maxAgentsNumber = decryptionManager.getAgentsNumber();
        String message = String.format("Please enter number of agents for the mission(Mission difficulty: %d, Integer between %d-%d):", possibilitiesNumber, minAgentsNumber, maxAgentsNumber);

        return UtilityInputFromUser.getFromUserNumber(scanner, message, minAgentsNumber, maxAgentsNumber);
    }*/

   //Should delete
   private int getAgentsNumberForMissionFromUser(long possibilitiesNumber) {
        int minAgentsNumber = decryptionManager.getMinAgentsNum();
        int maxAgentsNumber = 6;
        //minAgentsNumber = decryptionManager.getAgentsNumber();
        String message = String.format("Please enter number of agents for the mission(Mission difficulty: %d, Integer between %d-%d):", possibilitiesNumber, minAgentsNumber, maxAgentsNumber);

        return UtilityInputFromUser.getFromUserNumber(scanner, message, minAgentsNumber, maxAgentsNumber);
    }

    private int getUserMissionSize(long possibilitiesNumber, int agentsNumberUserEntered) {
        int minNumber = 1;
       // int maxNumber = (int)decryptionManager.getMaxMissionSize(agentsNumberUserEntered);
      //  String message = String.format("Please enter the size of the mission for every agent(Mission difficulty: %d, Integer between %d-%d):", possibilitiesNumber, minNumber, maxNumber);

      //  return UtilityInputFromUser.getFromUserNumber(scanner, message, minNumber, maxNumber);
        return 0;
    }

    private int getUserLevelDifficultyInMission() {
        int minLevel = decryptionManager.getMinLevel();
        int maxLevel = decryptionManager.getMaxLevel();
        String message = String.format("Please enter the difficulty level of the Mission(Integer between %d-%d):", minLevel, maxLevel);

        return UtilityInputFromUser.getFromUserNumber(scanner, message, minLevel, maxLevel);
    }
}
