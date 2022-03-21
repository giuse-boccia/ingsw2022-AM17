package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.Collection;

public class DiningRoom implements Place{
    private final Collection<Student> students;

    public DiningRoom() {
        students = new ArrayList<>();
    }

    /**
     *
     * @param color The color which we want to know the number of students of
     * @return The number of students of the input color
     */
    public int getNumberOfStudentsOfColor(Color color){
        int studentsCount = 0;
        for(Student s : students){
            if(s.getColor() == color)
                studentsCount++;
        }
        return studentsCount;
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
