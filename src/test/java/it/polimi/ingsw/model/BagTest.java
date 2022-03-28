package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.EmptyBagException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BagTest {

    Game game = TestGameFactory.getNewGame();

    @Test
    void testBag() {
        Bag bag = game.getGameBoard().getBag();
        Player chosenPlayer = game.getPlayers().get(0);

        for (int i = 0; i < 120; i++) {
            assertDoesNotThrow(() -> bag.giveStudent(chosenPlayer.getDashboard().getEntrance(), bag.getRandStudent()));
        }

        assertThrows(EmptyBagException.class, () -> bag.giveStudent(chosenPlayer.getDashboard().getEntrance(), bag.getRandStudent()));

        ArrayList<Student> students = chosenPlayer.getDashboard().getEntrance().getStudents();
        assertEquals(120, students.size());
        for (Color color : Color.values()) {
            assertEquals(24, Students.countColor(students, color));
        }
    }

}
