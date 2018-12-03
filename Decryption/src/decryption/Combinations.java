package decryption;

import enigma.emachine.Rotor;

import java.util.Arrays;
import java.util.List;

public class Combinations {

    private static int positionNextArray;
    private static Rotor[][] result;

    private static void combinationUtil(List<Rotor> arr, Rotor[] currentOptionArr, int start,
                                int end, int index, int r)
    {
        if (index == r)
        {
            result[positionNextArray] = Arrays.copyOf(currentOptionArr, currentOptionArr.length);
            positionNextArray++;
            return;
        }

        for (int i=start; i<=end && end-i+1 >= r-index; i++)
        {
            currentOptionArr[index] = arr.get(i);
            combinationUtil(arr, currentOptionArr, i+1, end, index+1, r);
        }
    }

    public static Rotor[][] getAllSubGroupRotorsCombinations(List<Rotor> rotors, int sizeSubGroup, int sizeOfOptions) {
        result = new Rotor[sizeOfOptions][sizeSubGroup];
        positionNextArray = 0;
        combinationUtil(rotors, new Rotor[sizeSubGroup], 0, rotors.size() - 1, 0, sizeSubGroup);

      //  print();
        return result;
    }

    public static void print() {
        for ( Rotor[] rotorsOptions: result ) {
            for ( Rotor element : rotorsOptions ) {
                System.out.print(element.getId() + ", ");
            }

            System.out.println();
        }
    }
}
