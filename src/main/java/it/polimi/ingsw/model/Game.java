package it.polimi.ingsw.model;

import java.util.ArrayList;

public class Game {
    private final ArrayList<Player> players;
    private final GameBoard gameBoard;
    private final Round currentRound;

    public Game(ArrayList<Player> players) {
        this.players = players;
        gameBoard = new GameBoard(this);
        currentRound = new Round();
    }

    private boolean gameEnded(){
        return false;
    }

    public void setup(){

    }

    public void start(){

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
}
