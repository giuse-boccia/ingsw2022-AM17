package it.polimi.ingsw.model;

import org.junit.Test;
import static org.junit.Assert.*;
import java.util.ArrayList;

public class GameBoardTest {

    Game game = new Game(createPlayers());
    GameBoard gb = new GameBoard(game);

    private ArrayList<Player> createPlayers(){
        ArrayList<Player> players = new ArrayList<>();
        players.add(new Player("Rick", game, 6));
        players.add(new Player("Clod", game, 6));
        players.add(new Player("Giuse", game, 6));
        return players;
    }

    @Test
    public void moveMotherNature1() {
        while(gb.getMotherNatureIndex() != 4){
            gb.moveMotherNature(1);
            assertFalse(gb.getMotherNatureIndex() < 0);
            assertFalse(gb.getMotherNatureIndex() >= 12);
        }
        gb.moveMotherNature(3);
        assertEquals(gb.getMotherNatureIndex(), 7);
    }

    @Test
    public void moveMotherNature2() {
        while(gb.getMotherNatureIndex() != 11) gb.moveMotherNature(1);
        gb.moveMotherNature(5);
        assertEquals(gb.getMotherNatureIndex(), 4);
    }

    @Test
    public void moveMotherNature3() {
        while(gb.getMotherNatureIndex() != 5) gb.moveMotherNature(1);   // MN starts in island 5
        ArrayList<Island> islands= gb.getIslands();

        // Merge island 7 and island 8
        islands.get(7).setOwner(game.getPlayers().get(1));
        islands.get(8).setOwner(game.getPlayers().get(1));

        // MN shouldn't move
        assertEquals(5, gb.getMotherNatureIndex());

        // Merge islands 2 and 3
        islands.get(2).setOwner(game.getPlayers().get(2));
        islands.get(3).setOwner(game.getPlayers().get(2));

        // MN should be in island 4
        assertEquals(4, gb.getMotherNatureIndex());

        // Merge islands 4 and 5
        islands.get(5).setOwner(game.getPlayers().get(2));
        islands.get(4).setOwner(game.getPlayers().get(2));

        // MN should be in island 2
        assertEquals(2, gb.getMotherNatureIndex());
    }

    @Test
    public void mergeTwoIslands1() {
        ArrayList<Island> islands = gb.getIslands();
        assertEquals(12, islands.size());

        islands.get(0).setOwner(game.getPlayers().get(0));
        assertEquals(1, gb.getIslands().get(0).getNumOfTowers());

        islands.get(1).setOwner(game.getPlayers().get(0));
        assertEquals(11, gb.getIslands().size());
        assertEquals(game.getPlayers().get(0), islands.get(0).getOwner());
        assertEquals(2, islands.get(0).getNumOfTowers());
        assertEquals(islands.get(0), gb.getIslands().get(0));

        islands.get(2).setOwner(game.getPlayers().get(1));
        islands.get(4).setOwner(game.getPlayers().get(1));
        assertEquals(11, gb.getIslands().size());
        assertEquals(islands.get(2).getOwner(), game.getPlayers().get(1));
        assertEquals(1, islands.get(2).getNumOfTowers());

    }

    @Test
    public void mergeTwoIslands2() {
        ArrayList<Island> islands = gb.getIslands();
        islands.get(11).setOwner(game.getPlayers().get(0));
        assertEquals(1, gb.getIslands().get(11).getNumOfTowers());

        islands.get(0).setOwner(game.getPlayers().get(0));
        assertEquals(11, gb.getIslands().size());
        assertEquals(gb.getIslands().get(10).getOwner(), game.getPlayers().get(0));
        assertEquals(gb.getIslands().get(10).getNumOfTowers(), 2);
        assertEquals(islands.get(11), gb.getIslands().get(10));
    }

    @Test
    public void mergeThreeIslands() {
        ArrayList<Island> islands = gb.getIslands();

        // Rick conquers island 11
        islands.get(11).setOwner(game.getPlayers().get(0));
        assertEquals(1, gb.getIslands().get(11).getNumOfTowers());

        while (gb.getMotherNatureIndex() != 1)
            gb.moveMotherNature(1);

        // Rick conquers island 1
        islands.get(1).setOwner(game.getPlayers().get(0));
        assertEquals(1, gb.getIslands().get(1).getNumOfTowers());

        // Rick conquers island 0 (expected merge of i0, i1 and i11 into i11)
        islands.get(0).setOwner(game.getPlayers().get(0));
        assertEquals(10, gb.getIslands().size());
        assertEquals(game.getPlayers().get(0), gb.getIslands().get(9).getOwner());
        assertEquals(3, gb.getIslands().get(9).getNumOfTowers());
        assertEquals(islands.get(11), gb.getIslands().get(9));
        assertEquals(9, gb.getMotherNatureIndex());

        // island 2 is now at index 0
        assertEquals(islands.get(2), gb.getIslands().get(0));

        // Rick conquers island 2 (now at index 0) (expected merge of i2, and previous i11)
        islands.get(2).setOwner(game.getPlayers().get(0));
        assertEquals(9, gb.getIslands().size());
        assertEquals(gb.getIslands().get(8).getOwner(), game.getPlayers().get(0));
        assertEquals(gb.getIslands().get(8).getNumOfTowers(), 4);
        assertEquals(islands.get(11), gb.getIslands().get(8));
        assertEquals(8, gb.getMotherNatureIndex());

        // island 10 is now at index 7
        assertEquals(islands.get(10), gb.getIslands().get(7));

        // Rick conquers island 10 (which now is at index 7) (expected merge of i10 and i0)
        islands.get(10).setOwner(game.getPlayers().get(0));
        assertEquals(8, gb.getIslands().size());
        assertEquals(gb.getIslands().get(7).getOwner(), game.getPlayers().get(0));
        assertEquals(gb.getIslands().get(7).getNumOfTowers(), 5);
        assertEquals(islands.get(10), gb.getIslands().get(7));
        assertEquals(7, gb.getMotherNatureIndex());
    }

}