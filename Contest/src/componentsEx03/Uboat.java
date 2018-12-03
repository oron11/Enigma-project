package componentsEx03;

import commonclasses.StringCandidate;
import commonclasses.Trie;
import componentsEx03.LogCandidates.LogCandidatesManager;
import enigma.emachine.EnigmaInfo;
import enigma.emachine.EnigmaMachine;
import enigma.emachine.Secret;

import java.util.NoSuchElementException;

public class Uboat {
    private String nickname;
    private EnigmaMachine enigmaMachine;
    private String excludeChars;
    private Trie dictionaryTrie;
    private String startSecretCodeString;
    private Secret startSecretCode;
    private String messageBeforeEncode;
    private String messageAfterEncode;
    private LogCandidatesManager logCandidatesManager;

    public String getMessageAfterEncode() {
        return messageAfterEncode;
    }

    public Secret getStartSecretCode() {
        return startSecretCode;
    }

    public Uboat(String nickname, EnigmaInfo enigmaInfo) {
        this.nickname = nickname;
        this.enigmaMachine = enigmaInfo.getEnigmaMachine();
        this.excludeChars = enigmaInfo.getExcludeChars();
        buildDictionaryTrie(enigmaInfo.getWords());
        messageBeforeEncode = null;
        messageAfterEncode = null;
    }

    public EnigmaMachine getEnigmaMachine() {
        return enigmaMachine;
    }

    public Trie getDictionaryTrie() {
        return dictionaryTrie;
    }

    public String getStartSecretCodeString() {
        return startSecretCodeString;
    }

    public String getMessageBeforeEncode() {
        return messageBeforeEncode;
    }

    public void setRandomEnigmaSecretCodeSecretCode() {
        this.startSecretCodeString = enigmaMachine.randomizeSecretCode();
        startSecretCode = enigmaMachine.getSecretCode();
    }

    public String getNickname() {
        return nickname;
    }

    public void setManualEnigmaSecretCodeSecretCode(String rotorsSelection, String rotorsFirstPosition, String reflectorSelection) {

        try {
            enigmaMachine.defineManuallySecretCodeRotors(rotorsSelection);
        }
        catch(NumberFormatException exception) {
            throw new RuntimeException("rotors selection:" + System.lineSeparator() + "The format of Secret rotors that entered is not matching to the format required.");
        }
        catch(Exception exception) {
            throw new RuntimeException("rotors selection: " + System.lineSeparator() + exception.getMessage());
        }

        try {
            enigmaMachine.defineManuallySecretCodeRotorsPositions(rotorsFirstPosition);
        }catch (Exception exception) {
            throw new RuntimeException("rotors first position:" + System.lineSeparator() + exception.getMessage());
        }

        try {
            enigmaMachine.defineManuallySecretCodeReflector(reflectorSelection);
        }catch (Exception exception) {
            throw new RuntimeException("reflector selection:" + System.lineSeparator() + exception.getMessage());
        }

        startSecretCodeString = enigmaMachine.getSecretCodeString();
        startSecretCode = enigmaMachine.getSecretCode();
    }

    public void encodeMessage(String messageToEncode) {
        messageBeforeEncode = messageToEncode.toUpperCase();
        messageAfterEncode = returnMessageValidationOfMessageToDecrypt(messageToEncode);
    }

    private String returnMessageValidationOfMessageToDecrypt(String userInput) {
        String afterExcludeCharsString = removeExcludeCharsFromString(userInput).toUpperCase();
        for(int i=0; i<afterExcludeCharsString.length(); i++) {
            if(enigmaMachine.getAlphabet().indexOf(afterExcludeCharsString.charAt(i)) == -1) {
                throw new NoSuchElementException(
                        String.format("User input contains char: %c ,that is not found in the alphabet defined in the enigma machine: %s.",
                                afterExcludeCharsString.charAt(i), enigmaMachine.getAlphabet()));
            }
        }

        for(String word : afterExcludeCharsString.split(" ")) {
            if(!dictionaryTrie.search(word)) {
                throw new NoSuchElementException(
                        String.format("User input contains word: %s that is not found in the dictionary defined in the enigma machine.", word));
            }
        }

        return enigmaMachine.processWithoutStatistics(afterExcludeCharsString);
    }

    private String removeExcludeCharsFromString(String input) {
        StringBuilder stringBuilder = new StringBuilder(input.length());

        for(int i=0; i<input.length(); i++) {
            char currentChar = input.charAt(i);
            if(excludeChars.indexOf(currentChar) == -1) {
                stringBuilder.append(currentChar);
            }
        }

        return stringBuilder.toString();
    }

    private void buildDictionaryTrie(String words) {
        String wordsAfterExcludeChars = removeExcludeCharsFromString(words).toUpperCase();
        wordsAfterExcludeChars = wordsAfterExcludeChars.trim();
        dictionaryTrie = new Trie();

        for(String word : wordsAfterExcludeChars.split(" ")) {
            dictionaryTrie.insert(word);
        }
    }

    public LogData getLogData(int logVersion) {
        return new LogData(logCandidatesManager.getChatEntries(logVersion), logCandidatesManager.getVersion());
    }

    public void startProcess(ChatManager logmanager) {
        this.logCandidatesManager = new LogCandidatesManager(logmanager);
    }

    public boolean addStringCandidateToLogUboatAndCheckWin(StringCandidate stringCandidate, String messageLog) {
        logCandidatesManager.addChatString(messageLog);
        return stringCandidate.getStringCandidate().equals(messageBeforeEncode);
    }

    public void addLogMessage(String message) {
        logCandidatesManager.addChatString(message);
    }
}
