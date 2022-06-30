package it.polimi.ingsw.model.game_objects.gameboard_objects;

import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.exceptions.InvalidActionException;
import it.polimi.ingsw.model.Place;
import it.polimi.ingsw.model.game_objects.Student;
import it.polimi.ingsw.model.utils.Students;
import it.polimi.ingsw.utils.constants.Constants;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A bag initially containing 120 students.
 * The bag is shuffled at its creation and every time it receives a new {@code Student}
 */
public class Bag implements Place {
    private final List<Student> students;

    public Bag() {
        this.students = Students.getSomeStudents(Constants.INITIAL_STUDENTS_IN_BAG_OF_EACH_COLOR);
    }

    public Bag(List<Student> students) {
        this.students = students;
    }

    /**
     * @return a random {@code Student} of the collection
     */
    public Student getRandStudent() throws EmptyBagException {
        if (isEmpty()) throw new EmptyBagException("empty_bag");
        return students.get(0);
    }

    public boolean isEmpty() {
        return students.size() == 0;
    }

    @Override
    public void giveStudent(Place destination, Student student) throws InvalidActionException {
        students.remove(student);
        destination.receiveStudent(student);
    }

    @Override
    public void receiveStudent(Student student) {
        students.add(student);
        Collections.shuffle(students);
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Bag bag = (Bag) o;

        return students.equals(bag.students);
    }
}
