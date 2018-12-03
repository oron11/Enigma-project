package decryption;

import enigma.emachine.Rotor;

import java.util.ArrayList;
import java.util.List;

public class Permute {
        private List<Rotor> items;
        private List<List<Rotor>> permutations = new ArrayList<>();

        public Permute(List<Rotor> data) {
            items = data;
        }

        private void _permute(List<Rotor> permutation, List<Rotor> data) {
            if (data.size() <= 0) {
                permutations.add(permutation);
                return;
            }

            for ( Rotor datum : data ) {
                List<Rotor> remnants = new ArrayList<Rotor>(data);
                remnants.remove(datum);
                List<Rotor> elements = new ArrayList<Rotor>(permutation);
                elements.add(datum);
                _permute(elements, remnants);
            }
        }

        public List<List<Rotor>> permute() {
            List<Rotor> permutation = new ArrayList<Rotor>();
            _permute(permutation, items);
            return permutations;
        }

        public void print() {
            for ( List<Rotor> permutation : permutations ) {
                Rotor last = permutation.remove(permutation.size() - 1);

                for ( Rotor element : permutation ) {
                    System.out.print(element.getId() + ", ");
                }

                System.out.println(last.getId());
            }
        }
}
