package it.polimi.ingsw.messages.login;

public class ClientLoginMessage extends LoginMessage {
    private String username;
    private int numPlayers;

    public ClientLoginMessage() {
        super();
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public int getNumPlayers() {
        return numPlayers;
    }

    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }
}
