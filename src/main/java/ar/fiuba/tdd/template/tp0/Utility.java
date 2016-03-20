package ar.fiuba.tdd.template.tp0;

import java.util.List;
import java.util.Random;

class Utility {
    static boolean isQuantifier(char character) {
        return ( character == '*' || character == '?' || character == '+' );
    }

    static int getRandomBetween(int infInclusive, int supInclusive) {
        Random random = new Random();
        return infInclusive + random.nextInt(supInclusive - infInclusive + 1);
    }

    static int getRandomBetweenWithOut(int infInclusive, int supInclusive, List<Integer> numbersToFilter) {
        int random = getRandomBetween(infInclusive, supInclusive);
        while ( numbersToFilter.contains(random) ) {
            random = getRandomBetween(infInclusive, supInclusive);
        }
        return random;
    }

}
