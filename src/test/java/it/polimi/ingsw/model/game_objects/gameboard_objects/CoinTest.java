package it.polimi.ingsw.model.game_objects.gameboard_objects;

import it.polimi.ingsw.exceptions.InvalidActionException;
import it.polimi.ingsw.exceptions.InvalidStudentException;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.TestGameFactory;
import it.polimi.ingsw.model.game_actions.action_phase.PlayerActionPhase;
import it.polimi.ingsw.model.game_objects.Assistant;
import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.model.game_objects.Student;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CoinTest {

    Game game = TestGameFactory.getNewGame();

    /**
     * Tests the increasing of the coins of a player
     *
     * @throws InvalidActionException  if the action in the {@code PlayerActionPhase} is not valid - never thrown
     * @throws InvalidStudentException if any of the students moved to the {@code DiningRoom}
     *                                 is not valid - never thrown
     */
    @Test
    void testIncreasingOfCoins() throws InvalidActionException, InvalidStudentException {
        Player rick = game.getPlayers().get(0);
        PlayerActionPhase pap = new PlayerActionPhase(new Assistant(4, 8, rick), game.getGameBoard());

        for (int i = 0; i < 4; i++) {
            rick.getDashboard().getEntrance().receiveStudent(new Student(Color.GREEN));
            rick.getDashboard().getEntrance().receiveStudent(new Student(Color.YELLOW));
        }
        assertEquals(1, rick.getNumCoins());

        for (int i = 0; i < 2; i++) {
            pap.moveStudent(Color.GREEN, rick.getDashboard().getDiningRoom());
            pap.moveStudent(Color.YELLOW, rick.getDashboard().getDiningRoom());
        }
        assertEquals(1, rick.getNumCoins());

        pap = new PlayerActionPhase(new Assistant(4, 8, rick), game.getGameBoard());
        pap.moveStudent(Color.GREEN, rick.getDashboard().getDiningRoom());
        assertEquals(2, rick.getNumCoins());
        pap.moveStudent(Color.YELLOW, rick.getDashboard().getDiningRoom());
        assertEquals(3, rick.getNumCoins());

    }

    @Test
    void testDecreasingOfCoins() {
        //TODO Give the Game some known players and test their decreasing

    }

}
