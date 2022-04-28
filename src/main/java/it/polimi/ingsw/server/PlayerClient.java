package it.polimi.ingsw.server;

import it.polimi.ingsw.model.Player;

public class PlayerClient {
    private final ClientHandler ch;
    private final String username;
    private Player player;

    public PlayerClient(ClientHandler ch, String username) {
        this.ch = ch;
        this.username = username;
        this.player = null;
    }

    public ClientHandler getClientHandler() {
        return ch;
    }

    public String getUsername() {
        return username;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }
}
