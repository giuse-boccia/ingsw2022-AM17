package it.polimi.ingsw.model.game_objects.gameboard_objects;

import it.polimi.ingsw.utils.constants.Constants;
import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.characters.*;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.model.game_objects.Student;
import it.polimi.ingsw.model.game_objects.TowerColor;
import it.polimi.ingsw.model.utils.Students;

import java.util.*;
import java.util.stream.Collectors;

public class GameBoard {
    private final Game game;
    private Bag bag;
    private List<Island> islands;
    private List<Cloud> clouds;
    private Character[] characters;
    private Map<Color, Player> professors;
    private int motherNatureIndex;

    public GameBoard(Game game) {
        this.game = game;
        bag = new Bag();
    }

    public void initGameBoard(int motherNatureIndex) {
        this.motherNatureIndex = motherNatureIndex;
        this.professors = initProfessorMap();
        this.clouds = initClouds();
        this.islands = initIslands();
        this.characters = initCharacters();
    }

    public void setBag(Bag bag) {
        this.bag = bag;
    }

    public void setIslands(List<Island> islands) {
        this.islands = islands;
    }

    public void setClouds(List<Cloud> clouds) {
        this.clouds = clouds;
    }

    public void setProfessors(Map<Color, Player> professors) {
        this.professors = professors;
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
        allCharacters.add(new MovingCharacter(CharacterName.move1FromCardToDining, this, 4, 1));

        allCharacters.add(new PassiveCharacter(CharacterName.plus2MNMoves));
        allCharacters.add(new PassiveCharacter(CharacterName.takeProfWithEqualStudents));
        allCharacters.add(new PassiveCharacter(CharacterName.plus2Influence));
        allCharacters.add(new PassiveCharacter(CharacterName.ignoreTowers));
        allCharacters.add(new PassiveCharacter(CharacterName.ignoreColor));

        Collections.shuffle(allCharacters);

        for (int i = 0; i < 3; i++) {
            res[i] = allCharacters.get(i);
            if (
                    res[i].getCardName() == CharacterName.move1FromCardToIsland ||
                            res[i].getCardName() == CharacterName.move1FromCardToDining ||
                            res[i].getCardName() == CharacterName.swapUpTo3FromEntranceToCard
            ) {
                MovingCharacter mc = (MovingCharacter) res[i];
                try {
                    mc.fillCardFromBag();
                } catch (EmptyBagException e) {
                    e.printStackTrace();
                }
            }
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
        for (int i = 0; i < Constants.MAX_ISLANDS; i++) {
            Island newIsland = new Island();
            if (i != motherNatureIndex && i != ((motherNatureIndex + (Constants.MAX_ISLANDS / 2)) % Constants.MAX_ISLANDS)) {
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
            result.add(new Cloud(numPlayers % 2 == 0 ? Constants.STUDENTS_ON_CLOUD_IN_TWO_OR_FOUR_PLAYER_GAME : Constants.STUDENTS_ON_CLOUD_IN_THREE_PLAYER_GAME));
        }
        return result;
    }

    /**
     * Initializes professors to start the game
     *
     * @return a {@code Map} with the five colors as keys and 5 null as values
     */
    private Map<Color, Player> initProfessorMap() {
        Map<Color, Player> res = new HashMap<>();
        for (Color c : Color.values()) {
            res.put(c, null);
        }
        return res;
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
     * Sets a new {@code towerColor} for the given {@code Island} and checks if that island can now be merged with adjacent ones
     *
     * @param island        the {@code Island} which owner has to be changed
     * @param newTowerColor the {@code TowerColor} of the team which will own the Island
     */
    public void setIslandOwner(Island island, TowerColor newTowerColor) {
        island.setTowerColor(newTowerColor);
        mergeIfPossible(island);
    }

    /**
     * Checks if the given {@code Island} can be merged with adjacent ones
     *
     * @param island the {@code Island} to be checked
     */
    private void mergeIfPossible(Island island) {
        int indexOfIsland = island.getIndexIn(islands);
        Island right = islands.get((indexOfIsland + 1) % islands.size());
        Island left = islands.get((indexOfIsland + islands.size() - 1) % islands.size());
        if (left.getTowerColor() != null && left.getTowerColor() == island.getTowerColor()) {
            if (right.getTowerColor() != null && right.getTowerColor() == island.getTowerColor()) {
                mergeIslands(left, island, right);
            } else {
                mergeIslands(left, island);
            }
        } else if (right.getTowerColor() != null && right.getTowerColor() == island.getTowerColor())
            mergeIslands(island, right);

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
            motherNatureIndex = left.getIndexIn(islands);
        } else {
            motherNatureIndex = mnIsland.getIndexIn(islands);
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

    public void setCharacters(Character[] characters) {
        this.characters = characters;
    }

    public int getMotherNatureIndex() {
        return motherNatureIndex;
    }

    public void setMotherNatureIndex(int motherNatureIndex) {
        this.motherNatureIndex = motherNatureIndex;
    }

    public Game getGame() {
        return game;
    }

    public Bag getBag() {
        return bag;
    }

    public Map<Color, Player> getProfessorsMap() {
        return professors;
    }

    /**
     * Returns the {@code Player} who owns the professor of the given color or {@code null} if nobody owns the professor
     *
     * @param color the color of the professor to check
     * @return {@code null} if nobody owns the professor, or the {@code Player} who owns the professor
     */
    public Player getOwnerOfProfessor(Color color) {
        return professors.get(color);
    }

    /**
     * Sets a new owner for the professor of the given color
     *
     * @param color  the color of the professor
     * @param player the new owner of the professor
     */
    public void setOwnerOfProfessor(Color color, Player player) {
        professors.put(color, player);

    }

    /**
     * Given a {@code Player}, returns a {@code List} of the colors of the professors which he/she owns
     *
     * @param player the {@code Player} to check the owned professors of
     * @return a {@code List} of the colors of which that {@code Player} owns the professor of
     */
    public List<Color> getColorsOfOwnedProfessors(Player player) {
        return professors.keySet().stream()
                .filter(color -> professors.get(color) == player)
                .collect(Collectors.toList());
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        GameBoard gameBoard = (GameBoard) o;

        if (motherNatureIndex != gameBoard.motherNatureIndex) return false;
        if (!bag.equals(gameBoard.bag)) return false;
        if (!islands.equals(gameBoard.islands)) return false;
        if (!clouds.equals(gameBoard.clouds)) return false;
        if (!Arrays.equals(characters, gameBoard.characters)) return false;
        return professors.equals(gameBoard.professors);
    }
}
