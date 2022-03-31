package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.game_actions.action_phase.PlayerActionPhase;
import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.model.game_objects.GameBoard;
import it.polimi.ingsw.model.game_objects.Island;
import it.polimi.ingsw.model.game_objects.Student;
import it.polimi.ingsw.model.utils.Students;

import java.util.ArrayList;

public class EveryOneMovesCharacter extends GameboardCharacter {

    public EveryOneMovesCharacter(CharacterName characterName, GameBoard gb) {
        super(characterName, gb);
    }

    @Override
    public void useEffect(PlayerActionPhase currentPlayerActionPhase, Island island, Color color, ArrayList<Student> srcStudents, ArrayList<Student> dstStudents) {
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
