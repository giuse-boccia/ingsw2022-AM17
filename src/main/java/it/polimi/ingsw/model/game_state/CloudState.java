package it.polimi.ingsw.model.game_state;

import it.polimi.ingsw.model.game_objects.Student;
import it.polimi.ingsw.model.game_objects.gameboard_objects.Cloud;

import java.util.List;

public class CloudState {
    private final List<Student> students;
    private final int maxStudents;

    public CloudState(List<Student> students, int maxStudents) {
        this.students = students;
        this.maxStudents = maxStudents;
    }

    // TODO: refactor so that Cloud extends CloudState (and rename accordingly)
    public CloudState(Cloud cloud) {
        this(
                cloud.getStudents(),
                cloud.getMaxStudents()
        );
    }

    public List<Student> getStudents() {
        return students;
    }

    public int getMaxStudents() {
        return maxStudents;
    }
}
