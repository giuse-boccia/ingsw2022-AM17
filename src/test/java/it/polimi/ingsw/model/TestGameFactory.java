package it.polimi.ingsw.model;

import java.util.ArrayList;

public class TestGameFactory {

    /**
     * Creates a non-expert {@code Game}
     *
     * @return the created {@code Game}
     */
    public static Game getNewGame() {
        Game res = new Game(createPlayers(), true);
        res.start(0);
        return res;
    }

    /**
     * A helper method add three players to the {@code Game}
     *
     * @return the {@code ArrayList} of players added
     */
    private static ArrayList<Player> createPlayers() {
        ArrayList<Player> players = new ArrayList<>();
        players.add(new Player("Rick", 6));
        players.add(new Player("Clod", 6));
        players.add(new Player("Giuse", 6));
        return players;
    }

}
