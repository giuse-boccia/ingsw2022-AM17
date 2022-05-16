package it.polimi.ingsw.model.game_objects.dashboard_objects;

import it.polimi.ingsw.exceptions.InvalidStudentException;
import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.model.Place;
import it.polimi.ingsw.model.game_objects.Student;
import it.polimi.ingsw.model.utils.Students;

import java.util.ArrayList;

public class DiningRoom implements Place {
    private final ArrayList<Student> students;

    public DiningRoom() {
        students = new ArrayList<>();
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
    public void giveStudent(Place destination, Student student) throws InvalidStudentException {
        if (student == null || !students.contains(student)) {
            throw new InvalidStudentException("The dining room doesn't contain this student");
        }
        students.remove(student);
        destination.receiveStudent(student);
    }

    @Override
    public void receiveStudent(Student student) {
        students.add(student);
    }
}
