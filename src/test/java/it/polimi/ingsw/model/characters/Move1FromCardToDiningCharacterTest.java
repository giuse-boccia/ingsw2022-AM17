package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.exceptions.StudentNotOnTheCardException;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.TestGameFactory;
import it.polimi.ingsw.model.game_actions.PlayerActionPhase;
import it.polimi.ingsw.model.game_objects.Assistant;
import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.model.game_objects.gameboard_objects.GameBoard;
import it.polimi.ingsw.model.game_objects.Student;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class Move1FromCardToDiningCharacterTest {

    Game game = TestGameFactory.getNewGame();
    GameBoard gb = game.getGameBoard();
    Character[] c = {new MovingCharacter(CharacterName.move1FromCardToDining, gb, 4, 1)};

    /**
     * Tests the effect of the {@code Character} called "move1FromCardToDining"
     *
     * @throws EmptyBagException if the {@code Bag} is empty
     */
    @Test
    void testMove1FromCardToDining() throws EmptyBagException {

        gb.setCharacters(c);

        MovingCharacter character = (MovingCharacter) gb.getCharacters()[0];
        Player rick = game.getPlayers().get(0);
        PlayerActionPhase pap = new PlayerActionPhase(new Assistant(4, 8, rick), gb);
        for (int i = 0; i < 4; i++) {
            rick.addCoin();
        }

        character.fillCardFromBag();

        assertEquals(0, rick.getDashboard().getDiningRoom().getStudents().size());
        assertEquals(4, character.getStudents().size());

        Student firstStudentOnTheCard = character.getStudents().get(0);

        assertDoesNotThrow(() -> pap.playCharacter(character, null, null, new ArrayList<>(List.of(firstStudentOnTheCard)), null));

        assertEquals(4, character.getStudents().size());
        assertEquals(1, rick.getDashboard().getDiningRoom().getStudents().size());
        assertTrue(rick.getDashboard().getDiningRoom().getStudents().contains(firstStudentOnTheCard));
    }

    /**
     * Tests the case when the {@code Student} to move is not on the {@code Character}
     *
     * @throws EmptyBagException if the {@code Bag} is empty
     */
    @Test
    void testInvalidStudents1() throws EmptyBagException {
        gb.setCharacters(c);

        MovingCharacter character = (MovingCharacter) gb.getCharacters()[0];
        Player rick = game.getPlayers().get(0);
        PlayerActionPhase pap = new PlayerActionPhase(
                new Assistant(4, 8, rick), gb
        );
        for (int i = 0; i < 4; i++) {
            rick.addCoin();
        }

        character.fillCardFromBag();

        ArrayList<Student> invalidStudents = new ArrayList<>(List.of(character.getStudents().get(0), new Student(Color.PINK)));

        assertThrows(
                StudentNotOnTheCardException.class,
                () -> pap.playCharacter(character, null, null, invalidStudents, null),
                "The student is not on the card"
        );
    }

    /**
     * Tests the case when the {@code Student} to move is not on the {@code Character}
     *
     * @throws EmptyBagException if the {@code Bag} is empty
     */
    @Test
    void testInvalidStudents2() throws EmptyBagException {
        gb.setCharacters(c);

        MovingCharacter character = (MovingCharacter) gb.getCharacters()[0];
        Player rick = game.getPlayers().get(0);
        PlayerActionPhase pap = new PlayerActionPhase(
                new Assistant(4, 8, rick), gb
        );
        for (int i = 0; i < 4; i++) {
            rick.addCoin();
        }

        character.fillCardFromBag();

        ArrayList<Student> invalidStudents = new ArrayList<>(List.of(new Student(Color.PINK)));

        assertThrows(
                StudentNotOnTheCardException.class,
                () -> pap.playCharacter(character, null, null, invalidStudents, null),
                "The student is not on the card"
        );
    }
}
