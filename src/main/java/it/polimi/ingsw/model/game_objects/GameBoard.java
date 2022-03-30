package it.polimi.ingsw.model.game_objects;

import it.polimi.ingsw.exceptions.ProfessorAlreadyPresentException;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.character.*;
import it.polimi.ingsw.model.character.Character;
import it.polimi.ingsw.model.game_objects.dashboard_objects.ProfessorRoom;
import it.polimi.ingsw.model.utils.Students;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
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

    /**
     * Initializes characters to start the game
     *
     * @return an {@code Array} of characters
     */
    private Character[] initCharacters() {
        Character[] res = new Character[3];
        ArrayList<Character> allCharacters = new ArrayList<>();

        allCharacters.add(new ResolveIslandCharacter(CharacterName.resolveIsland, this));

        allCharacters.add(new NoEntryCharacter(CharacterName.noEntry, this));
        allCharacters.add(new EveryOneMovesCharacter(CharacterName.everyOneMove3FromDiningRoomToBag, this));

        allCharacters.add(new MovingCharacter(CharacterName.move1FromCardToIsland, this, 4, 1));
        allCharacters.add(new MovingCharacter(CharacterName.swapUpTo2FromEntranceToDiningRoom, this, 0, 2));
        allCharacters.add(new MovingCharacter(CharacterName.swapUpTo3FromEntranceToCard, this, 6, 3));
        allCharacters.add(new MovingCharacter(CharacterName.move1FromCardToDining, this, 1, 1));

        allCharacters.add(new PassiveCharacter(CharacterName.plus2MNMoves));
        allCharacters.add(new PassiveCharacter(CharacterName.takeProfWithEqualStudents));
        allCharacters.add(new PassiveCharacter(CharacterName.plus2Influence));
        allCharacters.add(new PassiveCharacter(CharacterName.ignoreTowers));
        allCharacters.add(new PassiveCharacter(CharacterName.ignoreColor));

        Collections.shuffle(allCharacters);

        for (int i = 0; i < 3; i++) {
            res[i] = allCharacters.get(i);
        }

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
        if (left.getOwner() != null && left.getOwner() == island.getOwner()) {
            if (right.getOwner() != null && right.getOwner() == island.getOwner()) {
                mergeIslands(left, island, right);
            } else {
                mergeIslands(left, island);
            }
        } else if (right.getOwner() != null && right.getOwner() == island.getOwner())
            mergeIslands(island, right);

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
     */
    private void mergeIslands(Island left, Island right) {
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
    private void mergeIslands(Island first, Island second, Island third) {
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
