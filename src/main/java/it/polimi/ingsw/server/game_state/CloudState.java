package it.polimi.ingsw.server.game_state;

import it.polimi.ingsw.model.game_objects.Student;
import it.polimi.ingsw.model.game_objects.gameboard_objects.Cloud;

import java.util.ArrayList;
import java.util.List;

public class CloudState {
    private final List<Student> students;
    private final int maxStudents;

    public CloudState(List<Student> students, int maxStudents) {
        this.students = students;
        this.maxStudents = maxStudents;
    }


    /**
     * Loads the clouds from the given saved game
     *
     * @param savedGame the saved game state
     * @return a list of clouds
     */
    public static List<Cloud> loadClouds(SavedGameState savedGame) {
        List<Cloud> clouds = new ArrayList<>();
        savedGame.getClouds().forEach(cloudState -> clouds.add(cloudState.loadCloud()));
        return clouds;
    }

    /**
     * Loads a Cloud from this IslandState
     *
     * @return the loaded cloud
     */
    public Cloud loadCloud() {
        return new Cloud(students, maxStudents);
    }


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
