package it.polimi.ingsw.messages.login;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.messages.Message;

public class ClientLoginMessage extends Message {
    private String username;
    private int numPlayers;
    private String action;

    public ClientLoginMessage() {
        super();
        setStatus("login");
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

    public static ClientLoginMessage getMessageFromJSON(String json) throws JsonSyntaxException {
        Gson gson = new Gson();
        return gson.fromJson(json, new TypeToken<ClientLoginMessage>() {
        }.getType());
    }

    public String getAction() {
        return action;
    }

    public void setNumPlayers(int numPlayers) {
        this.numPlayers = numPlayers;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public String getJSON() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}
