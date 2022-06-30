package it.polimi.ingsw.utils;

import it.polimi.ingsw.languages.Messages;
import it.polimi.ingsw.model.utils.RandomGenerator;

import java.util.Random;

public class RandomNicknameGenerator {

    public static final String[] animals = {
            Messages.getMessage("racoon"), Messages.getMessage("warthog"),
            Messages.getMessage("sloth"), Messages.getMessage("canary"),
            Messages.getMessage("turtle"), Messages.getMessage("horse"),
            Messages.getMessage("panther"), Messages.getMessage("hen"),
            Messages.getMessage("snake"), Messages.getMessage("trout"),
            Messages.getMessage("gecko"), Messages.getMessage("tuna"),
            Messages.getMessage("shark")
    };
    public static final String[] adjectives = {
            Messages.getMessage("commanding"), Messages.getMessage("scrappy"),
            Messages.getMessage("soporific"), Messages.getMessage("caring"),
            Messages.getMessage("distracted"), Messages.getMessage("greedy"),
            Messages.getMessage("wise"), Messages.getMessage("empathetic"),
            Messages.getMessage("overwhelming"), Messages.getMessage("diligent"),
            Messages.getMessage("dynamic"), Messages.getMessage("brave"),
            Messages.getMessage("skilled")
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
        if (Messages.getCurrentLanguageTag().equals("it")) {
            if (animal.endsWith("a") && adjective.endsWith("o")) {
                adjective = adjective.substring(0, adjective.length() - 1) + "a";
            }
            return Character.toUpperCase(animal.charAt(0)) + animal.substring(1) + " " + adjective.toLowerCase();
        }
        return adjective + " " + animal;
    }

}
