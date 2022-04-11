package it.polimi.ingsw.model;

import it.polimi.ingsw.model.characters.Character;

import java.util.ArrayList;

public class TestGameFactory {

    /**
     * Creates an expert {@code Game}
     *
     * @return the created {@code Game}
     */
    public static Game getNewGame() {
        Game res = new Game(createPlayers(), true);
        res.start(0);
        return res;
    }

    /**
     * Creates an expert 4-player {@code Game}
     *
     * @return the created {@code Game}
     */
    public static Game getNewFourPlayersGame() {
        Game res = new Game(createFourPlayers(), true);
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

    /**
     * A helper method add four players to the {@code Game}
     *
     * @return the {@code ArrayList} of players added
     */
    private static ArrayList<Player> createFourPlayers() {
        ArrayList<Player> players = new ArrayList<>();
        players.add(new Player("Rick", 8));
        players.add(new Player("Clod", 8));
        players.add(new Player("Giuse", 8));
        players.add(new Player("Fabio", 8));
        return players;
    }

}
