package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.InvalidStudentException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.game_actions.PlayerActionPhase;
import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.model.game_objects.gameboard_objects.GameBoard;
import it.polimi.ingsw.model.game_objects.gameboard_objects.Island;
import it.polimi.ingsw.model.game_objects.Student;
import it.polimi.ingsw.model.utils.Students;

import java.util.ArrayList;

public class EveryOneMovesCharacter extends GameboardCharacter {

    public EveryOneMovesCharacter(CharacterName characterName, GameBoard gb) {
        super(characterName, gb);
    }

    /**
     * Every {@code Player} moves 3 students (if they have them) of the selected {@code Color} from their
     * {@code DiningRoom} to the {@code Bag}
     *
     * @param currentPlayerActionPhase the {@code PlayerActionPhase} which the effect is used in
     * @param island                   the {@code Island} which the {@code Character} affects
     * @param color                    the {@code Color} which the {@code Character} affects
     * @param srcStudents              the students to be moved to the destination
     * @param dstStudents              the students to be moved to the source (only if the effect is a "swap" effect)
     */
    @Override
    public void useEffect(PlayerActionPhase currentPlayerActionPhase, Island island, Color color, ArrayList<Student> srcStudents, ArrayList<Student> dstStudents) throws InvalidStudentException {
        for (Player player : getGameBoard().getGame().getPlayers()) {
            int studentsToMove = Math.max(player.getDashboard().getDiningRoom().getNumberOfStudentsOfColor(color), 3);
            for (int i = 0; i < studentsToMove; i++) {
                Student studentToGive = Students.findFirstStudentOfColor(player.getDashboard().getDiningRoom().getStudents(), color);
                if (studentToGive != null) {
                    player.getDashboard().getDiningRoom().giveStudent(getGameBoard().getBag(), studentToGive);
                }
            }
            super.addCoinAfterFirstUse();
        }
    }
}
