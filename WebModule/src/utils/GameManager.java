package utils;
import componentsEx03.*;
import enigma.emachine.EnigmaInfo;
import enigma.emachine.MachineBuilderJaxB;
import servlets.commonservlets.SignUpServlet;

import java.io.InputStream;
import java.util.*;

public class GameManager {
    private static GameManager instance = null;
    private List<String> nicknamesCollection = Arrays.asList("Deadpool", "IronMan", "Joker", "SpiderMan", "WonderWoman", "SuperMan", "Batman", "Ninja", "Donatello", "Refael", "Leonardo", "Michelangelo");

    private Set<String> setNicknames;
    private final Object setNicknamesLock;

    private Map<String, ContestManager> mapContests;
    private Map<String, ContestInfo> mapContestsInfo;
    private final Object mapContestsLock;

    private Map<String, Alies> mapAlies;
    private final Object mapALiesLock;

    private GameManager() {
        setNicknames = new HashSet<>();
        mapContests = new HashMap<>();
        mapContestsInfo = new HashMap<>();
        setNicknamesLock = new Object();
        mapContestsLock = new Object();
        mapAlies = new HashMap<>();
        mapALiesLock = new Object();
    }

    public static GameManager getInstance() {
        if (instance == null) {
            synchronized(GameManager.class) {
                if (instance == null) {
                    instance = new GameManager();
                }
            }
        }
        return instance;
    }

    public void addNewAliesToTheGame(String nickname) {
        synchronized (mapALiesLock) {
            mapAlies.put(nickname, new Alies(nickname));
        }
    }

    public void addNicknameToServer(String nickname) throws NicknameExistsException {
        synchronized (setNicknamesLock) {
            if (setNicknames.contains(nickname)) {
                throw new NicknameExistsException("Nickname already exists");
            }
            setNicknames.add(nickname);
            if (nicknamesCollection.contains(nickname)) {
                List<String> newList = new ArrayList<>();
                for(String nicknameString : nicknamesCollection) {
                    if(!nicknameString.equals(nickname)) {
                        newList.add(nicknameString);
                    }
                }
                nicknamesCollection = newList;
            }
        }
    }

    public String getNicknameSuggestion() {
        return nicknamesCollection.size() > 0 ? nicknamesCollection.get(0) : null;
    }

    public String parseXmlFileAndDefineUboatAndReturnContestName(String nickname, InputStream inputStream, RedirectResponse redirectResponse) {
        String messageToUser = "Xml file uploaded successfully, A new contest has been added to the server." + System.lineSeparator() +
                               "You are being transferred to the contest defining page.";
        String contestName = null;
        try {
            EnigmaInfo enigmaInfo = MachineBuilderJaxB.parseXmlToEnigmaMachine(inputStream);
            Battlefield battlefield = new Battlefield(enigmaInfo.getBattlefield());

            synchronized (mapContestsLock) {
                if (mapContests.containsKey(battlefield.getBattlefieldName())) {
                    throw new RuntimeException("Battlefield name:" + battlefield.getBattlefieldName() + " already exists in the server.");
                }
                ContestManager newContestManager = new ContestManager(battlefield, new Uboat(nickname, enigmaInfo));
                mapContests.put(battlefield.getBattlefieldName(), newContestManager);
                mapContestsInfo.put(battlefield.getBattlefieldName(), newContestManager.getContestInfo());
            }

            redirectResponse.setRedirectUrl(Constants.SECRET_CODE_DEFINITION_URL);
            contestName = battlefield.getBattlefieldName();
        }
        catch (Exception e) {
            messageToUser = e.getMessage()!= null ? "Error: " + e.getMessage() : "Error: " + "The File is isn't in the right new format." + System.lineSeparator() +  "Maybe you have entered the old format from exercise 1?";
            redirectResponse.setErrorOccurred(true);
        }
        redirectResponse.setMessage(messageToUser);
        return contestName;
    }

    public void defineRandomSecretCode(String contestName, RedirectResponse redirectResponse) {
        try {
            Uboat uboat = getUboatFromContest(contestName);
            uboat.setRandomEnigmaSecretCodeSecretCode();
            redirectSuccess(redirectResponse, contestName);
        }catch (Exception exception) {
            redirectResponse.setMessage("Error: " + exception.getMessage());
            redirectResponse.setErrorOccurred(true);
        }
    }

    public void defineManualSecretCode(String contestName, String rotorsSelection, String rotorsFirstPosition, String reflectorSelection, RedirectResponse redirectResponse) {
        try {
            Uboat uboat = getUboatFromContest(contestName);
            uboat.setManualEnigmaSecretCodeSecretCode(rotorsSelection, rotorsFirstPosition, reflectorSelection);
            redirectSuccess(redirectResponse, contestName);

        } catch (Exception exception) {
            redirectResponse.setMessage("Error in " + exception.getMessage());
            redirectResponse.setErrorOccurred(true);
        }
    }

    private void redirectSuccess(RedirectResponse redirectResponse, String contestName) {
        getContestManager(contestName).addSuccessSecretCodeLogMessage();
        redirectResponse.setMessage("Enigma machine secret code has been defined successfully." );
        redirectResponse.setRedirectUrl(Constants.CONTEST_URL);
    }

    private Uboat getUboatFromContest(String contestName) throws Exception {
        synchronized (mapContestsLock) {
            ContestManager contestManager = mapContests.get(contestName);
            if(contestManager != null) {
                return contestManager.getUboat();
            }
        }
        throw new Exception("ContestName: " + contestName + " could not be found in the server.");
    }

    public void encodeMessage(String contestName, String messageToEncode, RedirectResponse redirectResponse) {
        try {
            ContestManager contestManager = getContestManager(contestName);
            if(contestManager == null) {
                redirectResponse.setMessage("Error: Could not find contest with the name: " + contestName+ ".");
                redirectResponse.setErrorOccurred(true);
                return;
            }

            contestManager.encodeMessage(messageToEncode);

            redirectResponse.setRedirectUrl(Constants.CONTEST_URL);
            redirectResponse.setMessage("The message has been encoded successfully." + System.lineSeparator() + "You are ready to start the contest.");

        } catch(Exception exception) {
            redirectResponse.setMessage("Error: " + exception.getMessage());
            redirectResponse.setErrorOccurred(true);
        }
    }

    private ContestManager getContestManager(String contestName){
        synchronized (mapContestsLock) {
            return mapContests.get(contestName);
        }
    }

    public RefreshAnswer getRefreshAnswer(String contestName, String nickname) {
        ContestManager contestManager = getContestManager(contestName);
        if(contestManager != null) {
            return contestManager.getRefreshAnswer(nickname);
        }
        else {
            return null;
        }
    }

    public ContestInfo getContestInfo(String contestName) {
        ContestManager contestManager = getContestManager(contestName);
        if(contestManager == null) {
            return null;
        }
        else {
            return contestManager.getContestInfo();
        }
    }

    public void uploadChatMessage(String contestName,String nickname, String userMessageToUpload) {
        ContestManager contestManager = getContestManager(contestName);
        if(contestManager != null) {
            contestManager.uploadMessageChatToServer(nickname, userMessageToUpload);
        }
    }

    public Map<String, ContestInfo> getMapContestsInfo() {
        synchronized (mapContestsLock) {
            return mapContestsInfo;
        }
    }

    public void addAliesToContest(String nickname, String contestName, RedirectResponse redirectResponse) {
        Alies alies = getAlies(nickname);
        ContestManager contestManager = getContestManager(contestName);
        if(contestManager == null) {
            redirectResponse.setErrorOccurred(true);
            redirectResponse.setMessage(String.format("Error: Server did not find contest in the name: %s.", contestName));
        }
        else if(alies == null) {
            redirectResponse.setErrorOccurred(true);
            redirectResponse.setMessage(String.format("Error: Server did not find alies in the nickname given: %s.", nickname));
        }
        else {
             if(contestManager.addAliesToTheContest(alies)) {
                 redirectResponse.setMessage(String.format("You have been joined to the contest: %s successfully.", contestName) + System.lineSeparator() + "You are being transferred to the contest page.");
                 redirectResponse.setRedirectUrl(Constants.CONTEST_URL);
             }
             else {
                 redirectResponse.setErrorOccurred(true);
                 String message = contestManager.isActive() ? String.format("Error: Server could not join you to the contest because the contest %s is active.", contestName) : String.format("Error: Server could not join you to the contest because the contest %s is full.", contestName);
                 redirectResponse.setMessage(message);
             }
        }
    }

    private Alies getAlies(String nickname) {
        synchronized (mapALiesLock) {
            return mapAlies.get(nickname);
        }
    }

    public Alies.InitializeMessage getAliesInitializeMessage(String nickname) {
        Alies alies = getAlies(nickname);
        if(alies == null) {
            return null;
        }
        else {
            return alies.getInitializeMessageForSizeMission();
        }
    }

    public void checkAliesParameters(String nickname,String contestName, int missionSize, int agentsNumberCounter, RedirectResponse redirectResponse) {
        Alies alies = getAlies(nickname);
        if(alies == null){
            redirectResponse.setMessage("Error: Could not find alies in the server with the nickname: " + nickname + ".");
            redirectResponse.setErrorOccurred(true);
        }
        else {
            ContestManager contestManager = getContestManager(contestName);
            if(contestManager == null) {
                redirectResponse.setMessage("Error: Could not find contest with the name: " + contestName+ ".");
                redirectResponse.setErrorOccurred(true);
            }
            else {
                try {
                    contestManager.setAliesParameters(alies, missionSize, agentsNumberCounter);
                    redirectResponse.setMessage("Mission size and agents number has been set successfully.");

                }catch (Exception e) {
                    redirectResponse.setMessage("Error: " + e.getMessage());
                    redirectResponse.setErrorOccurred(true);
                }
            }
        }
    }

    public void logoutUserFromContest(String nickname, String contestName, SignUpServlet.TypeEntity typeEntity, RedirectResponse redirectResponse) {
        ContestManager contestManager = getContestManager(contestName);
        if(contestManager == null) {
            redirectResponse.setMessage("Error: Could not find contest with the name: " + contestName+ ".");
            redirectResponse.setErrorOccurred(true);
        }
        else {
            synchronized (setNicknamesLock) {
                setNicknames.remove(nickname);
            }
            boolean isNeedToCloseContest = contestManager.logoutUserAndReturnIfNeedToCloseContest(nickname);
            if(isNeedToCloseContest) {
                synchronized (mapContestsLock) {
                    mapContestsInfo.remove(contestManager.getBattlefield().getBattlefieldName());
                    mapContests.remove(contestManager.getBattlefield().getBattlefieldName());
                }
            }
            if(typeEntity.equals(SignUpServlet.TypeEntity.Alies)) {
                synchronized (mapALiesLock) {
                    mapAlies.remove(nickname);
                }
            }

            redirectResponse.setMessage("You have been logged out from the contest successfully." + System.lineSeparator() + "you are being transferred to the sign up page.");
            redirectResponse.setRedirectUrl(Constants.SIGN_UP_URL);
        }
    }

    public LogData refreshChat(String contestName, int chatVersion) {
        ContestManager contestManager = getContestManager(contestName);
        if(contestName == null) {
            return null;
        }
        else {
            return contestManager.chatRefresh(chatVersion);
        }
    }

    public LogData refreshLog(String contestName, String nickname, int chatVersion) {
        ContestManager contestManager = getContestManager(contestName);
        if(contestName == null) {
            return null;
        }
        else {
            return contestManager.logRefresh(nickname, chatVersion);
        }
    }


    public class UboatExtraContestInfo {
        private String encodedMessage;
        private String startSecretCode;

        public UboatExtraContestInfo(String encodedMessage, String startSecretCode) {
            this.encodedMessage = encodedMessage;
            this.startSecretCode = startSecretCode;
        }
    }

    public UboatExtraContestInfo getUboatExtraContestInfo(String contestName) {
        ContestManager contestManager = getContestManager(contestName);
        if(contestManager == null) {
            return null;
        }
        else {
            Uboat uboat = contestManager.getUboat();
            return new UboatExtraContestInfo(uboat.getMessageBeforeEncode(), uboat.getStartSecretCodeString());
        }
    }
}































