package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.exceptions.InvalidActionException;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.TestGameFactory;
import it.polimi.ingsw.model.game_actions.action_phase.PlayerActionPhase;
import it.polimi.ingsw.model.game_actions.action_phase.PlayerActionPhaseFactory;
import it.polimi.ingsw.model.game_objects.*;
import it.polimi.ingsw.model.game_objects.dashboard_objects.DiningRoom;
import it.polimi.ingsw.model.game_objects.dashboard_objects.Entrance;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class SwapUpTo3CharacterTest {

    Game game = TestGameFactory.getNewGame();
    GameBoard gb = game.getGameBoard();

    /**
     * Private method to fill the {@code Entrance} for the tests:
     * the {@code Entrance} receives 4 blue and 4 green students
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
        MovingCharacter character = new MovingCharacter(CharacterName.swapUpTo3FromEntranceToCard, gb, 6, 3);
        Player rick = game.getPlayers().get(0);

        character.fillCardFromBag();

        assertEquals(6, character.getStudents().size());

        Entrance entrance = rick.getDashboard().getEntrance();
        fillEntrance(entrance);

        for (int i = 0; i < 3; i++) {
            PlayerActionPhase pap = PlayerActionPhaseFactory.createPlayerActionPhase(
                    new Assistant(4, 8, rick), gb, true
            );
            ArrayList<Student> initialStudentsOnCard = character.getStudents();
            ArrayList<Student> initialStudentsInEntrance = entrance.getStudents();
            ArrayList<Student> studentsOnCardToMove = new ArrayList<>(character.getStudents().subList(0, i));
            ArrayList<Student> studentsInEntranceToMove = new ArrayList<>(entrance.getStudents().subList(0, i));

            assertDoesNotThrow(
                    () -> pap.playCharacter(character, null, null, studentsInEntranceToMove, studentsOnCardToMove)
            );

            // Edit the expected Students
            initialStudentsOnCard.removeAll(studentsOnCardToMove);
            initialStudentsInEntrance.removeAll(studentsInEntranceToMove);
            initialStudentsOnCard.addAll(studentsInEntranceToMove);
            initialStudentsInEntrance.addAll(studentsOnCardToMove);

            for (int j = 0; j < initialStudentsOnCard.size(); j++) {
                assertEquals(initialStudentsOnCard.get(j), character.getStudents().get(j));
            }
            for (int j = 0; j < initialStudentsInEntrance.size(); j++) {
                assertEquals(initialStudentsInEntrance.get(j), entrance.getStudents().get(j));
            }
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
        MovingCharacter character = new MovingCharacter(CharacterName.swapUpTo3FromEntranceToCard, gb, 6, 3);
        Player rick = game.getPlayers().get(0);
        PlayerActionPhase pap = PlayerActionPhaseFactory.createPlayerActionPhase(
                new Assistant(4, 8, rick), gb, true
        );

        character.fillCardFromBag();

        Entrance entrance = rick.getDashboard().getEntrance();
        fillEntrance(entrance);

        ArrayList<Student> invalidStudents = new ArrayList<>(List.of(new Student(Color.PINK), new Student(Color.GREEN)));

        assertThrows(
                InvalidActionException.class,
                () -> pap.playCharacter(character, null, null, invalidStudents, new ArrayList<>()),
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
        MovingCharacter character = new MovingCharacter(CharacterName.swapUpTo3FromEntranceToCard, gb, 6, 3);
        Player rick = game.getPlayers().get(0);
        PlayerActionPhase pap = PlayerActionPhaseFactory.createPlayerActionPhase(
                new Assistant(4, 8, rick), gb, true
        );

        character.fillCardFromBag();

        Entrance entrance = rick.getDashboard().getEntrance();
        fillEntrance(entrance);

        ArrayList<Student> invalidStudents = new ArrayList<>(List.of(new Student(Color.PINK), new Student(Color.GREEN)));

        assertThrows(
                InvalidActionException.class,
                () -> pap.playCharacter(character, null, null, new ArrayList<>(), invalidStudents),
                "One or more students are not on the card"
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
        MovingCharacter character = new MovingCharacter(CharacterName.swapUpTo3FromEntranceToCard, gb, 6, 3);
        Player rick = game.getPlayers().get(0);
        PlayerActionPhase pap = PlayerActionPhaseFactory.createPlayerActionPhase(
                new Assistant(4, 8, rick), gb, true
        );

        character.fillCardFromBag();

        Entrance entrance = rick.getDashboard().getEntrance();
        fillEntrance(entrance);

        ArrayList<Student> srcStudents = new ArrayList<>(entrance.getStudents().subList(0, 2));
        ArrayList<Student> dstStudents = new ArrayList<>(character.getStudents().subList(0, 1));

        assertThrows(
                InvalidActionException.class,
                () -> pap.playCharacter(character, null, null, srcStudents, dstStudents),
                "This is not a valid swap"
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
        MovingCharacter character = new MovingCharacter(CharacterName.swapUpTo3FromEntranceToCard, gb, 6, 3);
        Player rick = game.getPlayers().get(0);
        PlayerActionPhase pap = PlayerActionPhaseFactory.createPlayerActionPhase(
                new Assistant(4, 8, rick), gb, true
        );

        character.fillCardFromBag();

        Entrance entrance = rick.getDashboard().getEntrance();
        DiningRoom diningRoom = rick.getDashboard().getDiningRoom();
        fillEntrance(entrance);

        ArrayList<Student> srcStudents = new ArrayList<>(entrance.getStudents().subList(0, 4));
        ArrayList<Student> dstStudents = new ArrayList<>(character.getStudents().subList(0, 4));

        assertThrows(
                InvalidActionException.class,
                () -> pap.playCharacter(character, null, null, srcStudents, dstStudents),
                "You can move up to three students"
        );
    }

}
