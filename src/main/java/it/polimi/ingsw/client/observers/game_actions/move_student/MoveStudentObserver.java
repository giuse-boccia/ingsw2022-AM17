package it.polimi.ingsw.client.observers.game_actions.move_student;

import it.polimi.ingsw.model.game_objects.Color;

public interface MoveStudentObserver {

    /**
     * This method is triggered when a player moves a student of the selected color
     *
     * @param color       the color of the student to move
     * @param islandIndex the index of the island to eventually move the student to
     */
    void onStudentMoved(Color color, Integer islandIndex);

}
