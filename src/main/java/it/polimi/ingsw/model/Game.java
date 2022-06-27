package it.polimi.ingsw.model;

import it.polimi.ingsw.utils.constants.Constants;
import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.exceptions.InvalidActionException;
import it.polimi.ingsw.model.game_actions.Round;
import it.polimi.ingsw.model.game_objects.TowerColor;
import it.polimi.ingsw.model.game_objects.gameboard_objects.GameBoard;
import it.polimi.ingsw.model.utils.RandomGenerator;

import java.util.*;

public class Game {
    private final List<Player> players;
    private final boolean isExpert;
    private final RandomGenerator rng;
    private GameBoard gameBoard;
    private Round currentRound;
    private int roundsPlayed;
    private boolean isEnded;
    private List<Player> winners;

    public Game(List<Player> players, boolean isExpert, int seed) {
        this.players = players;
        gameBoard = new GameBoard(this);
        this.isExpert = isExpert;
        roundsPlayed = 0;
        isEnded = false;
        this.rng = new RandomGenerator(seed);
        winners = null;
    }

    public Game(List<Player> players, boolean isExpert) {
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
                    .mapToInt(Player::getRemainingTowers)
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
        int motherNatureIndex = rng.getRandomInteger(Constants.MAX_ISLANDS);

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
            } catch (EmptyBagException | InvalidActionException e) {
                e.printStackTrace();
            }
        }

        currentRound = new Round(nextFirstPlayerIndex, this);
        currentRound.startPlanningPhase();
    }

    /**
     * attaches players to this Game
     */
    public void resume() {
        for (Player player : players) {
            player.addToGame(this);
        }
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

    public void setGameBoard(GameBoard gameBoard) {
        this.gameBoard = gameBoard;
    }

    public Round getCurrentRound() {
        return currentRound;
    }

    public void setCurrentRound(Round currentRound) {
        this.currentRound = currentRound;
    }

    public boolean isExpert() {
        return isExpert;
    }

    public List<Player> getWinners() {
        return winners;
    }

    public int getRoundsPlayed() {
        return roundsPlayed;
    }

    public void setRoundsPlayed(int roundsPlayed) {
        this.roundsPlayed = roundsPlayed;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Game game = (Game) o;

        if (isExpert != game.isExpert) return false;
        if (roundsPlayed != game.roundsPlayed) return false;
        if (isEnded != game.isEnded) return false;
        if (!players.equals(game.players)) return false;
        if (!gameBoard.equals(game.gameBoard)) return false;
        if (!currentRound.equals(game.currentRound)) return false;
        return Objects.equals(winners, game.winners);
    }
}
