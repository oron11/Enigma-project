package commonclasses;

import enigma.emachine.EnigmaMachine;

import java.io.Serializable;

public class AgentInitializeParams implements Serializable {
    private int id;
    private Trie trie;
    private EnigmaMachine enigmaMachine;
    private String encodedString;

    public int getId() { return id; }

    public Trie getTrie() {
        return trie;
    }

    public EnigmaMachine getEnigmaMachine() {
        return enigmaMachine;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getEncodedString() {
        return encodedString;
    }

    public AgentInitializeParams(Trie trie, EnigmaMachine enigmaMachine, String encodedString) {
        this.trie = trie;
        this.enigmaMachine = enigmaMachine;
        this.encodedString = encodedString;
    }
}
