package it.polimi.ingsw.model.characters.moving;

import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.exceptions.InvalidActionException;
import it.polimi.ingsw.exceptions.InvalidStudentException;
import it.polimi.ingsw.exceptions.StudentNotOnTheCardException;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.TestGameFactory;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.model.characters.MovingCharacter;
import it.polimi.ingsw.model.game_actions.PlayerActionPhase;
import it.polimi.ingsw.model.game_objects.Assistant;
import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.model.game_objects.gameboard_objects.GameBoard;
import it.polimi.ingsw.model.game_objects.Student;
import it.polimi.ingsw.model.utils.Students;
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
        ArrayList<Student> listOfStudents = createListOfStudents();
        character.setStudents(listOfStudents);

        assertEquals(0, rick.getDashboard().getDiningRoom().getStudents().size());
        assertEquals(4, character.getStudents().size());


        assertDoesNotThrow(() -> pap.playCharacter(character, null, null, List.of(Color.PINK), null));

        assertEquals(4, character.getStudents().size());
        assertEquals(1, rick.getDashboard().getDiningRoom().getStudents().size());
        assertEquals(1, Students.countColor(rick.getDashboard().getDiningRoom().getStudents(), Color.PINK));
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
        ArrayList<Student> listOfStudents = createListOfStudents();
        character.setStudents(listOfStudents);

        assertThrows(
                StudentNotOnTheCardException.class,
                () -> pap.playCharacter(character, null, null, List.of(Color.GREEN), null)
        );
    }

    /**
     * Tests the case when the player tries to move more than one {@code Student}
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
        ArrayList<Student> listOfStudents = createListOfStudents();
        character.setStudents(listOfStudents);

        assertThrows(
                InvalidActionException.class,
                () -> pap.playCharacter(character, null, null, List.of(Color.BLUE, Color.PINK), null)
        );
    }

    /**
     * Creates a list of students containing 3 BLUE students and 1 PINK student
     *
     * @return a list of students containing 3 BLUE students and 1 PINK student
     */
    private ArrayList<Student> createListOfStudents() {
        ArrayList<Student> listOfStudents = new ArrayList<>();
        listOfStudents.add(new Student(Color.BLUE));
        listOfStudents.add(new Student(Color.BLUE));
        listOfStudents.add(new Student(Color.BLUE));
        listOfStudents.add(new Student(Color.PINK));
        return listOfStudents;
    }
}
