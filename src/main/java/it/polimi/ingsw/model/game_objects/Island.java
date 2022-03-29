package it.polimi.ingsw.model.game_objects;

import it.polimi.ingsw.model.Player;

import java.util.ArrayList;
import java.util.Collection;

public class Island implements Place {

    private final Collection<Student> students;
    private Player owner;
    private int noEntryNum;
    private int numOfTowers;

    public Island() {
        owner = null;
        students = new ArrayList<>();
        noEntryNum = 0;
        numOfTowers = 0;
    }

    public Player getOwner() {
        return owner;
    }

    /**
     * Sets a new owner and adds a tower if the island hase none.
     * Does NOT check if the island can be merged with adjacent islands
     *
     * @param owner the {@code Player} which will own the Island
     */
    public void setOwner(Player owner) {
        if (owner == this.owner) return;
        this.owner = owner;
        if (numOfTowers == 0) {
            numOfTowers = 1;
        }
    }

    public ArrayList<Student> getStudents() {
        return new ArrayList<>(students);
    }

    public int getNoEntryNum() {
        return noEntryNum;
    }

    public void increaseNoEntryNum() {
        noEntryNum++;
    }

    public int getNumOfTowers() {
        return numOfTowers;
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
    public void giveStudent(Place destination, Student student) {
        students.remove(student);
        destination.receiveStudent(student);
    }

    @Override
    public void receiveStudent(Student student) {
        students.add(student);
    }
}
