package it.polimi.ingsw.messages.login;

import java.util.ArrayList;
import java.util.List;

public class GameLobby {
    private List<String> players;
    private int numPlayers;
    private boolean isExpert;
    private boolean isSaved;
    private String[] allPlayers;

    // for Controller
    public GameLobby() {
        players = new ArrayList<>();
        numPlayers = -1;
        isExpert = false;
        isSaved = false;
        allPlayers = null;
    }

    // new game
    public GameLobby(List<String> players, int numPlayers, boolean isExpert) {
        this.players = players;
        this.numPlayers = numPlayers;
        this.isExpert = isExpert;
        this.isSaved = false;
        this.allPlayers = null;
    }

    // loading from save
    public GameLobby(List<String> players, int numPlayers, boolean isExpert, boolean isSaved, String[] allPlayers) {
        this.players = players;
        this.numPlayers = numPlayers;
        this.isExpert = isExpert;
        this.isSaved = isSaved;
        this.allPlayers = allPlayers;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void setPlayers(List<String> players) {
        this.players = players;
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

    public void setExpert(boolean expert) {
        isExpert = expert;
    }

    public boolean isSaved() {
        return isSaved;
    }

    public void setSaved(boolean saved) {
        isSaved = saved;
    }

    public String[] getAllPlayers() {
        return allPlayers;
    }

    public void setAllPlayers(String[] allPlayers) {
        this.allPlayers = allPlayers;
    }
}
