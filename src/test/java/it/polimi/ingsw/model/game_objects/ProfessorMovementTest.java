package it.polimi.ingsw.model.game_objects;

import it.polimi.ingsw.exceptions.ProfessorAlreadyPresentException;
import it.polimi.ingsw.exceptions.ProfessorNotFoundException;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.TestGameFactory;
import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.model.game_objects.GameBoard;
import it.polimi.ingsw.model.game_objects.dashboard_objects.ProfessorRoom;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ProfessorMovementTest {

    Game game = TestGameFactory.getNewGame();
    GameBoard gb = game.getGameBoard();

    /**
     * Checks if the initialization of the {@code ProfessorRoom} has been done correctly
     */
    @Test
    public void initProfessorTest() {
        assertEquals(5, gb.getStartingProfessors().getProfessors().size());
        for (Color color : Color.values()) {
            assertTrue(gb.getStartingProfessors().hasProfessorOfColor(color));
        }
    }

    /**
     * Tests the normal movement of the professors from one {@code ProfessorRoom} to another
     *
     * @throws ProfessorNotFoundException       if the {@code Professor} to be moved is not in the starting {@code ProfessorRoom}
     * @throws ProfessorAlreadyPresentException if the {@code Professor} to be moved is already in the {@code ProfessorRoom} where it shold be moved
     */
    @Test
    public void moveProfessorsNormal() throws ProfessorNotFoundException, ProfessorAlreadyPresentException {
        ProfessorRoom pr0 = game.getPlayers().get(0).getDashboard().getProfessorRoom();
        ProfessorRoom pr1 = game.getPlayers().get(1).getDashboard().getProfessorRoom();
        ProfessorRoom pr2 = game.getPlayers().get(2).getDashboard().getProfessorRoom();
        gb.getStartingProfessors().giveProfessor(Color.GREEN, pr0);
        assertEquals(1, pr0.getProfessors().size());
        assertEquals(4, gb.getStartingProfessors().getProfessors().size());
        for (Color color : Color.values()) {
            if (color == Color.GREEN) {
                assertTrue(pr0.hasProfessorOfColor(color));
            } else {
                assertFalse(pr0.hasProfessorOfColor(color));
            }
        }
        gb.getStartingProfessors().giveProfessor(Color.BLUE, pr1);
        gb.getStartingProfessors().giveProfessor(Color.PINK, pr2);
        gb.getStartingProfessors().giveProfessor(Color.RED, pr2);
        assertEquals(1, pr0.getProfessors().size());
        assertEquals(1, pr1.getProfessors().size());
        assertEquals(2, pr2.getProfessors().size());
        assertEquals(1, gb.getStartingProfessors().getProfessors().size());

        for (Color color : Color.values()) {
            if (color == Color.GREEN) {
                assertTrue(pr0.hasProfessorOfColor(color));
            } else {
                assertFalse(pr0.hasProfessorOfColor(color));
            }
        }

        for (Color color : Color.values()) {
            if (color == Color.BLUE) {
                assertTrue(pr1.hasProfessorOfColor(color));
            } else {
                assertFalse(pr1.hasProfessorOfColor(color));
            }
        }

        for (Color color : Color.values()) {
            if (color == Color.PINK || color == Color.RED) {
                assertTrue(pr2.hasProfessorOfColor(color));
            } else {
                assertFalse(pr2.hasProfessorOfColor(color));
            }
        }

    }

    /**
     * Tests the {@code ProfessorNotFoundException} exception
     *
     * @throws ProfessorNotFoundException       if the {@code Professor} to be moved is not in the starting {@code ProfessorRoom}
     * @throws ProfessorAlreadyPresentException if the {@code Professor} to be moved is already in the {@code ProfessorRoom} where it shold be moved
     */
    @Test
    public void moveProfessorNotPresent() throws ProfessorNotFoundException, ProfessorAlreadyPresentException {
        ProfessorRoom pr0 = game.getPlayers().get(0).getDashboard().getProfessorRoom();
        ProfessorRoom pr1 = game.getPlayers().get(1).getDashboard().getProfessorRoom();
        ProfessorRoom pr2 = game.getPlayers().get(2).getDashboard().getProfessorRoom();

        gb.getStartingProfessors().giveProfessor(Color.GREEN, pr0);
        assertEquals(1, pr0.getProfessors().size());
        assertEquals(4, gb.getStartingProfessors().getProfessors().size());

        Assertions.assertThrows(ProfessorNotFoundException.class, () -> pr0.giveProfessor(Color.YELLOW, pr2));
        assertEquals(1, pr0.getProfessors().size());
        assertEquals(0, pr2.getProfessors().size());
        assertEquals(4, gb.getStartingProfessors().getProfessors().size());

    }

    /**
     * Tests the {@code ProfessorAlreadyPresentException} exception
     *
     * @throws ProfessorNotFoundException       if the {@code Professor} to be moved is not in the starting {@code ProfessorRoom}
     * @throws ProfessorAlreadyPresentException if the {@code Professor} to be moved is already in the {@code ProfessorRoom} where it shold be moved
     */
    @Test
    public void moveProfessorAlreadyPresent() throws ProfessorNotFoundException, ProfessorAlreadyPresentException {
        ProfessorRoom pr0 = game.getPlayers().get(0).getDashboard().getProfessorRoom();
        ProfessorRoom pr1 = game.getPlayers().get(1).getDashboard().getProfessorRoom();
        ProfessorRoom pr2 = game.getPlayers().get(2).getDashboard().getProfessorRoom();

        gb.getStartingProfessors().giveProfessor(Color.GREEN, pr0);
        assertEquals(1, pr0.getProfessors().size());
        assertEquals(4, gb.getStartingProfessors().getProfessors().size());

        assertThrows(ProfessorNotFoundException.class, () -> gb.getStartingProfessors().giveProfessor(Color.GREEN, pr2));
        assertThrows(ProfessorNotFoundException.class, () -> pr0.giveProfessor(Color.YELLOW, pr0));

        assertThrows(ProfessorAlreadyPresentException.class, () -> pr0.giveProfessor(Color.GREEN, pr0));

        assertEquals(1, pr0.getProfessors().size());
        assertEquals(4, gb.getStartingProfessors().getProfessors().size());

    }
}
