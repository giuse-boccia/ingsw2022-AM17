package it.polimi.ingsw.model.game_objects;

import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.TestGameFactory;
import it.polimi.ingsw.model.game_objects.Bag;
import it.polimi.ingsw.model.game_objects.Cloud;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CloudTest {

    Game game = TestGameFactory.getNewGame();

    /**
     * Tests all the methods relative to the clouds
     */
    @Test
    void testCloud() {
        ArrayList<Cloud> clouds = game.getGameBoard().getClouds();
        Bag bag = game.getGameBoard().getBag();

        assertEquals(clouds.size(), game.getPlayers().size());

        for (int i = 0; i < clouds.size(); i++) {
            try {
                clouds.get(i).fillFromBag(bag);
            } catch (EmptyBagException e) {
                e.printStackTrace();
            }
            clouds.get(i).emptyTo(game.getPlayers().get(i).getDashboard().getEntrance());
        }

        for (Player player : game.getPlayers()) {
            assertEquals(clouds.size() + 1, player.getDashboard().getEntrance().getStudents().size());
        }
    }

}
