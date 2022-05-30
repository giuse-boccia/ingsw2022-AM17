package it.polimi.ingsw.model;

import it.polimi.ingsw.constants.Constants;
import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.model.game_actions.Round;
import it.polimi.ingsw.model.game_objects.TowerColor;
import it.polimi.ingsw.model.game_objects.gameboard_objects.GameBoard;
import it.polimi.ingsw.model.utils.RandomGenerator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Random;
import java.util.stream.Stream;

public class Game {
    private final ArrayList<Player> players;
    private final GameBoard gameBoard;
    private final boolean isExpert;
    private Round currentRound;
    private int roundsPlayed;
    private boolean isEnded;
    private final RandomGenerator rng;
    private ArrayList<Player> winners;

    public Game(ArrayList<Player> players, boolean isExpert, int seed) {
        this.players = players;
        gameBoard = new GameBoard(this);
        this.isExpert = isExpert;
        roundsPlayed = 0;
        isEnded = false;
        this.rng = new RandomGenerator(seed);
        winners = null;
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
        if (currentRound.isLastRound() || gameBoard.getBag().isEmpty()) {
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
    public void end() {
        isEnded = true;
        computeWinners();
    }

    /**
     * Creates the winners {@code ArrayList}
     */
    private void computeWinners() {
        winners = new ArrayList<>();

        HashMap<TowerColor, Integer> towerColorMapNumTowers = new HashMap<>();

        // The HashMap maps every TowerColor to number of towers of that color
        for (TowerColor color : TowerColor.values()) {

            // Array containing the number of towers of each color in the GameBoard
            // - the array has length 0 if there's no Player/Team who owns the considered TowerColor
            int[] towersOfColor = players
                    .stream()
                    .filter(player -> player.getTowerColor() == color)
                    .mapToInt(Player::getNumberOfTowers)
                    .toArray();

            if (towersOfColor.length != 0) {
                towerColorMapNumTowers.put(color, towersOfColor[0]);
            }
        }

        // Find the minimum number of towers in the HashMap
        int min = 100;
        for (TowerColor color : towerColorMapNumTowers.keySet()) {
            if (towerColorMapNumTowers.get(color) < min) {
                min = towerColorMapNumTowers.get(color);
            }
        }

        HashMap<TowerColor, Integer> towerColorMapNumProfessors = new HashMap<>();
        for (TowerColor tc : towerColorMapNumTowers.keySet()) {
            if (towerColorMapNumTowers.get(tc) == min)
                towerColorMapNumProfessors.put(tc, null);
        }

        for (TowerColor tc : towerColorMapNumProfessors.keySet()) {
            int numProfessors = players
                    .stream()
                    .filter(player -> player.getTowerColor() == tc)
                    .mapToInt(player -> player.getColorsOfOwnedProfessors().size())
                    .sum();
            towerColorMapNumProfessors.put(tc, numProfessors);
        }

        int max = -100;
        for (TowerColor tc : towerColorMapNumProfessors.keySet()) {
            if (towerColorMapNumProfessors.get(tc) > max) {
                max = towerColorMapNumProfessors.get(tc);
            }
        }

        ArrayList<TowerColor> winningColors = new ArrayList<>();
        for (TowerColor tc : towerColorMapNumProfessors.keySet()) {
            if (towerColorMapNumProfessors.get(tc) == max) {
                winningColors.add(tc);
            }
        }

        for (TowerColor tc : winningColors) {
            for (Player player : players) {
                if (player.getTowerColor() == tc) {
                    winners.add(player);
                }
            }
        }
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

        for (Player player : players) {
            try {
                int times = ((players.size() % 2) == 0) ? Constants.STUDENTS_IN_ENTRANCE_IN_TWO_OR_FOUR_PLAYER_GAME : Constants.STUDENTS_IN_ENTRANCE_IN_THREE_PLAYER_GAME;
                for (int i = 0; i < times; i++) {
                    gameBoard.getBag().giveStudent(
                            player.getDashboard().getEntrance(),
                            gameBoard.getBag().getRandStudent());
                }
            } catch (EmptyBagException e) {
                e.printStackTrace();
            }
        }

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

    public ArrayList<Player> getWinners() {
        return winners;
    }

    public int getRoundsPlayed() {
        // TODO implement this method - it was created just to make the project build
        return 0;
    }
}
