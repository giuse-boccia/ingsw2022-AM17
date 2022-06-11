package it.polimi.ingsw.model.game_objects;

import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.exceptions.InvalidActionException;
import it.polimi.ingsw.exceptions.InvalidStudentException;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.TestGameFactory;
import it.polimi.ingsw.model.game_objects.dashboard_objects.Dashboard;
import it.polimi.ingsw.model.game_objects.dashboard_objects.Entrance;
import it.polimi.ingsw.model.game_objects.gameboard_objects.Bag;
import it.polimi.ingsw.model.game_objects.gameboard_objects.Island;
import it.polimi.ingsw.model.utils.Students;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Random;

class StudentTest {

    Game game = TestGameFactory.getNewGame();

    /**
     * Tests the movement of the students from the {@code Entrance} of a {@code Player} to their {@code DiningRoom}
     *
     * @throws EmptyBagException       if the {@code Bag} is empty
     * @throws InvalidStudentException if the {@code Student} is not valid
     */
    @Test
    void testEntranceToDining() throws EmptyBagException, InvalidStudentException, InvalidActionException {
        Bag gameBag = game.getGameBoard().getBag();
        for (int i = 0; i < game.getPlayers().size(); i++) {
            Dashboard selectedPlayerDashboard = game.getPlayers().get(i).getDashboard();
            for (int j = 0; j < 7; j++) {
                gameBag.giveStudent(selectedPlayerDashboard.getEntrance(), gameBag.getRandStudent());
            }

            ArrayList<Student> initialStudents = selectedPlayerDashboard.getEntrance().getStudents();
            assertEquals(7, initialStudents.size());

            for (int j = 0; j < 7; j++) {
                selectedPlayerDashboard.getEntrance().giveStudent(selectedPlayerDashboard.getDiningRoom(), initialStudents.get(j));
            }

            assertEquals(0, selectedPlayerDashboard.getEntrance().getStudents().size());

            int totalStudents = 0;
            for (Color color : Color.values()) {
                assertEquals(Students.countColor(initialStudents, color), selectedPlayerDashboard.getDiningRoom().getNumberOfStudentsOfColor(color));
                totalStudents += selectedPlayerDashboard.getDiningRoom().getNumberOfStudentsOfColor(color);
            }
            assertEquals(7, totalStudents);
        }
    }

    /**
     * Tests the movement of the students from the {@code Entrance} of a {@code Player} an {@code Island}
     *
     * @throws EmptyBagException       if the {@code Bag} is empty
     * @throws InvalidStudentException if the {@code Student} is not valid
     */
    @Test
    void testEntranceToIsland() throws EmptyBagException, InvalidStudentException, InvalidActionException {
        Bag bag = game.getGameBoard().getBag();
        for (Player player : game.getPlayers()) {
            Entrance playerEntrance = player.getDashboard().getEntrance();
            for (int i = 0; i < 7; i++) {
                bag.giveStudent(playerEntrance, bag.getRandStudent());
            }

            Island randomIsland = game.getGameBoard().getIslands().get(0);
            int initialIslandStudents = randomIsland.getStudents().size();

            int studentsToMove = 4;
            ArrayList<Student> studentsMoved = new ArrayList<>();
            for (int i = 0; i < initialIslandStudents; i++) {
                studentsMoved.add(randomIsland.getStudents().get(i));
            }
            for (int i = 0; i < studentsToMove; i++) {
                studentsMoved.add(playerEntrance.getStudents().get(0));
                playerEntrance.giveStudent(randomIsland, playerEntrance.getStudents().get(0));
            }

            assertEquals(7 - studentsToMove, playerEntrance.getStudents().size());
            assertEquals(studentsToMove + initialIslandStudents, randomIsland.getStudents().size());
            assertArrayEquals(studentsMoved.toArray(), randomIsland.getStudents().toArray());

        }
    }
}