package enigma.emachine;

import java.io.Serializable;
import java.util.NoSuchElementException;
import java.util.Objects;

public class Reflector implements Serializable {
    private final int id;
    private byte[] reflectArr;

    //Debug:
    private byte[] partA;
    private byte[] partB;

  /* public static void main(String[] args){
        Reflector ref = new Reflector(1,new byte[]{1,3,5}, new byte[]{6,4,2});
        int res = ref.process(0);
        res = ref.process(2);
        res = ref.process(3);
        res = ref.process(4);
        res = ref.process(5);
        res = ref.process(1);
    }*/

    public Reflector(int id, byte[] partA, byte[] partB) {
        this.id = id;
        makeArray(partA, partB);
        this.partA = partA;
        this.partB = partB;
    }

    public Reflector clone() {
        return new Reflector(id, partA.clone(), partB.clone());
    }

    private void makeArray(byte[] partA, byte[] partB) {
        reflectArr = new byte[partA.length * 2];
        for(int i = 0; i < partA.length; i++) {
            byte numToArr = partA[i] < partB[i] ? partA[i] : partB[i];
            reflectArr[partA[i] - 1] = numToArr;
            reflectArr[partB[i] - 1] = numToArr;
        }
    }

    public int getId() {
        return id;
    }

    public byte[] getPartA() { return partA; }

    public byte[] getPartB() { return partB; }

    public int process(int index) throws IndexOutOfBoundsException{
        if(index < 0 || index >= reflectArr.length) {
            throw new IndexOutOfBoundsException("Index " + index + " of reflector number: " + id +" is out of range, Needs to be between 1-" + String.valueOf(reflectArr.length - 1));
        }

        for(int i = 0; i < reflectArr.length; i++) {
            if(i != index && reflectArr[i] == reflectArr[index]) {
                return i;
            }
        }

        throw new NoSuchElementException("Couldn't find the index to in the reflector");
    }

    public static int getIntId(String id){
        int res;

        switch(id) {
            case "I"  : {res = 1; break; }
            case "II" : {res = 2; break; }
            case "III": {res = 3; break; }
            case "IV" : {res = 4; break; }
            case "V"  : {res = 5; break;}
            default:  throw new NoSuchElementException("The id of the reflector: " + id + " isn't valid, not I to V in roman digits.");
        }

        return res;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Reflector reflector = (Reflector) o;
        return id == reflector.id;
    }

    @Override
    public int hashCode() {

        return Objects.hash(id);
    }
}
