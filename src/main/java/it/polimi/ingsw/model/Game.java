package it.polimi.ingsw.model;

import it.polimi.ingsw.model.game_actions.Round;
import it.polimi.ingsw.model.game_objects.GameBoard;

import java.util.ArrayList;
import java.util.Random;

public class Game {
    private final ArrayList<Player> players;
    private final GameBoard gameBoard;
    private final Round currentRound;
    private final boolean isExpert;

    public Game(ArrayList<Player> players, boolean isExpert) {
        this.players = players;
        gameBoard = new GameBoard(this);
        this.isExpert = isExpert;
        currentRound = createNewRound();
    }

    private boolean gameEnded() {
        return false;
    }

    private Round createNewRound() {
        int firstPlayerIndex = new Random().nextInt(players.size());
        return new Round(firstPlayerIndex, this);
    }

    public void start() {


        // Create new Round
    }

    public ArrayList<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public Round getCurrentRound() {
        return currentRound;
    }

    public boolean isExpert() {
        return isExpert;
    }
}
