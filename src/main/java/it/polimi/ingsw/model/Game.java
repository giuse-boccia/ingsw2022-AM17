package it.polimi.ingsw.model;

import it.polimi.ingsw.model.game_actions.Round;
import it.polimi.ingsw.model.game_objects.GameBoard;

import java.util.ArrayList;
import java.util.Random;

public class Game {
    private final ArrayList<Player> players;
    private final GameBoard gameBoard;
    private final boolean isExpert;
    private Round currentRound;
    private int roundsPlayed;
    private boolean isEnded;

    public Game(ArrayList<Player> players, boolean isExpert) {
        this.players = players;
        gameBoard = new GameBoard(this);
        this.isExpert = isExpert;
        roundsPlayed = 0;
        isEnded = false;
    }

    /**
     * If it's the last {@code Round}, it ends the {@code Game};
     * else it creates the next {@code Round}
     *
     * @param nextFirstPlayerIndex the index of the player starting in the next {@code Round}
     */
    public void nextRound(int nextFirstPlayerIndex) {
        if (currentRound.isLastRound()) {
            end();
        } else {
            roundsPlayed++;
            currentRound = new Round(nextFirstPlayerIndex, this, roundsPlayed == 9);
        }
    }

    /**
     * Ends the {@code Game}
     */
    private void end() {
        isEnded = true;
        // check winner etc...
    }

    /**
     * Checks if it's the {@code Game} has ended
     *
     * @return true if the {@code Game} has ended
     */
    public boolean isEnded() {
        return isEnded;
    }

    /**
     * Starts the {@code Game} creating the first {@code Round}
     */
    public void start() {
        int nextFirstPlayerIndex = new Random().nextInt(players.size());
        currentRound = new Round(nextFirstPlayerIndex, this);
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
