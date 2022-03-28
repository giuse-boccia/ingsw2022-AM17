package it.polimi.ingsw.model;

import java.util.ArrayList;

public class TestGameFactory {

    public static Game getNewGame() {
        return new Game(createPlayers(), false);
    }

    private static ArrayList<Player> createPlayers() {
        ArrayList<Player> players = new ArrayList<>();
        players.add(new Player("Rick", 6));
        players.add(new Player("Clod", 6));
        players.add(new Player("Giuse", 6));
        return players;
    }

}
