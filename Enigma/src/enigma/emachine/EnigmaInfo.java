package enigma.emachine;

import enigma.generated.Battlefield;

public class EnigmaInfo {
    private EnigmaMachine enigmaMachine;
    private Battlefield battlefield;
    private String excludeChars;
    private String words;

    public EnigmaInfo(EnigmaMachine enigmaMachine, Battlefield battlefield, String excludeChars, String words) {
        this.enigmaMachine = enigmaMachine;
        this.battlefield = battlefield;
        this.excludeChars = excludeChars;
        this.words = words;
    }

    public EnigmaMachine getEnigmaMachine() {
        return enigmaMachine;
    }

    public Battlefield getBattlefield() {
        return battlefield;
    }

    public String getExcludeChars() {
        return excludeChars;
    }

    public String getWords() {
        return words;
    }
}
