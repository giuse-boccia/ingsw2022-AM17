package it.polimi.ingsw.model;

public interface Place {
    /**
     * Transfers a student to the destination. The student will no longer be present in the object which this method
     * is called on.
     * @param destination Place which the method gives the student to
     * @param student The student given to the destination
     */
    void giveStudent(Place destination, Student student);

    /**
     * Adds a student to the object which this method is called on.
     * @param student The student to be received
     */
    void receiveStudent(Student student);
}
