package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.exceptions.InvalidActionException;
import it.polimi.ingsw.exceptions.InvalidStudentException;
import it.polimi.ingsw.exceptions.StudentNotOnTheCardException;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.TestGameFactory;
import it.polimi.ingsw.model.game_actions.PlayerActionPhase;
import it.polimi.ingsw.model.game_objects.*;
import it.polimi.ingsw.model.game_objects.gameboard_objects.GameBoard;
import it.polimi.ingsw.model.game_objects.gameboard_objects.Island;
import it.polimi.ingsw.model.utils.Students;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class Move1FromCardToIslandTest {

    Game game = TestGameFactory.getNewGame();
    GameBoard gb = game.getGameBoard();
    Character[] c = {new MovingCharacter(CharacterName.move1FromCardToIsland, gb, 4, 1)};


    /**
     * Tests the effect of the {@code Character} called "testMove1FromCardToIsland1"
     *
     * @throws EmptyBagException if the {@code Bag} is empty
     */
    @Test
    void testMove1FromCardToIsland1() throws EmptyBagException, InvalidStudentException {

        gb.setCharacters(c);

        MovingCharacter character = (MovingCharacter) gb.getCharacters()[0];
        PlayerActionPhase pap = new PlayerActionPhase(
                new Assistant(4, 8, game.getPlayers().get(0)), gb
        );
        Island island = gb.getIslands().get(4);
        if (island.getStudents().size() != 0) {
            island.giveStudent(gb.getBag(), island.getStudents().get(0));
        }
        island.receiveStudent(new Student(Color.PINK));
        island.receiveStudent(new Student(Color.BLUE));
        character.fillCardFromBag();

        assertEquals(1, character.getNumStudents());
        assertEquals(4, character.getInitialStudents());

        character.setStudents(createListOfStudents());

        assertEquals(4, character.getStudents().size());
        assertFalse(character.hasCoin());

        assertDoesNotThrow(() -> pap.playCharacter(character, island, null, List.of(Color.PINK), null));

        assertTrue(character.hasCoin());
        assertEquals(4, character.getStudents().size());
        assertEquals(3, island.getStudents().size());
        assertEquals(2, Students.countColor(island.getStudents(), Color.PINK));
    }

    /**
     * Tests the effect of the {@code Character} called "testMove1FromCardToIsland1"
     *
     * @throws EmptyBagException if the {@code Bag} is empty
     */
    @Test
    void testMove1FromCardToIsland2() throws EmptyBagException, InvalidStudentException {
        gb.setCharacters(c);
        MovingCharacter character = (MovingCharacter) gb.getCharacters()[0];
        character.fillCardFromBag();
        assertEquals(4, character.getInitialStudents());
        assertEquals(4, character.getStudents().size());

        character.setStudents(createListOfStudents());

        Island island = gb.getIslands().get(3);
        if (island.getStudents().size() != 0) {
            island.giveStudent(gb.getBag(), island.getStudents().get(0));
        }
        island.receiveStudent(new Student(Color.GREEN));

        PlayerActionPhase pap = new PlayerActionPhase(
                new Assistant(4, 8, game.getPlayers().get(0)), gb
        );

        assertDoesNotThrow(() -> pap.playCharacter(
                character, island, null, List.of(Color.BLUE), null
        ));

        assertEquals(2, island.getStudents().size());
        assertEquals(1, Students.countColor(island.getStudents(), Color.BLUE));
        assertEquals(4, character.getStudents().size());
    }

    /**
     * Tests the effect of the {@code Character} called "testMove1FromCardToIsland1" when the selected
     * student is not on the card
     *
     * @throws EmptyBagException if the {@code Bag} is empty
     */
    @Test
    void testStudentNotPresentMove1FromCardToIsland() throws EmptyBagException {
        gb.setCharacters(c);

        MovingCharacter character = (MovingCharacter) gb.getCharacters()[0];
        PlayerActionPhase pap = new PlayerActionPhase(
                new Assistant(4, 8, game.getPlayers().get(0)), gb
        );
        Island island = gb.getIslands().get(6);

        character.fillCardFromBag();
        character.setStudents(createListOfStudents());

        assertThrows(
                StudentNotOnTheCardException.class,
                () -> pap.playCharacter(character, island, null, List.of(Color.YELLOW), null)
        );
    }

    /**
     * Tests the effect of the {@code Character} called "testMove1FromCardToIsland1" when two students are passed
     * to the method
     *
     * @throws EmptyBagException if the {@code Bag} is empty
     */
    @Test
    void testInvalidSizeMove1FromCardToIsland() throws EmptyBagException {
        gb.setCharacters(c);

        MovingCharacter character = (MovingCharacter) gb.getCharacters()[0];
        PlayerActionPhase pap = new PlayerActionPhase(
                new Assistant(4, 8, game.getPlayers().get(0)), gb
        );
        Island island = gb.getIslands().get(6);

        character.fillCardFromBag();

        character.setStudents(createListOfStudents());

        assertThrows(
                InvalidActionException.class,
                () -> pap.playCharacter(character, island, null, List.of(Color.BLUE, Color.PINK), null)
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
