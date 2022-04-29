package it.polimi.ingsw.messages.login;

public class GameLobby {
    private String[] players;
    private int numPlayers;

    public GameLobby(String[] players, int numPlayers) {
        this.players = players;
        this.numPlayers = numPlayers;
    }

    public String[] getPlayers() {
        return players;
    }

    public void setPlayers(String[] players) {
        this.players = players;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }
}
