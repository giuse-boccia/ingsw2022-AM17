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
    public void mergeThreeIslands1() {
        ArrayList<Island> islands = gb.getIslands();

        islands.get(11).setOwner(game.getPlayers().get(0));
        assertEquals(1, gb.getIslands().get(11).getNumOfTowers());

        islands.get(1).setOwner(game.getPlayers().get(0));
        assertEquals(1, gb.getIslands().get(1).getNumOfTowers());

        islands.get(0).setOwner(game.getPlayers().get(0));
        assertEquals(10, gb.getIslands().size());
        assertEquals(gb.getIslands().get(9).getOwner(), game.getPlayers().get(0));
        assertEquals(gb.getIslands().get(9).getNumOfTowers(), 3);
        assertEquals(islands.get(11), gb.getIslands().get(9));

        islands.get(2).setOwner(game.getPlayers().get(0));
        assertEquals(9, gb.getIslands().size());
        assertEquals(gb.getIslands().get(8).getOwner(), game.getPlayers().get(0));
        assertEquals(gb.getIslands().get(8).getNumOfTowers(), 4);
        assertEquals(islands.get(11), gb.getIslands().get(8));

        islands.get(10).setOwner(game.getPlayers().get(0));
        assertEquals(8, gb.getIslands().size());
        assertEquals(gb.getIslands().get(7).getOwner(), game.getPlayers().get(0));
        assertEquals(gb.getIslands().get(7).getNumOfTowers(), 5);
        assertEquals(islands.get(10), gb.getIslands().get(7));
    }

}