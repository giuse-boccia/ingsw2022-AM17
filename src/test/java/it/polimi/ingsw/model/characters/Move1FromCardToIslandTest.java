package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.exceptions.InvalidActionException;
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
     * Private method to find out if the array of Characters contains the one called "move1FromCardToIsland"
     *
     * @param characters the array of characters to check
     * @return the index of the {@code Character} called "move1FromCardToIsland" in the array
     */
    private int containsCard(Character[] characters) {
        for (int i = 0; i < characters.length; i++) {
            if (characters[i].getCardName() == CharacterName.move1FromCardToIsland)
                return i;
        }
        return -1;
    }

    /**
     * Tests the effect of the {@code Character} called "testMove1FromCardToIsland1"
     *
     * @throws EmptyBagException if the {@code Bag} is empty
     */
    @Test
    void testMove1FromCardToIsland1() throws EmptyBagException {

        gb.setCharacters(c);

        MovingCharacter character = (MovingCharacter) gb.getCharacters()[0];
        PlayerActionPhase pap = new PlayerActionPhase(
                new Assistant(4, 8, game.getPlayers().get(0)), gb
        );
        Island island = gb.getIslands().get(4);
        int initialStudentsOnIsland = island.getStudents().size();
        island.receiveStudent(new Student(Color.PINK));
        island.receiveStudent(new Student(Color.BLUE));
        character.fillCardFromBag();

        assertEquals(1, character.getNumStudents());
        assertEquals(4, character.getInitialStudents());

        character.setStudents(createListOfStudents());

        assertEquals(4, character.getStudents().size());

        assertDoesNotThrow(() -> pap.playCharacter(character, island, null, List.of(Color.PINK), null));

        assertEquals(4, character.getStudents().size());
        assertEquals(3 + initialStudentsOnIsland, island.getStudents().size());
    }

    /**
     * Tests the effect of the {@code Character} called "testMove1FromCardToIsland1"
     *
     * @throws EmptyBagException if the {@code Bag} is empty
     */
    @Test
    void testMove1FromCardToIsland2() throws EmptyBagException {
        gb.setCharacters(c);
        MovingCharacter character = (MovingCharacter) gb.getCharacters()[0];
        character.fillCardFromBag();
        assertEquals(4, character.getInitialStudents());
        assertEquals(4, character.getStudents().size());

        character.setStudents(createListOfStudents());

        Island island = gb.getIslands().get(3);
        int initialStudents = island.getStudents().size();
        island.receiveStudent(new Student(Color.GREEN));

        PlayerActionPhase pap = new PlayerActionPhase(
                new Assistant(4, 8, game.getPlayers().get(0)), gb
        );

        assertDoesNotThrow(() -> pap.playCharacter(
                character, island, null, List.of(Color.BLUE), null
        ));

        assertEquals(initialStudents + 2, island.getStudents().size());
        assertTrue(Students.countColor(island.getStudents(), Color.BLUE) >= 1);
        assertEquals(4, character.getStudents().size());
    }

    /**
     * Tests the effect of the {@code Character} called "testMove1FromCardToIsland1"
     *
     * @throws EmptyBagException if the {@code Bag} is empty
     */
    @Test
    void testMove1FromCardToIsland3() throws EmptyBagException {
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
     * Tests the effect of the {@code Character} called "testMove1FromCardToIsland1"
     *
     * @throws EmptyBagException if the {@code Bag} is empty
     */
    @Test
    void testMove1FromCardToIsland4() throws EmptyBagException {
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
