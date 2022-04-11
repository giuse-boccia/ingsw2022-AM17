package it.polimi.ingsw.model;

import it.polimi.ingsw.model.characters.*;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.model.game_actions.Round;
import it.polimi.ingsw.model.game_objects.TowerColor;
import it.polimi.ingsw.model.game_objects.gameboard_objects.GameBoard;
import it.polimi.ingsw.model.utils.RandomGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Game {
    private final ArrayList<Player> players;
    private final GameBoard gameBoard;
    private final boolean isExpert;
    private Round currentRound;
    private int roundsPlayed;
    private boolean isEnded;
    private final RandomGenerator rng;

    public Game(ArrayList<Player> players, boolean isExpert, int seed) {
        this.players = players;
        gameBoard = new GameBoard(this);
        this.isExpert = isExpert;
        roundsPlayed = 0;
        isEnded = false;
        this.rng = new RandomGenerator(seed);
    }

    public Game(ArrayList<Player> players, boolean isExpert) {
        this(players, isExpert, new Random().nextInt());
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
            currentRound.startPlanningPhase();
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
        int nextFirstPlayerIndex = rng.getRandomInteger(players.size());
        start(nextFirstPlayerIndex);
    }

    /**
     * Starts a {@code Game} creating the first {@code Round} with the selected {@code Player} starting
     *
     * @param nextFirstPlayerIndex the index of the {@code Player} who starts the first {@code Round}
     */
    public void start(int nextFirstPlayerIndex) {
        int motherNatureIndex = rng.getRandomInteger(12);

        for (Player player : players) {
            player.addToGame(this);
        }

        setTeams();

        gameBoard.initGameBoard(motherNatureIndex);

        currentRound = new Round(nextFirstPlayerIndex, this);
        currentRound.startPlanningPhase();
    }

    /**
     * Sets the {@code TowerColor} attribute for every player depending on how many players are in the game
     */
    private void setTeams() {
        players.get(0).setTowerColor(TowerColor.WHITE);
        players.get(1).setTowerColor(TowerColor.BLACK);
        if (players.size() == 3) {
            players.get(2).setTowerColor(TowerColor.GREY);
        } else if (players.size() == 4) {
            players.get(2).setTowerColor(TowerColor.WHITE);
            players.get(3).setTowerColor(TowerColor.BLACK);
        }

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
