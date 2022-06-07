package it.polimi.ingsw.model.game_objects.gameboard_objects;

import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.TestGameFactory;
import it.polimi.ingsw.model.game_objects.Student;
import it.polimi.ingsw.model.game_objects.gameboard_objects.Bag;
import it.polimi.ingsw.model.game_objects.gameboard_objects.Cloud;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CloudTest {

    Game game = TestGameFactory.getNewGame();

    /**
     * Tests all the methods relative to the clouds
     */
    @Test
    void testCloud() {
        ArrayList<Cloud> clouds = game.getGameBoard().getClouds();
        Bag bag = game.getGameBoard().getBag();

        assertEquals(3, clouds.size());

        for (int i = 0; i < clouds.size(); i++) {
            int index = i;
            assertEquals(4, clouds.get(i).getMaxStudents());
            assertDoesNotThrow(() -> clouds.get(index).fillFromBag(bag));
            List<Student> studentsInCloud = clouds.get(index).getStudents();
            assertDoesNotThrow(() -> clouds.get(index).emptyTo(game.getPlayers().get(index).getDashboard().getEntrance()));
            // Check if all the students on the cloud have gone to the entrance of the player
            assertArrayEquals(studentsInCloud.toArray(new Student[0]),
                    game.getPlayers().get(index).getDashboard().getEntrance().getStudents().toArray(new Student[0]));
        }

        for (Player player : game.getPlayers()) {
            assertEquals(clouds.size() + 1, player.getDashboard().getEntrance().getStudents().size());
        }
    }

}
