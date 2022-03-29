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

    private boolean gameEnded() {
        return false;
    }

    public void nextRound(int nextFirstPlayerIndex) {
        if (currentRound.isLastRound()) {
            end();
        } else {
            roundsPlayed++;
            currentRound = new Round(nextFirstPlayerIndex, this, roundsPlayed == 9);
        }
    }

    private void end() {
        isEnded = true;
        // check winner etc...
    }

    public boolean isEnded() {
        return isEnded;
    }

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
