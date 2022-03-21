package it.polimi.ingsw.model;

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
        for (Student student : students) {
            this.giveStudent(destination, student);
        }
    }

    /**
     * Fill the cloud taking students from the bag
     * @param bag Bag used in the game
     */
    public void fillFromBag(Bag bag) {
        for (int i = 0; i < maxStudents; i++) {
            bag.giveStudent(this, bag.getRandStudent());
        }
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
