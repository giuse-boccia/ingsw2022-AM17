package it.polimi.ingsw.model.game_objects.gameboard_objects;

import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.model.Place;
import it.polimi.ingsw.model.game_objects.Student;
import it.polimi.ingsw.model.game_objects.dashboard_objects.Entrance;

import java.util.ArrayList;

public class Cloud implements Place {
    private final ArrayList<Student> students;
    private final int maxStudents;

    public Cloud(int maxStudents) {
        students = new ArrayList<>();
        this.maxStudents = maxStudents;
    }

    /**
     * Give the students contained in the Cloud to the destination
     * @param destination the Entrance which the Cloud gives students to
     */
    public void emptyTo(Entrance destination) {
        for (int i = 0; i < students.size(); ) {
            giveStudent(destination, students.get(i));
        }
    }

    /**
     * Fills the {@code Cloud} from the {@code Bag}
     *
     * @param bag Bag used in the game
     */
    public void fillFromBag(Bag bag) throws EmptyBagException {
        while (students.size() < maxStudents) {
            bag.giveStudent(this, bag.getRandStudent());
        }
    }

    /**
     * Checks if the {@code Bag} is empty
     *
     * @return true if the {@code Bag} is empty
     */
    public boolean isEmpty() {
        return students.size() == 0;
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

    @Override
    public ArrayList<Student> getStudents() {
        return new ArrayList<>(students);
    }

    @Override
    public void setStudents(ArrayList<Student> students) {
        this.students.clear();
        this.students.addAll(students);
    }
}
