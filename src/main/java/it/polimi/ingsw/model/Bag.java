package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * A bag initially containing 120 students.
 * The bag is shuffled at its creation and every time it receives a new {@code Student}
 */
public class Bag implements Place{
    private final ArrayList<Student> students;

    public Bag(){
        this.students = Students.getSomeStudents(24);
    }

    /**
     * @return a random {@code Student} of the collection
     */
    public Student getRandStudent(){
        return students.get(0);
    }

    @Override
    public void giveStudent(Place destination, Student student) {
        students.remove(student);
        destination.receiveStudent(student);
    }

    @Override
    public void receiveStudent(Student student) {
        students.add(student);
        Collections.shuffle(students);
    }
}
