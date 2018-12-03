package enigma.emachine;

import enigma.emachine.exceptions.*;

import java.io.*;
import java.util.*;

public class EnigmaMachine implements Serializable {
    private List<Rotor> rotors;
    private List<Reflector> reflectors;
    private String alphabet;
    private final int rotorsCount;
    private List<Secret> allSecrets;

    private List<Rotor> secretRotors;
    private Reflector secretReflector;
    private Secret currSecret;
    private int processedMessagesCount;
    private long sumAmountTimeOfProcessMessages;

    private List<Integer> rotorsIdForManuallySecretCode;

    public EnigmaMachine(List<Rotor> rotors, List<Reflector> reflectors, String alphabet, int rotorCount) {
        this.rotors = rotors;
        this.reflectors = reflectors;
        this.alphabet = alphabet;
        this.rotorsCount = rotorCount;
        processedMessagesCount = 0;
        sumAmountTimeOfProcessMessages = 0;
        allSecrets = new ArrayList<>();
        currSecret = null;
    }

    public EnigmaMachine clone() {
        List<Rotor> rotors = new ArrayList<>(this.rotors.size());
        for(Rotor rotor: this.rotors) {
            rotors.add(rotor.clone());
        }

        List<Reflector> reflectors = new ArrayList<>(this.reflectors.size());
        for(Reflector reflector : this.reflectors) {
            reflectors.add(reflector.clone());
        }

        EnigmaMachine newMachine = new EnigmaMachine(rotors, reflectors, alphabet, rotorsCount);
        //Secret secret = currSecret.clone();
      //  newMachine.initFromSecret(secret);
        return newMachine;
    }

    public List<Rotor> getRotors() {
        return rotors;
    }

    public Secret getSecretCode() {
        return currSecret;
    }

    public List<Reflector> getReflectors() {
        return reflectors;
    }

    public boolean isSecretCodeDefined() {
        if(currSecret != null) {
            return true;
        }
        else {
            return false;
        }
    }

    public void initFromSecret(Secret secret) {
        this.currSecret = secret;
    }

    public String getAlphabet() {
        return alphabet;
    }

    public int getRotorsCount() {
        return rotorsCount;
    }

    public int getTotalRotorsCount() {
        return rotors.size();
    }

    public int getTotalReflectorsCount() {
        return reflectors.size();
    }


    public char process(char fromChar) {
        int index = alphabet.indexOf(Character.toUpperCase(fromChar));

        if(index == -1) {
            throw new NoSuchElementException("Char: '" + fromChar + "' isn't found in alphabet: " + alphabet);
        }

        int indexResult = currSecret.process(index);
        return alphabet.charAt(indexResult);
    }

    public String processTillComma(String input) {
        char[] result = new char[input.length()];
        char[] source = input.toCharArray();

        for(int i = 0; i < input.length(); i++){
            result[i] = process(source[i]);
            if(result[i] == ' ') {
                return new String(result, 0, i + 1);
            }
        }

        return String.valueOf(result);
    }

    public String processWithoutStatistics(String input) {
        char[] result = new char[input.length()];
        char[] source = input.toCharArray();

        for(int i = 0; i < input.length(); i++){
            result[i] = process(source[i]);
        }

        return String.valueOf(result);
    }

    public String process(String input) {
        if(currSecret == null) {
            throw new SecretCodeIsNotDefinedException("Process input in the enigma machine");
        }

        long startTime = System.nanoTime();
        String resString = processWithoutStatistics(input);
        long endTime = System.nanoTime() - startTime;

        currSecret.saveDataStatistics(input, resString, endTime);
        processedMessagesCount++;
        sumAmountTimeOfProcessMessages += endTime;

        return resString;
    }

    public void resetToInitialPosition() {
        if(currSecret == null) {
            throw new SecretCodeIsNotDefinedException("Reset current secret code in the enigma machine");
        }

        currSecret.resetSecret();
    }

    public EnigmaMachine createSecret() {
        secretRotors = new ArrayList<>();
        secretReflector = null;
        currSecret = null;
        return this;
    }

    public EnigmaMachine selectReflector(int reflectorId) {
        if(secretReflector != null ) {
            throw new MaximumDefinedException(1, "reflectors in secret code");
        }

        secretReflector = returnReflectorIfFound(reflectorId);
        return this;
    }

    public Secret create() {
        if(secretReflector == null ) {
            throw new MinimumDefinedException(1, "reflector in secret code");
        }

        if(secretRotors.size() < rotorsCount) {
            throw new MinimumDefinedException(rotorsCount, "used rotors as expected") ;
        }

        this.currSecret = new Secret(secretRotors, secretReflector);
        allSecrets.add(currSecret);
        return currSecret;
    }

   /* public void buildDebugSecret(pukteam.enigma.component.machine.api.EnigmaMachine debugEm) {
        pukteam.enigma.component.machine.secret.SecretBuilder secretBuilder = debugEm.createSecret();
        for(Rotor rotor : secretRotors) {
            secretBuilder.selectRotor(rotor.getId(), rotor.getStartSecretChar());
        }
        secretBuilder.selectReflector(secretReflector.getId());
        secretBuilder.create();
    }*/

    public List<String> getStatisticsData() {
        List<String> machineStatisticsData = new ArrayList<>();
        if(allSecrets.size() == 0) {
             machineStatisticsData.add("Enigma machine doesn't have any secret code statistics to show.");
             return machineStatisticsData;
        }

        machineStatisticsData.add("Enigma machine history and data statistics: ");
        long averageOfAllTimeMessagesProcessed = processedMessagesCount != 0 ? sumAmountTimeOfProcessMessages / processedMessagesCount  : 0;
        machineStatisticsData.add("Average time of all messages processes is: " + averageOfAllTimeMessagesProcessed);

        for(Secret secret : allSecrets) {
            machineStatisticsData.addAll(secret.getStatisticsDataOfSecretCode());
        }

        return machineStatisticsData;
    }

    public EnigmaMachine selectRotor(int rotorId, char rotorPosition) {
        checkPotentailSecretRotorValidation(rotorId);
        rotors.get(rotorId - 1).setRotorToSecretCodePosition(rotorPosition);
        secretRotors.add(rotors.get(rotorId - 1));
        return this;
    }

    public EnigmaMachine selectRotor(int rotorId, int rotorPosition) {
        checkPotentailSecretRotorValidation(rotorId);
        rotors.get(rotorId - 1).setRotorToSecretCodePosition(rotorPosition);
        secretRotors.add(rotors.get(rotorId - 1));
        return this;
    }

    private Reflector returnReflectorIfFound(int reflectorId) {
        for(Reflector reflector : reflectors) {
            if(reflector.getId() == reflectorId) {
                return reflector;
            }
        }

        throw new NoSuchElementException("Couldn't find the id " + Secret.getRomanDigit(reflectorId) + " of the reflector.");
    }

    private void checkPotentailSecretRotorValidation(int rotorId) {
        if(secretRotors.size() == rotorsCount) {
            throw new MaximumDefinedException(rotorsCount, "used rotors as expected");
        }

        if(rotorId < 1 || rotorId > rotors.size()) {
            throw new NoSuchElementException("Rotor id " + rotorId + " isn't exists in the enigma machine.");
        }

        if(checkIfRotorIsInSecretAlready(rotorId)) {
            throw new IdAlreadyExistsException(rotorId, "secret rotors");
        }
    }

    private boolean checkIfRotorIsInSecretAlready(int rotorId) {
        for(Rotor rotor : secretRotors) {
            if(rotor.getId() == rotorId) {
                return true;
            }
        }

        return false;
    }

    public String randomizeSecretCode() {
        ArrayList<Integer> rotorsId = new ArrayList<>(rotorsCount);
        Random random = new Random();
        createSecret();

        while(rotorsId.size() < rotorsCount) {
            int rotorId = random.nextInt(rotors.size()) + 1 ;
            if(!rotorsId.contains(rotorId)) {
                rotorsId.add(rotorId);
                selectRotor(rotorId, random.nextInt(alphabet.length() - 1) + 1);
            }
        }

        selectReflector(random.nextInt(reflectors.size()) + 1);
        create();
        return currSecret.getCodeSpecification(false);
    }

    public void defineManuallySecretCodeRotors(String userInput) {
        rotorsIdForManuallySecretCode = new ArrayList<>();

        for(String rotorIdStr : userInput.split(",")) {
            int rotorId = Integer.parseInt(rotorIdStr);
            checkPotentailManuallySecretRotorValidation(rotorId);
            rotorsIdForManuallySecretCode.add(rotorId);
        }
        if(rotorsIdForManuallySecretCode.size() != rotorsCount) {
            throw new IllegalFormatSecretCodeException(rotorsIdForManuallySecretCode.size(), rotorsCount);
        }
    }

    public void defineManuallySecretCodeRotorsPositions(String userInput) {
        createSecret();
        if(userInput.length() != rotorsCount) {
            throw new IllegalFormatSecretCodeException(userInput.length(), rotorsCount);
        }

        userInput = userInput.toUpperCase();
        for(int i = rotorsIdForManuallySecretCode.size() - 1; i >= 0; i--) {
            selectRotor(rotorsIdForManuallySecretCode.get(i), userInput.charAt(i));
        }
    }

    public void defineManuallySecretCodeReflector(String userInput) {
        int reflectorID = getIdFromRomanDigit(userInput.toUpperCase());
        selectReflector(reflectorID);

        create();
        rotorsIdForManuallySecretCode = null;
    }

    private void checkPotentailManuallySecretRotorValidation(int rotorId) {
        if(rotorsIdForManuallySecretCode.size() == rotorsCount) {
            throw new MaximumDefinedException(rotorsCount, "used rotors as expected");
        }

        if(rotorId < 1 || rotorId > rotors.size()) {
            throw new NoSuchElementException("Rotor id " + rotorId + " isn't exists in the enigma machine.");
        }

        for(int id : rotorsIdForManuallySecretCode) {
            if(id == rotorId) {
                throw new IdAlreadyExistsException(rotorId, "rotors");
            }
        }
    }

    private int getIdFromRomanDigit(String part3) {
        int reflectorId;

        switch(part3) {
            case "I":   {reflectorId = 1; break; }
            case "II":  {reflectorId = 2; break; }
            case "III": {reflectorId = 3; break; }
            case "IV":  {reflectorId = 4; break; }
            case "V":   {reflectorId = 5; break;}
            default: { throw new NoSuchElementException("The given reflector id isn't between 1-5 in roman digits."); }
        }

        return reflectorId;
    }

    public List<String> getMachineSpecification() {
        List<String> machineSpecification = new ArrayList<>();

        machineSpecification.add("Machine specification:" );
        machineSpecification.add("Amount of wheels(used/possible): " + rotorsCount + "/" + rotors.size());
        machineSpecification.add("Locations of notches in rotors: ");

        for(Rotor rotor : rotors) {
            machineSpecification.add("Rotor id: " + rotor.getId() + " notch position: " + rotor.getNotchIndex());
        }

        machineSpecification.add("Number of reflectors: " + reflectors.size());
        machineSpecification.add("Number of messages processed by the machine: " + processedMessagesCount);

        if(currSecret != null) {
            machineSpecification.add(currSecret.getCodeSpecification(true));
        }

        return machineSpecification;
    }

    public String getSecretCodeString() {
        return currSecret.getCodeSpecification(false);
    }

    public void writeEnigmaMachineToFile(String path) throws IOException {
        FileOutputStream fileOutputStream = new FileOutputStream(path);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(this);
        objectOutputStream.close();
    }

    public static EnigmaMachine readEnigmaMachineFromFile(String path) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(path);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        EnigmaMachine enigmaMachine = (EnigmaMachine)objectInputStream.readObject();
        objectInputStream.close();
        return enigmaMachine;
    }
}
