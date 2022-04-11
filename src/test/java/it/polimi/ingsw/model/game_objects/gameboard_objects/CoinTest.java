package it.polimi.ingsw.model.game_objects.gameboard_objects;

import it.polimi.ingsw.exceptions.InvalidActionException;
import it.polimi.ingsw.exceptions.InvalidStudentException;
import it.polimi.ingsw.exceptions.NotEnoughCoinsException;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.TestGameFactory;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.model.characters.PassiveCharacter;
import it.polimi.ingsw.model.game_actions.PlayerActionPhase;
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
        Player rick = game.getPlayers().get(0); // Rick is Player 0

        Character[] c = {
                new PassiveCharacter(CharacterName.takeProfWithEqualStudents),
                new PassiveCharacter(CharacterName.ignoreTowers),
                new PassiveCharacter(CharacterName.plus2MNMoves)
        };

        game.getGameBoard().setCharacters(c);

        for (int i = 0; i < 7; i++) {
            rick.addCoin();
        }
        // Rick now has 8 coins (the initial one + 7 added)

        // Rick's turn, he played the lion assistant
        Assistant lion = new Assistant(1, 1, rick);
        PlayerActionPhase pap = new PlayerActionPhase(lion, game.getGameBoard());

        // Rick plays first character, whose cost is 2
        assertDoesNotThrow(() -> pap.playCharacter(c[0], null, null, null, null));
        assertEquals(6, rick.getNumCoins());

        // Rick plays again first character, whose cost is now 3
        PlayerActionPhase pap2 = new PlayerActionPhase(lion, game.getGameBoard());
        assertDoesNotThrow(() -> pap2.playCharacter(c[0], null, null, null, null));
        assertEquals(3, rick.getNumCoins());

        // Rick plays once again first character, whose cost is still 3
        PlayerActionPhase pap3 = new PlayerActionPhase(lion, game.getGameBoard());
        assertDoesNotThrow(() -> pap3.playCharacter(c[0], null, null, null, null));
        assertEquals(0, rick.getNumCoins());

        // Rick tries to play the second character, but he doesn't have enough coins
        PlayerActionPhase pap4 = new PlayerActionPhase(lion, game.getGameBoard());
        assertThrows(NotEnoughCoinsException.class,
                () -> pap4.playCharacter(c[1], null, null, null, null));
        assertEquals(0, rick.getNumCoins());
        assertEquals(3, c[1].getCost());
    }

}
