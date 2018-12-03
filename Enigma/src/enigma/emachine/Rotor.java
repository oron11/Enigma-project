package enigma.emachine;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.Objects;

public class Rotor implements Serializable {
    private final int id;
    private int notchIndex;
    private char[] source;
    private char[] target;
    private char startSecretChar;

 /*   public static void main(String[] args) {
        Boolean check = false;
        char[] a = {'a','b','c','d', 'e' , 'f'};
        char[] b = {'f', 'e', 'd', 'c', 'b', 'a'};
        Rotor r1 = new Rotor(1,3,a, b);
        r1.setRotorToSecretCodePosition(3);
        r1.spinRotor(check);
        r1.spinRotor(check);
        int to = r1.process(2);
        to = r1.process(3);
        r1.resetRotorToSecretState();
    }*/

    public Rotor(int id, int notchIndex, char[] source, char[] target) {
        this.id = id;
        this.notchIndex = notchIndex;
        this.source = source;
        this.target = target;
    }

    public Rotor clone() {
        return new Rotor(id, notchIndex, source.clone(), target.clone());
    }

    public int getId() {
        return id;
    }

    public int getNotchIndex() { return notchIndex + 1;}

    public String getSource() {return String.valueOf(source);}

    public String getTarget() {return String.valueOf(target);}

    public char getStartSecretChar() { return startSecretChar; }

    public void setRotorsPositionAccordingToIndex(char charIndex) {
        int index = returnIndexOfChar(source, charIndex);
        spinAccordingToPosition(index);
    }

    public int process(int index, boolean isReverseSide) {
        if(index < 0 || index >= source.length) {
            throw new IndexOutOfBoundsException("Index " + index + " of rotor id: " + id + " is out of range, Needs to be between 1-" + source.length);
        }

        if(isReverseSide) {
            char fromSource = target[index];
            return returnIndexOfChar(source, fromSource);
        }
        else {
            char fromSource = source[index];
            return returnIndexOfChar(target, fromSource);
        }
    }

    private int returnIndexOfChar(char[] arr, char c) throws NoSuchElementException{
        boolean found = false;
        int index;

        for(index = 0; index < arr.length; index++) {
            if(arr[index] == c) {
                found = true;
                break;
            }
        }
        if(!found) {
            throw new NoSuchElementException("Char: '" + c + "' isn't found in alphabet: " + String.valueOf(arr) + " in rotor Id: " + id);
        }

        return index;
    }

    private void spinAccordingToPosition(int secretPosition) {
        for(int i = 0; i < secretPosition ;i++){
            spinRotorAndReturnIfNeedToSpinNext();
        }
    }

    public void setRotorToSecretCodePosition(int secretPosition) throws IndexOutOfBoundsException {
        if(secretPosition <= 0 || secretPosition > source.length) {
            throw new IndexOutOfBoundsException("Index: " + secretPosition + " of the rotor id: " + id + " in the secret code is out of range, Needs to be between 1-" + source.length);
        }

        startSecretChar = source[secretPosition - 1];
        spinAccordingToPosition(secretPosition - 1);
    }

    public void setRotorToSecretCodePosition(char secretChar) {
        int index = returnIndexOfChar(source, secretChar);
        startSecretChar = secretChar;
        spinAccordingToPosition(index);
    }

    public void resetRotorToSecretState() {
        setRotorToSecretCodePosition(startSecretChar);
    }

    public boolean spinRotorAndReturnIfNeedToSpinNext() {
        spinArray(source);
        spinArray(target);

        if(notchIndex == 0) {
            notchIndex = source.length - 1;
        }
        else {
            notchIndex--;
            if(notchIndex == 0) {
                return true;
            }
        }

        return false;
    }

    private void spinArray(char[] arr) {
        char first = arr[0];

        for(int i = 0; i < arr.length - 1; i++) {
            arr[i] = arr[i+1];
        }

        arr[arr.length - 1] = first;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Rotor rotor = (Rotor) o;
        return id == rotor.id;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
