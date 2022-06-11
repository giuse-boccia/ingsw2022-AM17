package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.InvalidActionException;
import it.polimi.ingsw.exceptions.InvalidStepsForMotherNatureException;
import it.polimi.ingsw.exceptions.InvalidStudentException;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.TestGameFactory;
import it.polimi.ingsw.model.game_actions.PlayerActionPhase;
import it.polimi.ingsw.model.game_objects.Assistant;
import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.model.game_objects.Student;
import it.polimi.ingsw.model.game_objects.gameboard_objects.GameBoard;
import it.polimi.ingsw.model.game_objects.gameboard_objects.Island;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IgnoreColorTest {

    Game game = TestGameFactory.getNewGame();
    GameBoard gb = game.getGameBoard();
    Player rick = game.getPlayers().get(0);
    Player clod = game.getPlayers().get(1);
    Player giuse = game.getPlayers().get(2);

    /**
     * Tests the behaviour of the character when useEffect method is called with invalid color parameter
     */
    @Test
    void testInvalidColor() {
        PassiveCharacter ignoreColor = new PassiveCharacter(CharacterName.ignoreColor);
        PlayerActionPhase pap = new PlayerActionPhase(new Assistant(1, 2, rick), gb);

        assertThrows(InvalidActionException.class, () ->
                ignoreColor.useEffect(pap, null, null, null, null));
    }

    /**
     * Tests the behaviour of ignoreColor character
     */
    @Test
    void testIgnoreColor() throws InvalidActionException, InvalidStudentException, InvalidStepsForMotherNatureException {
        PassiveCharacter ignoreColor = new PassiveCharacter(CharacterName.ignoreColor);
        gb.setCharacters(new Character[]{ignoreColor});

        gb.setMotherNatureIndex(0);
        for (int i = 0; i < 7; i++) {
            rick.getDashboard().getEntrance().receiveStudent(new Student(Color.GREEN));
            rick.addCoin();
        }
        moveStudentToDining(rick, Color.PINK);
        moveStudentToDining(clod, Color.RED);
        moveStudentToDining(giuse, Color.GREEN);
        fillJustFirstIsland();

        // Current game has all islands empty except the second one, where there are two green students and a red one
        // Rick owns pink professor, Clod owns red professor, Giuse owns green professor
        PlayerActionPhase pap = new PlayerActionPhase(new Assistant(1, 2, rick), gb);
        for (int i = 0; i < 4; i++) {
            pap.moveStudent(Color.GREEN, rick.getDashboard().getDiningRoom());
        }
        // After playing characters, influence in second island should no more be equal for Clod and Giuse;
        // instead, Clod should take the professor, because green students doesn't count
        assertDoesNotThrow(() -> pap.playCharacter(ignoreColor, null, Color.GREEN, null, null));

        pap.moveMotherNature(1);

        assertEquals(clod.getTowerColor(), gb.getIslands().get(1).getTowerColor());

    }

    /**
     * Moves one student of the given color from the entrance to the dining room of the given {@code Player} - even
     * though he initially doesn't own a student of that color
     *
     * @param player a valid {@code Player}
     * @param color  a valid student's {@code Color} to be moved to dining room
     */
    private void moveStudentToDining(Player player, Color color) throws InvalidActionException, InvalidStudentException {
        player.getDashboard().getEntrance().receiveStudent(new Student(color));
        PlayerActionPhase pap = new PlayerActionPhase(new Assistant(1, 2, player), gb);
        pap.moveStudent(color, player.getDashboard().getDiningRoom());
    }

    /**
     * Makes sure that all the islands don't have student except for the second one - which has two green students
     * and a red student
     */
    private void fillJustFirstIsland() throws InvalidStudentException, InvalidActionException {
        for (Island island : gb.getIslands()) {
            if (island.getStudents().size() != 0) {
                island.giveStudent(gb.getBag(), island.getStudents().get(0));
            }
        }
        gb.getIslands().get(1).receiveStudent(new Student(Color.RED));
        gb.getIslands().get(1).receiveStudent(new Student(Color.GREEN));
        gb.getIslands().get(1).receiveStudent(new Student(Color.GREEN));
    }

}
