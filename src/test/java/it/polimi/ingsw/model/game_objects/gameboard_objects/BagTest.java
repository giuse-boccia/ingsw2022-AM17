package it.polimi.ingsw.model.game_objects.gameboard_objects;

import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.TestGameFactory;
import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.model.game_objects.Student;
import it.polimi.ingsw.model.game_objects.gameboard_objects.Bag;
import it.polimi.ingsw.model.utils.Students;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class BagTest {

    /**
     * Tests if the {@code Bag} has the correct number of students, and tests all the exceptions relative to the {@code Bag}
     */
    @Test
    void testBag() {
        // Does not use the Game
        Bag bag = new Bag();    // new Bag contains 24 students of each color

        Island fooIsland = new Island();

        for (int i = 0; i < 120; i++) {
            assertDoesNotThrow(() -> bag.giveStudent(fooIsland, bag.getRandStudent()));
        }

        assertThrows(EmptyBagException.class, () -> bag.giveStudent(fooIsland, bag.getRandStudent()));

        Student newStudent = new Student(Color.GREEN);
        bag.receiveStudent(newStudent);
        assertDoesNotThrow(() -> assertEquals(newStudent, bag.getRandStudent()));

        ArrayList<Student> students = fooIsland.getStudents();
        assertEquals(120, students.size());
        for (Color color : Color.values()) {
            assertEquals(24, Students.countColor(students, color));
        }

    }

}
