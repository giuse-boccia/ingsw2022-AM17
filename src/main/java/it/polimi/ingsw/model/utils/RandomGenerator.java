package it.polimi.ingsw.model.utils;

import java.util.Random;

public class RandomGenerator {

    private final Random random;

    public RandomGenerator(int seed) {
        random = new Random(seed);
    }

    /**
     * Returns a random value between 0 (inclusive) and bound (exclusive)
     *
     * @param bound an integer
     * @return a random value chosen between 0 and bound-1
     */
    public int getRandomInteger(int bound) {
        return random.nextInt(bound);
    }

}
