package it.polimi.ingsw.server.game_state;

import it.polimi.ingsw.model.game_objects.Student;
import it.polimi.ingsw.model.game_objects.TowerColor;
import it.polimi.ingsw.model.game_objects.gameboard_objects.Island;

import java.util.Collection;
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
