package enigma.emachine;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public class Secret implements Serializable{
    private List<Rotor> rotors;
    private List<Character> rotorsStartPosition;
    private Reflector reflector;

    //For statistics:
    private List<String> allInputs;
    private List<String> allOutputs;
    private List<Long> allProcessNanoTime;

    public Secret(List<Rotor> secretRotors, Reflector secretReflector) {
        this.rotors = secretRotors;
        this.reflector = secretReflector;
        allInputs = new ArrayList<>();
        allOutputs = new ArrayList<>();
        allProcessNanoTime = new ArrayList<>();
        makeArrayOfRotorsStartPosition();
    }

    public Secret (List<Rotor> secretRotors, Reflector secretReflector, boolean flagNoStatistics) {
        this.rotors = secretRotors;
        this.reflector = secretReflector;
    }

    public Secret clone() {
        List<enigma.emachine.Rotor> rotors = new ArrayList<>(this.rotors.size());
        for(Rotor r : this.rotors) {
            rotors.add(r.clone());
        }

        Reflector reflector = this.reflector.clone();

        return new Secret(rotors, reflector, true);
    }

    public List<Rotor> getRotors() {
        return rotors;
    }

    public void setRotorsPositionAccordingToIndex(long index, String alphabet) {
        int alphabetLength = alphabet.length();
        int power = 0;
        rotorsStartPosition = new ArrayList<>(rotors.size());

        for(Rotor rotor : rotors) {
             int position = (int)((index / (long)Math.pow(alphabetLength, power)) % alphabetLength);
             power++;
             rotor.setRotorsPositionAccordingToIndex(alphabet.charAt(position));
             rotorsStartPosition.add(alphabet.charAt(position));
        }
    }

    private void makeArrayOfRotorsStartPosition() {
        rotorsStartPosition = new ArrayList<>(rotors.size());
        for(Rotor rotor : rotors) {
            rotorsStartPosition.add(rotor.getStartSecretChar());
        }
    }

    public int process(int index) {
        spinRotors();
        for(Rotor rotor : rotors) {
            index = rotor.process(index, false);
        }
        index = reflector.process(index);
        for(int i = rotors.size() - 1; i >= 0; i--) {
            index = rotors.get(i).process(index, true);
        }

        return index;
    }

    private void spinRotors() {
        boolean isNeedToSpinNextRotor;
        for(int i = 0; i < rotors.size(); i++) {
            isNeedToSpinNextRotor = rotors.get(i).spinRotorAndReturnIfNeedToSpinNext();
            if(isNeedToSpinNextRotor == false) {
                break;
            }
        }
    }

    public void resetSecret() {
        for(Rotor rotor : rotors) {
            rotor.resetRotorToSecretState();
        }
    }

    public String getCodeSpecification(boolean withStart) {
        StringBuilder codeSpecification = new StringBuilder();

        if(withStart) {
            codeSpecification.append("Secret code: <");
        }
        else {
            codeSpecification.append("<");
        }

        for(int i = rotors.size() - 1; i >= 0; i--) {

            codeSpecification.append(rotors.get(i).getId());
            if(i > 0) {
                codeSpecification.append(',');
            }
            else {
                codeSpecification.append('>');
            }
        }

        codeSpecification.append('<');
        for(int i = rotors.size() - 1; i >= 0; i--) {

            codeSpecification.append(rotorsStartPosition.get(i));
            if(i > 0) {
                codeSpecification.append(',');
            }
            else {
                codeSpecification.append('>');
            }
        }

        codeSpecification.append('<' + getRomanDigit(reflector.getId()) + '>');


        return codeSpecification.toString();
    }

    public static String getRomanDigit(int id){
        String res;

        switch(id) {
            case 1: {res = "I"; break; }
            case 2: {res = "II"; break; }
            case 3: {res = "III"; break; }
            case 4: {res = "IV"; break; }
            case 5: {res = "V"; break;}
            default:  throw new NoSuchElementException("There isn't id that not between 1 to 5.");
        }

        return res;
    }

    public void saveDataStatistics(String input, String output, long nanoTime) {
        allInputs.add(input);
        allOutputs.add(output);
        allProcessNanoTime.add(nanoTime);
    }

    public List<String> getStatisticsDataOfSecretCode() {
        List<String> secretData = new ArrayList<>();
        secretData.add(getCodeSpecification(true));

        if(allInputs.size() == 0) {
            secretData.add("This secret code didn't processed any inputs from user");
            return secretData;
        }

        for(int i = 0; i< allInputs.size(); i++) {
            secretData.add(String.format("%d. %s %s %s",i + 1, allInputs.get(i), allOutputs.get(i), String.valueOf(allProcessNanoTime.get(i))));
        }

        return secretData;
    }
}
