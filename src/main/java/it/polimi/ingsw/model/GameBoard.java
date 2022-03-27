package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.NotAdjacentIslandsException;
import it.polimi.ingsw.exceptions.ProfessorAlreadyPresentException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

public class GameBoard {
    private final ArrayList<Island> islands;
    private final ArrayList<Cloud> clouds;
    private final Character[] characters;
    private final Game game;
    private final Bag bag;
    private final ProfessorRoom startingProfessors;
    private int motherNatureIndex;

    public GameBoard(Game game) {
        this.game = game;
        bag = new Bag();
        motherNatureIndex = new Random().nextInt(12);
        this.startingProfessors = initProfessors();
        this.clouds = initClouds();
        this.islands = initIslands();
        this.characters = initCharacters();
    }

    private Character[] initCharacters() {
        Character[] res = new Character[3];
        ArrayList<Character> allCharacters = new ArrayList<>();

        // TODO factory character
        allCharacters.add(new ActiveCharacter(CardName.resolveIsland));
        allCharacters.add(new NoEntryCharacter(CardName.noEntry));
        // creates other character and shuffle
        return res;
    }

    /**
     * Initializes islands to start the game
     *
     * @return an {@code ArrayList} of islands, each one with a student, for a total of two students per color
     */
    private ArrayList<Island> initIslands() {
        ArrayList<Island> result = new ArrayList<>();

        ArrayList<Student> students = Students.getSomeStudents(2);
        for (int i = 0; i < 12; i++) {
            Island newIsland = new Island();
            if (i != motherNatureIndex && i != ((motherNatureIndex + 6) % 12)) {
                newIsland.receiveStudent(students.get(0));
                students.remove(0);
            }
            result.add(newIsland);
        }

        return result;
    }

    /**
     * Initializes clouds to start the game
     *
     * @return an {@code ArrayList} of empty clouds, each one of the desired size
     */
    private ArrayList<Cloud> initClouds() {
        ArrayList<Cloud> result = new ArrayList<>();
        int numPlayers = game.getPlayers().size();
        for (int i = 0; i < numPlayers; i++) {
            result.add(new Cloud(numPlayers % 2 == 0 ? 3 : 4));
        }
        return result;
    }

    /**
     * Initializes professors to start the game
     *
     * @return a {@code ProfessorRoom} with one professor for each color
     */
    private ProfessorRoom initProfessors() {
        ProfessorRoom result = new ProfessorRoom();

        for (Color color : Color.values()) {
            try {
                result.takeProfessor(new Professor(color));
            } catch (ProfessorAlreadyPresentException e) {
                e.printStackTrace();
                System.out.println(e.getMessage());
            }
        }

        return result;
    }

    /**
     * Moves mother nature of the given number of steps
     *
     * @param steps the number of steps which mother nature is moved of
     */
    public void moveMotherNature(int steps) {
        motherNatureIndex = (motherNatureIndex + steps) % islands.size();
    }

    /**
     * Sets a new owner for the given {@code Island} and checks if that island can now be merged with adjacent ones
     *
     * @param island   the {@code Island} which owner has to be changed
     * @param newOwner the {@code Player} which will own the Island
     */
    public void setIslandOwner(Island island, Player newOwner) {
        island.setOwner(newOwner);
        mergeIfPossible(island);
    }

    /**
     * Checks if the given {@code Island} can be merged with adjacent ones
     *
     * @param island the {@code Island} to be checked
     */
    private void mergeIfPossible(Island island) {
        int indexOfIsland = islands.indexOf(island);
        Island right = islands.get((indexOfIsland + 1) % islands.size());
        Island left = islands.get((indexOfIsland + islands.size() - 1) % islands.size());
        try {
            if (left.getOwner() != null && left.getOwner() == island.getOwner()) {
                if (right.getOwner() != null && right.getOwner() == island.getOwner()) {
                    mergeIslands(left, island, right);
                } else {
                    mergeIslands(left, island);
                }
            } else if (right.getOwner() != null && right.getOwner() == island.getOwner())
                mergeIslands(island, right);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    /**
     * Checks if two islands are adjacent
     *
     * @param first  the first {@code Island}
     * @param second the second {@code Island}
     * @return true if the two islands are adjacent
     */
    private boolean areIslandsAdjacent(Island first, Island second) {
        int firstIndex = islands.indexOf(first);
        int secondIndex = islands.indexOf(second);
        int check = (secondIndex - firstIndex + islands.size()) % islands.size();
        return check == 1 || check == islands.size() - 1;
    }

    /**
     * Merges two islands and moves mother nature accordingly. The islands must be adjacent
     *
     * @param left  the first {@code Island} to be merged
     * @param right the second {@code Island} to be merged
     * @throws NotAdjacentIslandsException if the two islands are not adjacent
     */
    private void mergeIslands(Island left, Island right) throws NotAdjacentIslandsException {
        if (!areIslandsAdjacent(left, right)) throw new NotAdjacentIslandsException("Islands are not adjacent");

        Island mnIsland = islands.get(motherNatureIndex);

        left.mergeWith(right);
        islands.remove(right);

        // Move mother nature accordingly
        if (mnIsland == right) {
            motherNatureIndex = islands.indexOf(left);
        } else {
            motherNatureIndex = islands.indexOf(mnIsland);
        }

    }

    /**
     * Merges three islands and moves mother nature accordingly
     *
     * @param first  the first {@code Island} to be merged
     * @param second the second {@code Island} to be merged
     * @param third  the third {@code Island} to be merged
     */
    private void mergeIslands(Island first, Island second, Island third) throws NotAdjacentIslandsException {
        if (!areIslandsAdjacent(first, second) || !areIslandsAdjacent(second, third))
            throw new NotAdjacentIslandsException("Islands are not adjacent");

        mergeIslands(first, second);
        mergeIslands(first, third);
    }

    public ArrayList<Island> getIslands() {
        return new ArrayList<>(islands);
    }

    public ArrayList<Cloud> getClouds() {
        return new ArrayList<>(clouds);
    }

    public Character[] getCharacters() {
        return Arrays.copyOf(characters, characters.length);
    }

    public int getMotherNatureIndex() {
        return motherNatureIndex;
    }

    public Game getGame() {
        return game;
    }

    public Bag getBag() {
        return bag;
    }

    public ProfessorRoom getStartingProfessors() {
        return startingProfessors;
    }


}
