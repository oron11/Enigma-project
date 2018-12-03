package ui;

import enigma.emachine.EnigmaMachine;
import enigma.emachine.MachineBuilder;
import enigma.emachine.exceptions.IdAlreadyExistsException;
import enigma.emachine.exceptions.IllegalFormatSecretCodeException;
import enigma.emachine.exceptions.MaximumDefinedException;
import enigma.emachine.exceptions.SecretCodeIsNotDefinedException;
import enigma.emachine.MachineBuilderJaxB;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.regex.PatternSyntaxException;

public class EnigmaUI {
    private static final int EXIT = 11;
    private static final String mainMenuString;
    private static final String[] allCommandsStrings =
            { "Read enigma machine details from xml file" ,
              "Show enigma machine's specification",
              "Define manually secret code for the enigma machine",
              "Define random secret code for the enigma machine",
              "Process input in the enigma machine",
              "Reset current secret code in the enigma machine",
              "Show history and statistics of the enigma machine",
              "Start decryption process",
              "Write enigma machine to a file",
              "Read enigma machine from a file",
              "Exit from the console application"
             };

    private boolean debug = true;
    private final Scanner scanner = new Scanner(System.in);
    private int currentUserMainMenuChoice;
    private EnigmaMachine enigmaMachine;
    private DecryptionUI decryptionUI = new DecryptionUI(scanner);
   // private pukteam.enigma.component.machine.api.EnigmaMachine debugEM;

    static {
        mainMenuString = buildMainMenuString();
    }

    public void run() {
        while(currentUserMainMenuChoice != EXIT) {
            showMainMenu();
            getUserMainMenuChoice();
            executeUserMainMenuChoice();
        }

        System.out.println("Bye bye!");
    }

    private void executeUserMainMenuChoice() {
        switch (currentUserMainMenuChoice) {
           // case 1: { loadMachineFromXmlFile(); break;}
            case 2: { showEnigmaMachineSpecification(); break;}
            case 3: { defineManuallySecretCode(); break;}
            case 4: { randomizeSecretCode(); break;}
            case 5: { processInput(); break;}
            case 6: {resetEnigmaMachine(); break;}
            case 7: {showHistoryAndStatistics(); break;}
            case 8: {startDecryptionMission(); break; }
            case 9: {writeEnigmaMachineToFile(); break; }
            case 10: {readEnigmaMachineFromFile(); break; }
            case EXIT: { break;}
            default: {throw new IllegalArgumentException("User choice entered invalid number"); }
            }
            System.out.println();
    }

    private void startDecryptionMission() {
        if(checkIfEnigmaMachineExists(allCommandsStrings[7])) {
            if(enigmaMachine.isSecretCodeDefined()) {
                decryptionUI.startDecryptionMission(enigmaMachine.clone());
            }
            else {
                System.out.println(String.format("System can't execute this command: %s, Because secret code isn't defined in the enigma machine.",allCommandsStrings[9]));
                System.out.println("Please define secret code first by command number 3 or 4 in the main menu.");
            }
        }
    }

    private void readEnigmaMachineFromFile() {
        String path;
        boolean isUserWantToTryAgain = true;
        System.out.println("---Executing the command: " + allCommandsStrings[9]);
        while (isUserWantToTryAgain) {
            try {
                System.out.println("Enter the name of the file:");
                path = scanner.nextLine();
                enigmaMachine = EnigmaMachine.readEnigmaMachineFromFile(path);
                isUserWantToTryAgain = false;
                System.out.println("Successfully read the enigma machine from the file.");
            } catch (FileNotFoundException e) {
                System.out.println("System couldn't find the desired file name.");
                isUserWantToTryAgain = getBooleanChoiceFromUser();
            }
            catch (IOException e) {
                System.out.println(e.getMessage());
                isUserWantToTryAgain = getBooleanChoiceFromUser();
            }
            catch (ClassNotFoundException e) {
                System.out.println("Couldn't load enigma machine from the file correctly.");
                isUserWantToTryAgain = getBooleanChoiceFromUser();
            }
        }
    }

    private void writeEnigmaMachineToFile() {
        if(checkIfEnigmaMachineExists(allCommandsStrings[8])) {
            String path;
            boolean isUserValidInput = false;
            while (!isUserValidInput) {
                try {
                    System.out.println("Enter the name of the file:");
                    path = scanner.nextLine();
                    enigmaMachine.writeEnigmaMachineToFile(path);
                    isUserValidInput = true;
                    System.out.println("Successfully wrote the enigma machine to the file.");
                } catch (IOException e) {
                    System.out.println(e.getMessage());
                }
            }
        }
    }

    private void showHistoryAndStatistics() {
        List<String> machineData;
        if(checkIfEnigmaMachineExists(allCommandsStrings[6])) {
            machineData = enigmaMachine.getStatisticsData();
            for(String string : machineData) {
                System.out.println(string);
            }
        }
    }

    private void resetEnigmaMachine() {
        if(checkIfEnigmaMachineExists(allCommandsStrings[5])) {
            try {
                enigmaMachine.resetToInitialPosition();
                //if(debug) {debugEM.resetToInitialPosition(); }
                System.out.println("Successfully executed the command: " + allCommandsStrings[5]);
            }
            catch (SecretCodeIsNotDefinedException e) {
                System.out.println(e.getMessage());
                System.out.println("Please define secret code first by command number 3 or 4 in the main menu.");
            }
        }
    }

    private void processInput() {
        if(checkIfEnigmaMachineExists(allCommandsStrings[4])) {
            String userInput = null, machineDecodeOutput = null;
            boolean validInput = false, isUserWantToTryAgain = false;
            do {
                System.out.println("Please enter the required string to encode: ");
                try {
                    userInput = scanner.nextLine();
                    machineDecodeOutput = enigmaMachine.process(userInput);
                    validInput = true;
                    isUserWantToTryAgain = false;
                }
                catch(SecretCodeIsNotDefinedException e) {
                    System.out.println(e.getMessage());
                    System.out.println("Please define secret code first by command number 3 or 4 in the main menu.");
                }
                catch (NoSuchElementException e) {
                    System.out.println(e.getMessage());
                    isUserWantToTryAgain = getBooleanChoiceFromUser();
                }
            } while(isUserWantToTryAgain);

            if(validInput) {
                System.out.println("The decoded machine output for: <" + userInput + "> is: <" + machineDecodeOutput + ">");
               /* if(debug) {
                    String debugDecodeOutput = debugEM.process(userInput);
                    System.out.println("The decode debug machine output is: " + debugDecodeOutput);
                    if(debugDecodeOutput.equals(machineDecodeOutput)) {
                        System.out.println("All good there is a match between the machines");
                    }
                }*/
            }
        }
    }

    private void randomizeSecretCode() {
        if(checkIfEnigmaMachineExists(allCommandsStrings[3])) {
            String randomSecretCodeString = enigmaMachine.randomizeSecretCode();
            //if(debug) {enigmaMachine.buildDebugSecret(debugEM); }
            System.out.println("Successfully executed the command: " + allCommandsStrings[3]);
            System.out.println("The random " + randomSecretCodeString);
        }
    }

    private void defineManuallySecretCode() {
        if(checkIfEnigmaMachineExists(allCommandsStrings[2])) {
            chooseSecretCodeRotors();
            chooseSecretCodeRotorsPositions();
            chooseSecretCodeReflector();
           // if(debug) {enigmaMachine.buildDebugSecret(debugEM); }
            System.out.println("Successfully completed the command, " + enigmaMachine.getSecretCodeString());
        }
    }

    private void chooseSecretCodeReflector() {
        boolean validAnswer = false;
        String userInput;

        while(!validAnswer) {
            System.out.println("Please choose the reflector number in roman presentation(I, II, III, IV, V):");
            try {
                userInput = scanner.nextLine();
                enigmaMachine.defineManuallySecretCodeReflector(userInput);
                validAnswer = true;
                System.out.println("Successfully chose reflector for the secret code.");
            }
            catch(NoSuchElementException e) {
                System.out.println(e.getMessage());
            }
            if(!validAnswer) {
                System.out.println("Try to enter again." + System.lineSeparator());
            }
        }
    }

    private void chooseSecretCodeRotorsPositions() {
        boolean validAnswer = false;
        String userInput;

        while(!validAnswer) {
            System.out.println("Please choose the start chars positions of the rotors secret code that you entered earlier in the same order:");
            System.out.println("(Like: AO! | Have to be at least 2 | Without separations between the letters)");
            try {
                userInput = scanner.nextLine();
                enigmaMachine.defineManuallySecretCodeRotorsPositions(userInput);
                validAnswer = true;
                System.out.println("Successfully set rotors start positions for the secret code.");
            }
            catch(NoSuchElementException | IllegalFormatSecretCodeException e) {
                System.out.println(e.getMessage());
            }
            if(!validAnswer) {
                System.out.println("Try to enter again." + System.lineSeparator());
            }
        }
    }

    private void chooseSecretCodeRotors() {
        boolean validAnswer = false;
        String userInput;

        while(!validAnswer) {
            System.out.println("Please choose rotors secret code by their id's with comma between them:");
            System.out.println("(Like: 45,27,94 | Have to be at least 2 | The order defined from right to left | Without spaces)");
            try {
                userInput = scanner.nextLine();
                enigmaMachine.defineManuallySecretCodeRotors(userInput);
                validAnswer = true;
                System.out.println("Successfully chose rotors for the secret code.");
            }
            catch (NumberFormatException e){
                System.out.println("There was a problem during parsing numbers id, Please make you sure you entered numbers correctly.");
            }
            catch (PatternSyntaxException e) {
                System.out.println("User input isn't in the right pattern.");
            }
            catch(MaximumDefinedException | NoSuchElementException | IdAlreadyExistsException | IllegalFormatSecretCodeException e) {
                System.out.println(e.getMessage());
            }
            if(!validAnswer) {
                System.out.println("Try to enter again." + System.lineSeparator());
            }
        }
    }

    private void showEnigmaMachineSpecification() {
        List<String> machineSpecification;
        if(checkIfEnigmaMachineExists(allCommandsStrings[1])) {
            machineSpecification = enigmaMachine.getMachineSpecification();
            for(String string : machineSpecification) {
                System.out.println(string);
            }
        }
    }

   /* private void loadMachineFromXmlFile() {
        System.out.println("---Executing the command: " + allCommandsStrings[0]);
        boolean isUserWantToTryAgain = true;
        String userInput;

        while (isUserWantToTryAgain) {
            System.out.println("Please enter the xml file path:");
            userInput = scanner.nextLine();
            if (userInput.endsWith(".xml") || userInput.endsWith(".XML")) {
                isUserWantToTryAgain = false;
                try {
                    enigmaMachine = MachineBuilderJaxB.parseXmlToEnigmaMachine(userInput);
                    decryptionUI.setDecryptionManager(MachineBuilder.getInstance().getDecryptionManager());
                    //if (debug) { debugEM = MachineBuilder.getInstance().buildDebugMachine(); }
                    System.out.println("Successfully executed the command: " + allCommandsStrings[0]);
                } catch (FileNotFoundException e) {
                    System.out.println("System couldn't find the xml file provided.");
                    isUserWantToTryAgain = getBooleanChoiceFromUser();
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    System.out.println("Failed to execute the command, returning to main...");
                }
            }
            else {
                System.out.println("The file path isn't an xml file.");
                isUserWantToTryAgain = getBooleanChoiceFromUser();
            }
        }
    }*/

    private boolean checkIfEnigmaMachineExists(String action) {
        if(enigmaMachine == null) {
            System.out.println(String.format("System cannot preform this action: %s," + System.lineSeparator() + "Because user didn't defined enigma machine first.",action));
            return false;
        }

        System.out.println("---Executing the command: " + action);
        return true;
    }

    private void getUserMainMenuChoice() {
        int input = -1;
        boolean isValidNumber = false;

        while(!isValidNumber) {
            try {
                input = scanner.nextInt();

                if(input <= 0 || input > EXIT) {
                    System.out.println(String.format("User entered number that isn't in range 1-%d, Please try again:", EXIT));
                }
                else {
                    isValidNumber = true;
                }
            }
            catch(InputMismatchException e) {
                System.out.println(String.format("User entered non integer input, Please try again to enter number in range 1-%d:", EXIT));
                scanner.nextLine();
            }
        }
        scanner.nextLine();
        currentUserMainMenuChoice = input;
    }

    private boolean getBooleanChoiceFromUser() {
        return UtilityInputFromUser.getBooleanChoiceFromUser(scanner);
    }

    private void showMainMenu() {
        System.out.println(mainMenuString);
    }

    private static String buildMainMenuString() {
        StringBuilder mainMenuString = new StringBuilder();
        mainMenuString.append("---Welcome to Enigma machine console application---" + System.lineSeparator());
        mainMenuString.append(System.lineSeparator());

        for(int i=0; i< EXIT; i++) {
            mainMenuString.append(String.format("%d. %s" + System.lineSeparator(), i+1, allCommandsStrings[i]));
        }

        mainMenuString.append(System.lineSeparator());
        mainMenuString.append(String.format("Please enter your choice 1-%d from above: ", EXIT));


        return mainMenuString.toString();
    }
}
