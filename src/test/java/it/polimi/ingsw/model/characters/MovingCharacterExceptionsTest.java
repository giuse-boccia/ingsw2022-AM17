package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.InvalidActionException;
import it.polimi.ingsw.exceptions.InvalidCharacterException;
import it.polimi.ingsw.exceptions.InvalidStudentException;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.TestGameFactory;
import it.polimi.ingsw.model.game_actions.PlayerActionPhase;
import it.polimi.ingsw.model.game_objects.Assistant;
import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.model.game_objects.Student;
import it.polimi.ingsw.model.game_objects.gameboard_objects.GameBoard;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class MovingCharacterExceptionsTest {

    Game game = TestGameFactory.getNewGame();
    GameBoard gb = game.getGameBoard();

    /**
     * Tests what happens when the moving character has to give away a student who is not
     * present on the card
     */
    @Test
    void testStudentNotOnCard() {
        MovingCharacter character = new MovingCharacter(CharacterName.move1FromCardToIsland, gb, 4, 1);
        PlayerActionPhase pap = new PlayerActionPhase(
                new Assistant(4, 8, game.getPlayers().get(0)), gb
        );

        assertThrows(InvalidStudentException.class, () ->
                character.giveStudent(gb.getBag(), new Student(Color.RED)));

        assertThrows(InvalidActionException.class, () ->
                character.useEffect(pap, null, null, null, null));
    }

    /**
     * Tests if correct exceptions are called when method useEffect() of moving characters is invoked
     * with wrong parameters
     */
    @Test
    void testNullSource() {
        MovingCharacter character = new MovingCharacter(CharacterName.swapUpTo2FromEntranceToDiningRoom, gb, 0, 2);
        PlayerActionPhase pap = new PlayerActionPhase(
                new Assistant(4, 8, game.getPlayers().get(0)), gb
        );

        assertThrows(InvalidActionException.class, () ->
                character.useEffect(pap, null, null, null, null));
    }

    /**
     * Tests what happens when the method useEffect of a moving character is called for a card
     * whose nickname isn't among the accepted ones
     */
    @Test
    void testInvalidCardName() {
        MovingCharacter character = new MovingCharacter(CharacterName.ignoreColor, gb, 0, 2);
        PlayerActionPhase pap = new PlayerActionPhase(
                new Assistant(4, 8, game.getPlayers().get(0)), gb
        );

        assertThrows(InvalidCharacterException.class, () ->
                character.useEffect(pap, null, Color.YELLOW, null, null));
    }

}
