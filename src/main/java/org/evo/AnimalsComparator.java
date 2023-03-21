package org.evo;

import java.util.Comparator;

public class AnimalsComparator implements Comparator<Animal> {

    @Override
    public int compare(Animal animal1, Animal animal2) {
        int result;
        if (animal1.energy==animal2.energy) {
            result=0;
        } else if (animal1.energy<animal2.energy) {
            result = -1;
        } else {
            result = 1;
        }
        if (result==0) {
            if (animal1.age==animal2.age) {
                result=0;
            } else if (animal1.age<animal2.age) {
                result = -1;
            } else {
                result = 1;
            }
        }
        if (result==0) {
            if (animal1.childNumber==animal2.childNumber) {
                result=0;
            } else if (animal1.childNumber<animal2.childNumber) {
                result = -1;
            } else {
                result = 1;
            }
        }
        return result;
    }
}
