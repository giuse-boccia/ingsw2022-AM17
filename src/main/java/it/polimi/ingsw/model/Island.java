package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.Collection;

public class Island implements Place {

    private Player owner;
    private final Collection<Student> students;
    private int noEntryNum;
    private int numOfTowers;
    private final GameBoard gameboard;

    public Island(GameBoard gameboard) {
        owner = null;
        students = new ArrayList<>();
        noEntryNum = 0;
        numOfTowers = 0;
        this.gameboard = gameboard;
    }

    public Player getOwner() {
        return owner;
    }

    public ArrayList<Student> getStudents() {
        return new ArrayList<>(students);
    }

    public int getNoEntryNum() {
        return noEntryNum;
    }

    public int getNumOfTowers() {
        return numOfTowers;
    }


    public void setOwner(Player owner) {
        if (owner == this.owner) return;
        this.owner = owner;
        if(numOfTowers == 0){
            numOfTowers = 1;
        }
        mergeIfPossible();
    }

    private void mergeIfPossible(){
        ArrayList<Island> islands = gameboard.getIslands();
        int indexOfIsland = islands.indexOf(this);
        Island right = islands.get((indexOfIsland + 1) % islands.size());
        Island left = islands.get((indexOfIsland + islands.size() - 1) % islands.size());
        try {
            if (left.owner != null && left.owner == owner) {
                if (right.owner != null && right.owner == owner) {
                    gameboard.mergeIslands(left, this, right);
                } else {
                    gameboard.mergeIslands(left, this);
                }
            } else if(right.owner != null && right.owner == owner)
                gameboard.mergeIslands(this, right);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(e.getMessage());
        }
    }

    /**
     * Gets every {@code Student}, tower and noEntry tile from another {@code Island}
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
