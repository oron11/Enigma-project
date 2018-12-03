package enigma.emachine;

import decryption.DecryptionManager;
import enigma.emachine.exceptions.*;
import enigma.generated.*;

import java.util.*;

public class MachineBuilder {
    private Set<Integer> rotorsId;
    private Set<Integer> reflectorsId;

    private int rotorsCount;
    private String alphabet;
    private List<Rotor> rotors;
    private List<Reflector> reflectors;
    private Battlefield battlefield;
    private String excludeChars;
    private String words;

    private final int MAX_ROTORS = 99;
    private final int CHARS_MAX_NUMBER = 256;
    private final int MIN_ROTORS = 2;
    private final int MAX_REFLECTORS = 5;


    public EnigmaInfo checkEnigmaMachineValidationAndReturnEnigmaInfo(Enigma enigma) {
        buildMachine(enigma.getMachine().getRotorsCount(), enigma.getMachine().getABC());
        defineRotorsFromXmlEnigma(enigma.getMachine());
        defineReflectorsFromXmlEnigma(enigma.getMachine());
        buildDecryptionStructure(enigma);
        checkBattleFieldValues(enigma.getBattlefield());
        EnigmaMachine enigmaMachine = create();

        return new EnigmaInfo(enigmaMachine, battlefield, excludeChars, words );
    }

    private void checkBattleFieldValues(Battlefield battlefield) {
        String battleName = battlefield.getBattleName();
        int aliesCount = battlefield.getAllies();
        String levelStringFromFile = battlefield.getLevel();
        DecryptionManager.LevelDifficultyMission levelDifficultyMissionEnum = null;
        int roundsCount = battlefield.getRounds();

        if(aliesCount < 1) {
            throw new BattlefieldMinimumException(1, "allies", aliesCount);
        }

        if(roundsCount < 1) {
            throw new BattlefieldMinimumException(1, "rounds", roundsCount);
        }

        for(DecryptionManager.LevelDifficultyMission level : DecryptionManager.LevelDifficultyMission.values()) {
            if(level.name().equals(levelStringFromFile)) {
                levelDifficultyMissionEnum = level;
                break;
            }
        }

        if(levelDifficultyMissionEnum == null) {
            throw new RuntimeException("Can't define battlefield with unknown level name: " + levelStringFromFile + ".");
        }

        this.battlefield = battlefield;
    }

    private void buildDecryptionStructure(Enigma enigma) {
        int agentsCount = enigma.getDecipher().getAgents();
        String words = enigma.getDecipher().getDictionary().getWords();
        String excludeChars = enigma.getDecipher().getDictionary().getExcludeChars();

        if(agentsCount < DecryptionManager.getMinAgentsNum()) {
            throw new MinimumDefinedException(DecryptionManager.getMinAgentsNum(), "agents, Found in file: " + agentsCount);
        }

        if(agentsCount > DecryptionManager.getMaxAgentsNum()) {
            throw new MaximumDefinedException(DecryptionManager.getMaxAgentsNum(), "agents, Found in file: " + agentsCount);
        }

        this.excludeChars = excludeChars;
        this.words = words;
    }

    private void defineReflectorsFromXmlEnigma(Machine machine) {
        List<enigma.generated.Reflector> reflectors = machine.getReflectors().getReflector();

        for(enigma.generated.Reflector reflector : reflectors) {
            int id = Reflector.getIntId(reflector.getId());
            byte[] partA = buildPart(reflector.getReflect(), 0);   // 0 to get input
            byte[] partB = buildPart(reflector.getReflect(), 1);   // 1 to get output

            defineReflector(id, partA, partB);
        }
    }

    private byte[] buildPart(List<Reflect> reflect, int flag) {
        byte[] res = new byte[reflect.size()];
        for(int i=0; i<reflect.size(); i++) {
            if(flag == 0) {
                res[i] = (byte)reflect.get(i).getInput();
            }
            else {
                res[i] = (byte)reflect.get(i).getOutput();
            }
        }

        return res;
    }

    private void defineRotorsFromXmlEnigma(Machine machine) {
        List<enigma.generated.Rotor> rotors = machine.getRotors().getRotor();

        for(enigma.generated.Rotor rotor : rotors) {
            int id = rotor.getId();
            int notch = rotor.getNotch();
            String from = buildString(rotor.getMapping(), 0);   // 0 to get From
            String to = buildString(rotor.getMapping(), 1);     // 1 to get to
            defineRotor(id, from, to, notch);
        }
    }

    private String buildString(List<Mapping> mapping, int flag) {
        StringBuilder res = new StringBuilder(mapping.size());
        for(int i=0; i<mapping.size(); i++) {
            if(flag==0) {
                res.append(mapping.get(i).getRight());
            }
            else {
                res.append(mapping.get(i).getLeft());

            }
        }

        return res.toString();
    }

    public EnigmaMachine create() {
        if(rotors.size() < 2) {
            throw new MinimumDefinedException(MIN_ROTORS, "rotors");
        }

        if(!checkAllIdLocatedAndValid(rotorsId, rotors.size())) {
            throw new NotAllIdHasFoundException("rotors", rotors.size());
        }

        if(reflectors.size() == 0) {
            throw new MinimumDefinedException(1, "reflector");
        }
        if(!checkAllIdLocatedAndValid(reflectorsId, reflectors.size())){
            throw new NotAllIdHasFoundException("reflectors", reflectors.size());
        }

        if(rotorsCount > rotors.size()) {
            throw new InputMismatchException(String.format("The rotors count isn't matched to the rotors number defined, rotors count must be less or equal to %d." ,rotors.size()));
        }

        rotors.sort(new Comparator<Rotor>() {
            @Override
            public int compare(Rotor o1, Rotor o2) {
                if(o1.getId() > o2.getId()) return 1;
                else if(o1.getId() == o2.getId()) return 0;
                else return -1;
            }
        });

        return new EnigmaMachine(rotors, reflectors, alphabet, rotorsCount);
    }

    /*public pukteam.enigma.component.machine.api.EnigmaMachine buildDebugMachine() {
        pukteam.enigma.component.machine.builder.EnigmaMachineBuilder em = EnigmaComponentFactory.INSTANCE.buildMachine(rotorsCount, alphabet);
        for(Rotor rotor : rotors) {
            em.defineRotor(rotor.getId(), rotor.getSource(), rotor.getTarget(), rotor.getNotchIndex());
        }
        for(Reflector reflector : reflectors) {
            em.defineReflector(reflector.getId(), reflector.getPartA(), reflector.getPartB());
        }
        return em.create();
    }*/

    private boolean checkAllIdLocatedAndValid(Set<Integer> allId, int max) {
        if(allId.size() != max) {
            return false;
        }

        for(int i = 1; i <= max; i++) {
            if(!allId.contains(i)) {
                return false;
            }
        }

        return true;
    }

    public MachineBuilder() {
    }

    private void resetMachine() {
        rotorsId = new HashSet<>();
        reflectorsId = new HashSet<>();
        rotors = new ArrayList<>();
        reflectors = new ArrayList<>();
    }

    private void defineRotor(int id, String source, String target, int notch) {
        notch--;
        if(notch < 0 || notch >= alphabet.length()) {
            throw new IllegalArgumentException(String.format("Notch value: %d is illegal index in rotor id: %d. Needs to be between %d-%d. ", notch+1, id, 1, alphabet.length()));
        }

        source = source.toUpperCase();
        target = target.toUpperCase();

        int duplicateChar = checkDuplicateInAlphaBet(source);
        if(duplicateChar >= 0) {
            throw new ArrayDuplicationsException(String.format("'Right' array alphabet of rotor %d includes duplicate letter: '%c', alphabet of rotor",id, (char)duplicateChar));
        }

        duplicateChar = checkDuplicateInAlphaBet(target);
        if(duplicateChar >= 0) {
            throw new ArrayDuplicationsException(String.format("'Left' array alphabet of rotor %d includes duplicate letter: '%c', alphabet of rotor",id, (char)duplicateChar));
        }

        if(source.length() != alphabet.length()) {
            throw new IllegalArgumentException("In rotor id: " + id +
                    " 'Right' alphabet array length isn't match to the alphabet length defined(" + alphabet.length() + ").");
        }

        if(target.length() != alphabet.length()) {
            throw new IllegalArgumentException("In rotor id: " + id +
                    " 'Left' alphabet array length isn't match to the alphabet length defined(" + alphabet.length() + ").");
        }

        checkIfStringContainsAllAlphabet(source, id, "Right");

        checkIfStringContainsAllAlphabet(target, id, "Left");

        addIdIfNotExist(id, rotorsId, "rotor", 0);

        rotors.add(new Rotor(id, notch, source.toCharArray(), target.toCharArray() ));
    }

    private void checkIfStringContainsAllAlphabet(String string, int id, String nameArrayInRotor) {
        char[] arrChars = string.toCharArray();
        for(int i=0;i<arrChars.length;i++) {
            if(alphabet.indexOf(arrChars[i]) == -1) {
                throw new IllegalArgumentException(String.format("In rotor id: %d %s array, There is a char: %c that isn't part of alphabet of the machine. ", id, nameArrayInRotor, arrChars[i]));
            }
        }
    }

    private void defineReflector(int id, byte[] partA, byte[] partB) {
        if(reflectors.size() >= MAX_REFLECTORS) {
            throw new MaximumDefinedException(MAX_REFLECTORS, "reflectors");
        }

        if(!(partA.length == partB.length && partA.length == alphabet.length()/2)) {
            throw new IllegalArgumentException("Reflector with id: " + id + " array length doesn't match alphabet length.");
        }

        HashSet<Byte> dictionary = new HashSet<>();
        if(checkDuplicationsInArray(partA, partB, dictionary)) {
            throw new ArrayDuplicationsException("Reflector id: " + id);
        }

        checkAllIndexesValid(dictionary, id);

        checkAllIndexesLocated(dictionary, id);

        addIdIfNotExist(id, reflectorsId, "reflector", MAX_REFLECTORS);

        reflectors.add(new Reflector(id,partA,partB));
    }

    private void checkAllIndexesValid(Set<Byte> dictionary, int id) {
        for (Byte b : dictionary) {
            if (b < 1 || b > alphabet.length()) {
                throw new IllegalArgumentException(String.format("Reflector id: %d array isn't valid, contains illegal index: %d.",id, b));
            }
        }
    }

    private void checkAllIndexesLocated(Set<Byte> dictionary, int id) {
        for (Byte i = 1; i <= alphabet.length(); i++) {
            if (!dictionary.contains(i)) {
                throw new IllegalArgumentException(String.format("Reflector id: %d array isn't valid, not contain index:", id ,i));
            }
        }
    }

    private boolean checkDuplicationsInArray(byte[] partA, byte[] partB, HashSet<Byte> dictionary) {
        for (Byte i : partA) {
            if (dictionary.contains(i)) {
                return true;
            }
            dictionary.add(i);
        }

        for(Byte i : partB) {
            if (dictionary.contains(i)) {
                return true;
            }
            dictionary.add(i);
        }

        return false;
    }

    private void addIdIfNotExist(int id, Set<Integer> allId, String enigmaToolString, int maxId) {
        if(allId.contains(id)) {
            throw new IdAlreadyExistsException(id, enigmaToolString);
        }

        if(id < 1) {
            throw new IllegalArgumentException("There is a " + enigmaToolString + " with id that isn't positive, Id must be positive number.");
        }

        if(enigmaToolString.equals("reflector") && id > maxId) {
            throw new IllegalArgumentException("Id of " + enigmaToolString + " can't be higher then: " + maxId);
        }

        allId.add(id);
    }

    private void buildMachine(int rotorsCount, String alphabet) {
        resetMachine();

        if(rotorsCount < MIN_ROTORS) {
            throw new MinimumDefinedException(MIN_ROTORS, "used rotors");
        }

        if(rotorsCount > MAX_ROTORS) {
            throw new MaximumDefinedException(MAX_ROTORS, "used rotors");
        }
        alphabet = reduceTabsAndEnters(alphabet);
        if( alphabet.length()% 2 == 1) {
            throw new IllegalArgumentException("Alphabet length has to be even number.");
        }

        alphabet = alphabet.toUpperCase();
        int duplicateChar = checkDuplicateInAlphaBet(alphabet);
        if(duplicateChar >= 0) {
            throw new ArrayDuplicationsException(String.format("Alphabet char '%c' appears more than once, Alphabet ",(char)duplicateChar));
        }

        this.rotorsCount = rotorsCount;
        this.alphabet = alphabet;
    }

    private String reduceTabsAndEnters(String alphabet) {
        StringBuilder res = new StringBuilder();
        char [] alpha = alphabet.toCharArray();
        for(char Char: alpha) {
            if(Char != '\n' && Char != '\t') {
                res.append(Char);
            }
        }

        return res.toString();
    }

    private int checkDuplicateInAlphaBet(String alphabet) {
        int[] countChars = new int[CHARS_MAX_NUMBER];

        for (int i = 0; i < alphabet.length();  i++) {
            countChars[alphabet.charAt(i)]++;
        }

        for (int i = 0; i < CHARS_MAX_NUMBER; i++) {
            if (countChars[i] > 1) {
                return i;
            }
        }

        return -1;
    }
}
