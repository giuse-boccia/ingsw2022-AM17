package it.polimi.ingsw.utils;

import it.polimi.ingsw.languages.MessageResourceBundle;
import it.polimi.ingsw.model.utils.RandomGenerator;

import java.util.Random;

public class RandomNicknameGenerator {

    public static final String[] animals = {
            MessageResourceBundle.getMessage("racoon"), MessageResourceBundle.getMessage("warthog"),
            MessageResourceBundle.getMessage("sloth"), MessageResourceBundle.getMessage("canary"),
            MessageResourceBundle.getMessage("turtle"), MessageResourceBundle.getMessage("horse"),
            MessageResourceBundle.getMessage("panther"), MessageResourceBundle.getMessage("hen"),
            MessageResourceBundle.getMessage("snake"), MessageResourceBundle.getMessage("trout"),
            MessageResourceBundle.getMessage("gecko"), MessageResourceBundle.getMessage("tuna"),
            MessageResourceBundle.getMessage("shark")
    };
    public static final String[] adjectives = {
            MessageResourceBundle.getMessage("commanding"), MessageResourceBundle.getMessage("scrappy"),
            MessageResourceBundle.getMessage("soporific"), MessageResourceBundle.getMessage("caring"),
            MessageResourceBundle.getMessage("distracted"), MessageResourceBundle.getMessage("greedy"),
            MessageResourceBundle.getMessage("wise"), MessageResourceBundle.getMessage("empathetic"),
            MessageResourceBundle.getMessage("overwhelming"), MessageResourceBundle.getMessage("diligent"),
            MessageResourceBundle.getMessage("dynamic"), MessageResourceBundle.getMessage("brave"),
            MessageResourceBundle.getMessage("skilled")
    };

    /**
     * Returns a random nickname for a player that chooses this option or that has provided an empty username
     *
     * @return a random nickname
     */
    public static String getRandomNickname() {
        RandomGenerator randomGenerator = new RandomGenerator(new Random().nextInt());
        String adjective = adjectives[randomGenerator.getRandomInteger(adjectives.length)];
        String animal = animals[randomGenerator.getRandomInteger(animals.length)];
        if (MessageResourceBundle.getCurrentLanguageTag().equals("it")) {
            if (animal.endsWith("a") && adjective.endsWith("o")) {
                adjective = adjective.substring(0, adjective.length() - 1) + "a";
            }
            return Character.toUpperCase(animal.charAt(0)) + animal.substring(1) + " " + adjective.toLowerCase();
        }
        return adjective + " " + animal;
    }

}
