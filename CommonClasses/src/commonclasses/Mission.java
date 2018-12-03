package commonclasses;

import decryption.DecryptionManager;
import enigma.emachine.Secret;

import java.io.Serializable;

public class Mission implements Serializable {
    private int missionSize;
    private int startingIndex;
    private Secret startSecretCode;
    private String startStringCodeSpecification;

    public Mission(int missionSize, int startingIndex, Secret startSecretCode, String alphabet ) {
        this.missionSize = missionSize;
        this.startingIndex = startingIndex;
        this.startSecretCode = startSecretCode;

        startSecretCode.setRotorsPositionAccordingToIndex(startingIndex, alphabet);
        startStringCodeSpecification = startSecretCode.getCodeSpecification(false);
    }

    public int getMissionSize() {
        return missionSize;
    }

    public int getStartingIndex() {
        return startingIndex;
    }

    public Secret getStartSecretCode() { return startSecretCode; }

    public String getMissionRepresentation() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Mission Representation- Start secret code:");
        stringBuilder.append(startStringCodeSpecification);
        stringBuilder.append(String.format(", Mission size: %d.", missionSize));
        return stringBuilder.toString();
    }
}
