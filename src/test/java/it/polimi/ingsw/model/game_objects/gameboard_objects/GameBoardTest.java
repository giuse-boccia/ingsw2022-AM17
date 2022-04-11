package it.polimi.ingsw.model.game_objects.gameboard_objects;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.TestGameFactory;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;

public class GameBoardTest {

    Game game = TestGameFactory.getNewGame();
    GameBoard gb = game.getGameBoard();

    /**
     * Tests MotherNature movement when its index does not exceed 11
     */
    @Test
    public void moveMotherNature1() {
        gb.setMotherNatureIndex(8);
        while (gb.getMotherNatureIndex() != 4) {
            gb.moveMotherNature(1);
            assertFalse(gb.getMotherNatureIndex() < 0);
            assertFalse(gb.getMotherNatureIndex() >= 12);
        }
        gb.moveMotherNature(3);
        assertEquals(gb.getMotherNatureIndex(), 7);
    }

    /**
     * Tests MotherNature movement when its index exceeds 11
     */
    @Test
    public void moveMotherNature2() {
        gb.setMotherNatureIndex(11);
        gb.moveMotherNature(5);
        assertEquals(4, gb.getMotherNatureIndex());
    }

    /**
     * Tests the movement of MotherNature related to the island merging
     */
    @Test
    public void moveMotherNature3() {
        gb.setMotherNatureIndex(5);   // MN starts in island 5
        ArrayList<Island> islands = gb.getIslands();

        // Merge island 7 and island 8
        gb.setIslandOwner(islands.get(7), game.getPlayers().get(1).getTowerColor());
        gb.setIslandOwner(islands.get(8), game.getPlayers().get(1).getTowerColor());

        // MN shouldn't move
        assertEquals(5, gb.getMotherNatureIndex());

        // Merge islands 2 and 3
        gb.setIslandOwner(islands.get(2), game.getPlayers().get(2).getTowerColor());
        gb.setIslandOwner(islands.get(3), game.getPlayers().get(2).getTowerColor());

        // MN should be in island 4
        assertEquals(4, gb.getMotherNatureIndex());

        // Merge islands 4 and 5
        gb.setIslandOwner(islands.get(5), game.getPlayers().get(2).getTowerColor());
        gb.setIslandOwner(islands.get(4), game.getPlayers().get(2).getTowerColor());

        // MN should be in island 2
        assertEquals(2, gb.getMotherNatureIndex());
    }

    /**
     * Tests the merging of two islands
     */
    @Test
    public void mergeTwoIslands1() {
        ArrayList<Island> islands = gb.getIslands();
        assertEquals(12, islands.size());

        gb.setIslandOwner(islands.get(0), game.getPlayers().get(0).getTowerColor());
        assertEquals(1, gb.getIslands().get(0).getNumOfTowers());

        gb.setIslandOwner(islands.get(1), game.getPlayers().get(0).getTowerColor());
        assertEquals(11, gb.getIslands().size());
        assertEquals(game.getPlayers().get(0).getTowerColor(), islands.get(0).getTowerColor());
        assertEquals(2, islands.get(0).getNumOfTowers());
        assertEquals(islands.get(0), gb.getIslands().get(0));

        gb.setIslandOwner(islands.get(2), game.getPlayers().get(1).getTowerColor());
        gb.setIslandOwner(islands.get(4), game.getPlayers().get(1).getTowerColor());
        assertEquals(11, gb.getIslands().size());
        assertEquals(islands.get(2).getTowerColor(), game.getPlayers().get(1).getTowerColor());
        assertEquals(1, islands.get(2).getNumOfTowers());

    }

    /**
     * Tests the merging of two islands
     */
    @Test
    public void mergeTwoIslands2() {
        ArrayList<Island> islands = gb.getIslands();
        gb.setIslandOwner(islands.get(11), game.getPlayers().get(0).getTowerColor());
        assertEquals(1, gb.getIslands().get(11).getNumOfTowers());

        gb.setIslandOwner(islands.get(0), game.getPlayers().get(0).getTowerColor());
        assertEquals(11, gb.getIslands().size());
        assertEquals(gb.getIslands().get(10).getTowerColor(), game.getPlayers().get(0).getTowerColor());
        assertEquals(gb.getIslands().get(10).getNumOfTowers(), 2);
        assertEquals(islands.get(11), gb.getIslands().get(10));
    }

    /**
     * Tests the merging of three islands
     */
    @Test
    public void mergeThreeIslands() {
        ArrayList<Island> islands = gb.getIslands();

        // Rick conquers island 11
        gb.setIslandOwner(islands.get(11), game.getPlayers().get(0).getTowerColor());
        assertEquals(1, gb.getIslands().get(11).getNumOfTowers());

        // Move mother nature to island 1
        gb.setMotherNatureIndex(1);

        // Rick conquers island 1
        gb.setIslandOwner(islands.get(1), game.getPlayers().get(0).getTowerColor());
        assertEquals(1, gb.getIslands().get(1).getNumOfTowers());
        assertEquals(1, gb.getMotherNatureIndex());

        // Rick conquers island 0 (expected merge of i0, i1 and i11 into i11)
        gb.setIslandOwner(islands.get(0), game.getPlayers().get(0).getTowerColor());
        assertEquals(10, gb.getIslands().size());
        assertEquals(game.getPlayers().get(0).getTowerColor(), gb.getIslands().get(9).getTowerColor());
        assertEquals(3, gb.getIslands().get(9).getNumOfTowers());
        assertEquals(islands.get(11), gb.getIslands().get(9));
        assertEquals(9, gb.getMotherNatureIndex());

        // island 2 is now at index 0
        assertEquals(islands.get(2), gb.getIslands().get(0));

        // Rick conquers island 2 (now at index 0) (expected merge of i2, and previous i11)
        gb.setIslandOwner(islands.get(2), game.getPlayers().get(0).getTowerColor());
        assertEquals(9, gb.getIslands().size());
        assertEquals(gb.getIslands().get(8).getTowerColor(), game.getPlayers().get(0).getTowerColor());
        assertEquals(gb.getIslands().get(8).getNumOfTowers(), 4);
        assertEquals(islands.get(11), gb.getIslands().get(8));
        assertEquals(8, gb.getMotherNatureIndex());

        // island 10 is now at index 7
        assertEquals(islands.get(10), gb.getIslands().get(7));

        // Rick conquers island 10 (which now is at index 7) (expected merge of i10 and i0)
        gb.setIslandOwner(islands.get(10), game.getPlayers().get(0).getTowerColor());
        assertEquals(8, gb.getIslands().size());
        assertEquals(gb.getIslands().get(7).getTowerColor(), game.getPlayers().get(0).getTowerColor());
        assertEquals(gb.getIslands().get(7).getNumOfTowers(), 5);
        assertEquals(islands.get(10), gb.getIslands().get(7));
        assertEquals(7, gb.getMotherNatureIndex());
    }
}