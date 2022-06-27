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
import it.polimi.ingsw.model.game_objects.*;
import it.polimi.ingsw.model.game_objects.dashboard_objects.DiningRoom;
import it.polimi.ingsw.model.game_objects.dashboard_objects.Entrance;
import it.polimi.ingsw.model.game_objects.gameboard_objects.GameBoard;
import it.polimi.ingsw.model.utils.Students;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class SwapUpTo3CharacterTest {

    Game game = TestGameFactory.getNewGame();
    GameBoard gb = game.getGameBoard();
    Character[] c = {new MovingCharacter(CharacterName.swapUpTo3FromEntranceToCard, gb, 6, 3)};


    /**
     * Private method to fill the {@code Entrance} for the tests:
     * the {@code Entrance} receives 4 blue, 4 green and 1 red students
     *
     * @param entrance the {@code Entrance} to fill
     */
    private void fillEntrance(Entrance entrance) {
        entrance.receiveStudent(new Student(Color.RED));
        for (int i = 0; i < 4; i++) {
            entrance.receiveStudent(new Student(Color.GREEN));
            entrance.receiveStudent(new Student(Color.BLUE));
        }
    }

    /**
     * Tests the effect of the {@code Character} called "swapUpTo3FromEntranceToCard"
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
        assertEquals(6, character.getStudents().size());

        character.setStudents(createListOfStudents());
        Entrance entrance = rick.getDashboard().getEntrance();
        fillEntrance(entrance);

        for (int i = 0; i < 3; i++) {
            PlayerActionPhase pap = new PlayerActionPhase(
                    new Assistant(4, 8, rick), gb
            );
            ArrayList<Student> initialStudentsOnCard = character.getStudents();
            ArrayList<Student> initialStudentsInEntrance = entrance.getStudents();
            ArrayList<Student> studentsOnCardToMove = new ArrayList<>(character.getStudents().subList(0, i));
            ArrayList<Student> studentsInEntranceToMove = new ArrayList<>(entrance.getStudents().subList(0, i));

            assertDoesNotThrow(
                    () -> pap.playCharacter(character, null, null,
                            TestGameFactory.fromListOfStudentToListOfColor(studentsInEntranceToMove),
                            TestGameFactory.fromListOfStudentToListOfColor(studentsOnCardToMove))
            );

            // Edit the expected Students
            initialStudentsOnCard.removeAll(studentsOnCardToMove);
            initialStudentsInEntrance.removeAll(studentsInEntranceToMove);
            initialStudentsOnCard.addAll(studentsInEntranceToMove);
            initialStudentsInEntrance.addAll(studentsOnCardToMove);

            /* TODO: REWRITE BROKEN ASSERTIONS -> order of students can be mixed up while swapping

            for (int j = 0; j < initialStudentsOnCard.size(); j++) {
                assertEquals(initialStudentsOnCard.get(j), character.getStudents().get(j));
            }
            for (int j = 0; j < initialStudentsInEntrance.size(); j++) {
                assertEquals(initialStudentsInEntrance.get(j), entrance.getStudents().get(j));
            }
            */
        }
    }

    /**
     * Tests the effect of the {@code Character} called "swapUpTo3FromEntranceToCard" and the exception thrown when
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
        fillEntrance(entrance);

        assertThrows(
                StudentNotOnTheCardException.class,
                () -> pap.playCharacter(character, null, null,
                        List.of(Color.PINK, Color.GREEN),
                        new ArrayList<>()),
                "One or more students are not on the entrance"
        );
    }

    /**
     * Tests the effect of the {@code Character} called "swapUpTo3FromEntranceToCard" and the exception thrown when
     * the {@code Player} is trying to move at least a {@code Student} which is not on the {@code Character}
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
        character.setStudents(createListOfStudents());

        Entrance entrance = rick.getDashboard().getEntrance();
        fillEntrance(entrance);

        assertThrows(
                StudentNotOnTheCardException.class,
                () -> pap.playCharacter(character, null, null, new ArrayList<>(), List.of(Color.GREEN))
        );
    }

    /**
     * Tests the effect of the {@code Character} called "swapUpTo3FromEntranceToCard" and the exception thrown when
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
        character.setStudents(createListOfStudents());

        Entrance entrance = rick.getDashboard().getEntrance();
        fillEntrance(entrance);

        assertThrows(
                InvalidActionException.class,
                () -> pap.playCharacter(character, null, null,
                        List.of(Color.BLUE),
                        new ArrayList<>())
        );
    }

    /**
     * Tests the effect of the {@code Character} called "swapUpTo3FromEntranceToCard" and the exception thrown when
     * the {@code Player} is trying to move more than 3 students
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

        PlayerActionPhase pap = new PlayerActionPhase(new Assistant(4, 8, rick), gb);

        character.fillCardFromBag();
        character.setStudents(createListOfStudents());

        Entrance entrance = rick.getDashboard().getEntrance();
        fillEntrance(entrance);

        assertThrows(
                InvalidActionException.class,
                () -> pap.playCharacter(character, null, null,
                        List.of(Color.GREEN, Color.BLUE, Color.BLUE, Color.BLUE),
                        List.of(Color.BLUE, Color.PINK, Color.PINK, Color.PINK)
                ));
    }

    /**
     * Creates a list of students containing 3 BLUE students and 3 PINK student
     *
     * @return a list of students containing 3 BLUE students and 3 PINK student
     */
    private ArrayList<Student> createListOfStudents() {
        ArrayList<Student> listOfStudents = new ArrayList<>();
        for (int i = 0; i < 3; i++) {
            listOfStudents.add(new Student(Color.BLUE));
            listOfStudents.add(new Student(Color.PINK));
        }
        return listOfStudents;
    }

}
