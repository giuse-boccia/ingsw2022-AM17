package it.polimi.ingsw.model.strategies.professor_strategies;

import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.model.game_objects.dashboard_objects.DiningRoom;

public class ProfessorDefault implements ProfessorStrategy {

    @Override
    public boolean canStealProfessor(Color color, DiningRoom myDiningRoom, DiningRoom otherDiningRoom) {
        int myStudentsNum = myDiningRoom.getNumberOfStudentsOfColor(color);
        int otherStudentsNum = otherDiningRoom.getNumberOfStudentsOfColor(color);

        return myStudentsNum > otherStudentsNum;
    }
}
