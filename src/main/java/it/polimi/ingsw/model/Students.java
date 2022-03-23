package it.polimi.ingsw.model;

import java.util.ArrayList;
import java.util.Collections;

/**
 * A class containing only static methods to handle collections of students efficiently
 */
public class Students {
    private Students(){
    }

    /**
     * Counts the number of students of the given {@code Color} inside the provided {@code ArrayList} of students
     * @param students an {@code ArrayList} of students to be counted
     * @param color a {@code Color} to count the students of
     * @return the number of students of the given {@code Color}
     */
    public static int countColor(ArrayList<Student> students, Color color){
        int res = 0;
        for (Student student: students){
            if(student.getColor() == color){
                res++;
            }
        }
        return res;
    }

    /**
     * Returns a shuffled {@code ArrayList} of students containing num students of each color.
     * If a negative number is provided the method returns an empty collection
     * @param num the number of students of each color to be contained in the result
     * @return a shuffled {@code ArrayList} of students containing num students of each color
     */
    public static ArrayList<Student> getSomeStudents(int num){
        ArrayList<Student> res = new ArrayList<>();
        for (int i = 0; i < num; i++) {
            for(Color color: Color.values()){
                res.add(new Student(color));
            }
        }

        Collections.shuffle(res);

        return res;
    }
}
