package it.polimi.ingsw.client.observers.game_actions.move_student;

import it.polimi.ingsw.model.game_objects.Color;

public interface MoveStudentObserver {

    void onStudentMoved(Color color, Integer islandIndex);

}
