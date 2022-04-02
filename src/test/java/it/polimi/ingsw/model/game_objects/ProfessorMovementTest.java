package it.polimi.ingsw.model.game_objects;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.TestGameFactory;
import it.polimi.ingsw.model.game_objects.gameboard_objects.GameBoard;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ProfessorMovementTest {

    Game game = TestGameFactory.getNewGame();
    GameBoard gb = game.getGameBoard();

    /**
     * Checks if the initialization of the professors has been done correctly
     */
    @Test
    public void initProfessorTest() {

        assertEquals(5, gb.getProfessorsMap().size());

        for (Color color : Color.values()) {
            assertNull(gb.getOwnerOfProfessor(color));
        }
    }

    /**
     * Tests the normal change of ownership of professors
     */
    @Test
    public void moveProfessorsNormal() {
        Player rick = game.getPlayers().get(0);
        Player clod = game.getPlayers().get(1);
        Player giuse = game.getPlayers().get(2);

        // Rick now owns green professor
        gb.setOwnerOfProfessor(Color.GREEN, rick);
        assertEquals(4, this.gb.getColorsOfOwnedProfessors(null).size());
        assertEquals(rick, gb.getOwnerOfProfessor(Color.GREEN));
        for (Color color : Color.values()) {
            if (color != Color.GREEN) {
                assertFalse(rick.hasProfessor(color));
            }
        }

        // Rick now also owns red professor
        gb.setOwnerOfProfessor(Color.RED, rick);
        assertEquals(rick, gb.getOwnerOfProfessor(Color.RED));

        List<Color> ownedByRick = gb.getColorsOfOwnedProfessors(rick);
        assertTrue(ownedByRick.contains(Color.RED));
        assertTrue(ownedByRick.contains(Color.GREEN));

        // Clod now steals the green professor from Rick
        gb.setOwnerOfProfessor(Color.GREEN, clod);

        assertEquals(clod, gb.getOwnerOfProfessor(Color.GREEN));
        assertTrue(gb.getColorsOfOwnedProfessors(clod).contains(Color.GREEN));
        assertFalse(gb.getColorsOfOwnedProfessors(rick).contains(Color.GREEN));

        // Clod gets blue professor
        gb.setOwnerOfProfessor(Color.BLUE, clod);
        // Giuse now steals the red professor from Rick and takes the pink professor
        gb.setOwnerOfProfessor(Color.RED, giuse);
        gb.setOwnerOfProfessor(Color.PINK, giuse);

        // PROFESSORS OWNED:
        //----- Rick ----> 0
        //----- Clod ----> 2 (blue, green)
        //----- Clod ----> 2 (red, pink)
        assertEquals(0, gb.getColorsOfOwnedProfessors(rick).size());
        assertEquals(2, gb.getColorsOfOwnedProfessors(clod).size());
        assertEquals(2, gb.getColorsOfOwnedProfessors(giuse).size());
        assertEquals(1, gb.getColorsOfOwnedProfessors(null).size());

        for (Color color : Color.values()) {
            assertFalse(rick.hasProfessor(color));
        }

        for (Color color : Color.values()) {
            if (color == Color.BLUE || color == Color.GREEN) {
                assertTrue(clod.hasProfessor(color));
            } else {
                assertFalse(clod.hasProfessor(color));
            }
        }

        for (Color color : Color.values()) {
            if (color == Color.PINK || color == Color.RED) {
                assertTrue(giuse.hasProfessor(color));
            } else {
                assertFalse(giuse.hasProfessor(color));
            }
        }
    }
}