package it.polimi.ingsw.model.strategies.professor_strategies;

import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.model.game_objects.dashboard_objects.DiningRoom;

public class ProfessorWithDraw implements ProfessorStrategy {
    /**
     * The method to check if the {@code Player} who owns myDiningRoom can steal the {@code Professor} of the
     * selected {@code Color} from the {@code Player} who owns otherDiningRoom when the effect of the {@code Character}
     * called "takeProfWithEqualStudents" is active
     *
     * @param color           the {@code Color} to check
     * @param myDiningRoom    the {@code DiningRoom} of the {@code Player} who wants to steal the {@code Professor}
     * @param otherDiningRoom the {@code DiningRoom} of the {@code Player} who owns the {@code Professor}
     * @return true if the number of professors in myDiningRoom is bigger than or equal to the one in otherDiningRoom
     */
    @Override
    public boolean canStealProfessor(Color color, DiningRoom myDiningRoom, DiningRoom otherDiningRoom) {
        int myStudentsNum = myDiningRoom.getNumberOfStudentsOfColor(color);
        int otherStudentsNum = otherDiningRoom.getNumberOfStudentsOfColor(color);

        return myStudentsNum >= otherStudentsNum;
    }
}
