package it.polimi.ingsw.messages.action;

import it.polimi.ingsw.messages.Message;

import java.util.ArrayList;
import java.util.List;

public class ServerActionMessage extends Message {

    private String message;
    private String player;
    private List<String> actions;

    public ServerActionMessage() {
        super();
        setStatus("ACTION");
        actions = new ArrayList<>();
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getPlayer() {
        return player;
    }

    public void setPlayer(String player) {
        this.player = player;
    }

    public List<String> getActions() {
        return actions;
    }

    public void addAction(String action) {
        actions.add(action);
    }
}
