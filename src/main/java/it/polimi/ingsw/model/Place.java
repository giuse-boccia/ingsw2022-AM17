package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.InvalidActionException;
import it.polimi.ingsw.exceptions.InvalidStudentException;
import it.polimi.ingsw.model.game_objects.Student;

import java.util.ArrayList;

public interface Place {
    /**
     * Transfers a student to the destination. The student will no longer be present in the object which this method
     * is called on.
     *
     * @param destination Place which the method gives the student to
     * @param student     The student given to the destination
     */
    void giveStudent(Place destination, Student student) throws InvalidStudentException, InvalidActionException;

    /**
     * Adds a student to the object which this method is called on.
     *
     * @param student The student to be received
     */
    void receiveStudent(Student student) throws InvalidActionException;

    ArrayList<Student> getStudents();

    void setStudents(ArrayList<Student> students);
}
