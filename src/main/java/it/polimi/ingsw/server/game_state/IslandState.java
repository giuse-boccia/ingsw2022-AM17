package it.polimi.ingsw.server.game_state;

import it.polimi.ingsw.model.game_objects.Student;
import it.polimi.ingsw.model.game_objects.TowerColor;
import it.polimi.ingsw.model.game_objects.gameboard_objects.Island;

import java.util.ArrayList;
import java.util.List;

public class IslandState {
    private final List<Student> students;
    private final TowerColor towerColor;
    private final int noEntryNum;
    private final int numOfTowers;

    private IslandState(List<Student> students, TowerColor towerColor, int noEntryNum, int numOfTowers) {
        this.students = students;
        this.towerColor = towerColor;
        this.noEntryNum = noEntryNum;
        this.numOfTowers = numOfTowers;
    }

    public IslandState(Island island) {
        this(
                island.getStudents(),
                island.getTowerColor(),
                island.getNoEntryNum(),
                island.getNumOfTowers()
        );
    }

    /**
     * Loads the islands from the given saved game
     *
     * @param savedGame the saved game state
     * @return a list of islands
     */
    public static List<Island> loadIslands(SavedGameState savedGame) {
        List<Island> islands = new ArrayList<>();
        savedGame.getIslands().forEach(islandState -> islands.add(islandState.loadIsland()));
        return islands;
    }

    /**
     * Loads an Island from this IslandState
     *
     * @return the loaded island
     */
    public Island loadIsland() {
        return new Island(
                students,
                towerColor,
                noEntryNum,
                numOfTowers
        );
    }

    public List<Student> getStudents() {
        return students;
    }

    public TowerColor getTowerColor() {
        return towerColor;
    }

    public int getNoEntryNum() {
        return noEntryNum;
    }

    public int getNumOfTowers() {
        return numOfTowers;
    }
}
