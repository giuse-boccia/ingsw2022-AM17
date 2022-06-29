package it.polimi.ingsw.model.game_objects.dashboard_objects;

import it.polimi.ingsw.exceptions.InvalidActionException;
import it.polimi.ingsw.exceptions.InvalidStudentException;
import it.polimi.ingsw.languages.MessageResourceBundle;
import it.polimi.ingsw.model.Place;
import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.model.game_objects.Student;
import it.polimi.ingsw.model.utils.Students;
import it.polimi.ingsw.utils.constants.Constants;

import java.util.ArrayList;
import java.util.List;

public class DiningRoom implements Place {
    private final List<Student> students;

    public DiningRoom() {
        students = new ArrayList<>();
    }

    public DiningRoom(List<Student> students) {
        this.students = students;
    }

    /**
     * @param color The color which we want to know the number of students of
     * @return The number of students of the input color
     */
    public int getNumberOfStudentsOfColor(Color color) {
        return Students.countColor(students, color);
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
    public void giveStudent(Place destination, Student student) throws InvalidStudentException, InvalidActionException {
        if (student == null || !students.contains(student)) {
            throw new InvalidStudentException(MessageResourceBundle.getMessage("dining_room_doesnt_contain_student"));
        }
        students.remove(student);
        destination.receiveStudent(student);
    }

    @Override
    public void receiveStudent(Student student) throws InvalidActionException {
        if (Students.countColor(students, student.getColor()) > Constants.MAX_STUDENTS_IN_DINING) {
            throw new InvalidActionException("You already have " + Constants.MAX_STUDENTS_IN_DINING + " " + student.getColor() + " students in your dining room!");
        }
        students.add(student);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DiningRoom dining = (DiningRoom) o;

        return students.equals(dining.students);
    }
}
