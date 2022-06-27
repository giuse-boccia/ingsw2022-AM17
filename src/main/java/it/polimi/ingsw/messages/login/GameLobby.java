package it.polimi.ingsw.messages.login;

import java.util.ArrayList;
import java.util.List;

public class GameLobby {
    private List<String> players;
    private int numPlayers;
    private boolean isExpert;
    private boolean isFromSavedGame;
    private String[] playersFromSavedGame;

    // for Controller
    public GameLobby() {
        this.players = new ArrayList<>();
        this.numPlayers = -1;
        this.isExpert = false;
        this.isFromSavedGame = false;
        this.playersFromSavedGame = null;
    }

    // new game
    public GameLobby(List<String> players, int numPlayers, boolean isExpert) {
        this.players = players;
        this.numPlayers = numPlayers;
        this.isExpert = isExpert;
        this.isFromSavedGame = false;
        this.playersFromSavedGame = null;
    }

    // loading from save
    public GameLobby(List<String> players, int numPlayers, boolean isExpert, boolean isFromSavedGame, String[] playersFromSavedGame) {
        this.players = players;
        this.numPlayers = numPlayers;
        this.isExpert = isExpert;
        this.isFromSavedGame = isFromSavedGame;
        this.playersFromSavedGame = playersFromSavedGame;
    }

    /**
     * Resets the game preferences (isExpert and numPlayers), but keeps the players list
     */
    public void resetPreferences() {
        this.numPlayers = -1;
        this.isExpert = false;
        this.isFromSavedGame = false;
        this.playersFromSavedGame = null;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
    }

    public void addPlayer(String newPlayer) {
        players.add(newPlayer);
    }

    public void removePlayer(int toRemove) {
        players.remove(toRemove);
    }

    public void removePlayer(String toRemove) {
        players.remove(toRemove);
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    public boolean isExpert() {
        return isExpert;
    }

    public void setIsExpert(boolean expert) {
        isExpert = expert;
    }

    public boolean isFromSavedGame() {
        return isFromSavedGame;
    }

    public void setFromSavedGame(boolean fromSavedGame) {
        isFromSavedGame = fromSavedGame;
    }

    public String[] getPlayersFromSavedGame() {
        return playersFromSavedGame;
    }

    /**
     * Sets the list of all players and sets the game lobby as
     *
     * @param playersFromSavedGame an array of strings containing the usernames of all the players
     */
    public void setPlayersFromSavedGame(String[] playersFromSavedGame) {
        this.playersFromSavedGame = playersFromSavedGame;
        this.isFromSavedGame = true;
    }

    /**
     * Clears the game lobby, removing all players and resetting all game preferences
     */
    public void clear() {
        players.clear();
        resetPreferences();
    }
}
