package it.polimi.ingsw.model.characters.moving;

import it.polimi.ingsw.exceptions.*;
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
import it.polimi.ingsw.model.game_objects.dashboard_objects.DiningRoom;
import it.polimi.ingsw.model.game_objects.dashboard_objects.Entrance;
import it.polimi.ingsw.model.utils.Students;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SwapUpTo2FromEntranceToDiningCharacterTest {

    Game game = TestGameFactory.getNewGame();
    GameBoard gb = game.getGameBoard();
    Character[] c = {new MovingCharacter(CharacterName.swapUpTo2FromEntranceToDiningRoom, gb, 0, 2)};


    /**
     * Private method which makes the final assertions for the tests
     *
     * @param entrance the {@code Entrance} to check
     * @param dining   the {@code DiningRoom} to check
     */
    private void doFinalAssertions(Entrance entrance, DiningRoom dining) {
        assertEquals(1, Students.countColor(entrance.getStudents(), Color.PINK));
        assertEquals(3, Students.countColor(dining.getStudents(), Color.RED));
        assertEquals(2, Students.countColor(dining.getStudents(), Color.PINK));
        assertEquals(2, Students.countColor(entrance.getStudents(), Color.RED));

        assertEquals(4, Students.countColor(dining.getStudents(), Color.BLUE));
        assertEquals(2, Students.countColor(entrance.getStudents(), Color.YELLOW));
        assertEquals(1, Students.countColor(entrance.getStudents(), Color.BLUE));
        assertEquals(1, Students.countColor(dining.getStudents(), Color.YELLOW));
    }

    /**
     * Private method to fill the {@code Entrance} and the {@code DiningRoom} for the tests:
     * the {@code Entrance} receives 3 pink and 3 yellow students
     * the {@code DiningRoom} receives 5 red and 5 blue students
     *
     * @param entrance the {@code Entrance} to fill
     * @param dining   the {@code DiningRoom} to fill
     */
    private void fillEntranceAndDining(Entrance entrance, DiningRoom dining) {
        for (int i = 0; i < 3; i++) {
            entrance.receiveStudent(new Student(Color.PINK));
            entrance.receiveStudent(new Student(Color.YELLOW));
        }
        for (int i = 0; i < 5; i++) {
            dining.receiveStudent(new Student(Color.RED));
            dining.receiveStudent(new Student(Color.BLUE));
        }
    }

    /**
     * Tests the effect of the {@code Character} called "swapUpTo2FromEntranceToDiningRoom"
     *
     * @throws EmptyBagException if the {@code Bag} is empty
     */
    @Test
    void testCharacter() throws EmptyBagException {
        gb.setCharacters(c);

        MovingCharacter character = (MovingCharacter) gb.getCharacters()[0];
        Player rick = game.getPlayers().get(0);
        for (int i = 0; i < 100; i++) {
            rick.addCoin();
        }

        character.fillCardFromBag();

        assertEquals(0, character.getStudents().size());

        Entrance entrance = rick.getDashboard().getEntrance();
        DiningRoom dining = rick.getDashboard().getDiningRoom();
        fillEntranceAndDining(entrance, dining);

        ArrayList<Student> entranceInitialStudents = entrance.getStudents();
        ArrayList<Student> diningInitialStudents = dining.getStudents();
        assertEquals(5, Students.countColor(diningInitialStudents, Color.RED));
        assertEquals(5, Students.countColor(diningInitialStudents, Color.BLUE));
        assertEquals(3, Students.countColor(entranceInitialStudents, Color.YELLOW));
        assertEquals(3, Students.countColor(entranceInitialStudents, Color.PINK));

        for (int i = 0; i <= character.getNumStudents(); i++) {
            PlayerActionPhase pap = new PlayerActionPhase(
                    new Assistant(4, 8, rick), gb
            );
            int index = i;
            assertDoesNotThrow(() -> pap.playCharacter(
                    character, null, null,
                    TestGameFactory.fromListOfStudentToListOfColor(entrance.getStudents().subList(0, index)),
                    TestGameFactory.fromListOfStudentToListOfColor(dining.getStudents().subList(0, index))
            ));

            if (i < character.getNumStudents()) {
                assertEquals(3 - i, Students.countColor(entrance.getStudents(), Color.PINK));
                assertEquals(5 - i, Students.countColor(dining.getStudents(), Color.RED));
                assertEquals(i, Students.countColor(dining.getStudents(), Color.PINK));
                assertEquals(i, Students.countColor(entrance.getStudents(), Color.RED));

                assertEquals(3 - i, Students.countColor(entrance.getStudents(), Color.PINK));
                assertEquals(5 - i, Students.countColor(dining.getStudents(), Color.RED));
                assertEquals(0, Students.countColor(entrance.getStudents(), Color.BLUE));
                assertEquals(0, Students.countColor(dining.getStudents(), Color.YELLOW));
            } else {
                doFinalAssertions(entrance, dining);
            }
        }

        PlayerActionPhase pap = new PlayerActionPhase(
                new Assistant(4, 8, rick), gb
        );

        assertThrows(InvalidActionException.class, () -> pap.playCharacter(
                character, null, null,
                List.of(Color.YELLOW, Color.PINK, Color.YELLOW),
                List.of(Color.RED, Color.RED, Color.RED)
        ));

        doFinalAssertions(entrance, dining);
    }

    /**
     * Tests the effect of the {@code Character} called "swapUpTo2FromEntranceToDiningRoom" and the exception thrown when
     * the {@code Player} is trying to move at least a {@code Student} which is not in their {@code Entrance}
     *
     * @throws EmptyBagException if the {@code Bag} is empty
     */
    @Test
    void testInvalidSource() throws EmptyBagException {
        gb.setCharacters(c);

        MovingCharacter character = (MovingCharacter) gb.getCharacters()[0];
        Player rick = game.getPlayers().get(0);
        for (int i = 0; i < 100; i++) {
            rick.addCoin();
        }

        PlayerActionPhase pap = new PlayerActionPhase(
                new Assistant(4, 8, rick), gb
        );

        character.fillCardFromBag();

        Entrance entrance = rick.getDashboard().getEntrance();
        DiningRoom diningRoom = rick.getDashboard().getDiningRoom();
        fillEntranceAndDining(entrance, diningRoom);

        assertThrows(
                StudentNotOnTheCardException.class,
                () -> pap.playCharacter(character, null, null, List.of(Color.PINK, Color.GREEN), List.of(Color.BLUE, Color.RED)));
    }

    /**
     * Tests the effect of the {@code Character} called "swapUpTo2FromEntranceToDiningRoom" and the exception thrown when
     * the {@code Player} is trying to move at least a {@code Student} which is not in their {@code DiningRoom}
     *
     * @throws EmptyBagException if the {@code Bag} is empty
     */
    @Test
    void testInvalidDestination() throws EmptyBagException {
        gb.setCharacters(c);

        MovingCharacter character = (MovingCharacter) gb.getCharacters()[0];
        Player rick = game.getPlayers().get(0);
        for (int i = 0; i < 100; i++) {
            rick.addCoin();
        }

        PlayerActionPhase pap = new PlayerActionPhase(
                new Assistant(4, 8, rick), gb
        );

        character.fillCardFromBag();

        Entrance entrance = rick.getDashboard().getEntrance();
        DiningRoom diningRoom = rick.getDashboard().getDiningRoom();
        fillEntranceAndDining(entrance, diningRoom);

        assertThrows(
                StudentNotOnTheCardException.class,
                () -> pap.playCharacter(character, null, null, List.of(Color.PINK, Color.YELLOW), List.of(Color.PINK, Color.GREEN)),
                "One or more students are not on the dining room"
        );
    }

    /**
     * Tests the effect of the {@code Character} called "swapUpTo2FromEntranceToDiningRoom" and the exception thrown when
     * the {@code Player} is trying to swap students chosen in different quantities
     *
     * @throws EmptyBagException if the {@code Bag} is empty
     */
    @Test
    void testInvalidSize1() throws EmptyBagException {
        gb.setCharacters(c);

        MovingCharacter character = (MovingCharacter) gb.getCharacters()[0];
        Player rick = game.getPlayers().get(0);
        for (int i = 0; i < 100; i++) {
            rick.addCoin();
        }

        PlayerActionPhase pap = new PlayerActionPhase(
                new Assistant(4, 8, rick), gb
        );

        character.fillCardFromBag();

        Entrance entrance = rick.getDashboard().getEntrance();
        DiningRoom diningRoom = rick.getDashboard().getDiningRoom();
        fillEntranceAndDining(entrance, diningRoom);


        assertThrows(
                InvalidActionException.class,
                () -> pap.playCharacter(character, null, null,
                        List.of(Color.PINK),
                        List.of(Color.RED, Color.RED))
        );
    }

    /**
     * Tests the effect of the {@code Character} called "swapUpTo2FromEntranceToDiningRoom" and the exception thrown when
     * the {@code Player} is trying to move more than 2 students
     *
     * @throws EmptyBagException if the {@code Bag} is empty
     */
    @Test
    void testInvalidSize2() throws EmptyBagException {
        gb.setCharacters(c);

        MovingCharacter character = (MovingCharacter) gb.getCharacters()[0];
        Player rick = game.getPlayers().get(0);
        for (int i = 0; i < 100; i++) {
            rick.addCoin();
        }

        PlayerActionPhase pap = new PlayerActionPhase(
                new Assistant(4, 8, rick), gb
        );

        character.fillCardFromBag();

        Entrance entrance = rick.getDashboard().getEntrance();
        DiningRoom diningRoom = rick.getDashboard().getDiningRoom();
        fillEntranceAndDining(entrance, diningRoom);
        
        assertThrows(
                InvalidActionException.class,
                () -> pap.playCharacter(character, null, null,
                        List.of(Color.YELLOW, Color.YELLOW, Color.YELLOW),
                        List.of(Color.RED, Color.RED, Color.RED))
        );
    }

}
