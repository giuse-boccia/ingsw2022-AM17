package it.polimi.ingsw.messages.login;

public class GameLobby {
    private String[] players;
    private final int numPlayers;
    private final boolean isExpert;

    public GameLobby(String[] players, int numPlayers, boolean isExpert) {
        this.players = players;
        this.numPlayers = numPlayers;
        this.isExpert = isExpert;
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

    public boolean isExpert() {
        return isExpert;
    }
}
