package it.polimi.ingsw.model.game_objects.gameboard_objects;

import it.polimi.ingsw.exceptions.InvalidActionException;
import it.polimi.ingsw.exceptions.InvalidStudentException;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.Place;
import it.polimi.ingsw.model.game_objects.Student;
import it.polimi.ingsw.model.game_objects.TowerColor;

import java.util.ArrayList;
import java.util.Collection;

public class Island implements Place {

    private final Collection<Student> students;
    private TowerColor towerColor;
    private int noEntryNum;
    private int numOfTowers;

    public Island() {
        towerColor = null;
        students = new ArrayList<>();
        noEntryNum = 0;
        numOfTowers = 0;
    }

    public Island(Collection<Student> students, TowerColor towerColor, int noEntryNum, int numOfTowers) {
        this.students = students;
        this.towerColor = towerColor;
        this.noEntryNum = noEntryNum;
        this.numOfTowers = numOfTowers;
    }

    public TowerColor getTowerColor() {
        return towerColor;
    }

    @Override
    public ArrayList<Student> getStudents() {
        return new ArrayList<>(students);
    }

    @Override
    public void setStudents(ArrayList<Student> students) {
        this.students.clear();
        this.students.addAll(students);
    }

    public int getNoEntryNum() {
        return noEntryNum;
    }

    public int getNumOfTowers() {
        return numOfTowers;
    }

    public void increaseNoEntryNum() {
        noEntryNum++;
    }

    public void decreaseNoEntryNum() {
        noEntryNum--;
    }

    /**
     * Sets a new {@code TowerColor} and adds a tower if the island has none.
     * Does NOT check if the island can be merged with adjacent islands
     *
     * @param towerColor the {@code TowerColor} which will own the Island
     */
    public void setTowerColor(TowerColor towerColor) {
        if (towerColor == this.towerColor) return;
        this.towerColor = towerColor;
        if (numOfTowers == 0) {
            numOfTowers = 1;
        }
    }

    /**
     * Gets every {@code Student}, tower and noEntry tile from another {@code Island}
     *
     * @param other the {@code Island} which the method gets everything from
     */
    public void mergeWith(Island other) {
        this.students.addAll(other.students);
        this.noEntryNum += other.noEntryNum;
        this.numOfTowers += other.numOfTowers;
    }

    @Override
    public void giveStudent(Place destination, Student student) throws InvalidStudentException, InvalidActionException {
        if (student == null || !students.contains(student)) {
            throw new InvalidStudentException("This island doesn't contain this student");
        }
        students.remove(student);
        destination.receiveStudent(student);
    }

    @Override
    public void receiveStudent(Student student) {
        students.add(student);
    }
}
