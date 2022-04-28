package it.polimi.ingsw.messages.login;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.messages.Message;

public class ServerLoginMessage extends Message {
    private String message;
    private String action;
    private GameLobby gameLobby;

    public ServerLoginMessage() {
        super();
        super.setStatus("login");
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public GameLobby getGameLobby() {
        return gameLobby;
    }

    public void setGameLobby(GameLobby gameLobby) {
        this.gameLobby = gameLobby;
    }

    /**
     * Deserializes a json {@code String} into the corresponding {@code LoginMessage} object
     *
     * @param json a json {@code String}
     * @return a {@code LoginMessage} object
     */
    public static ServerLoginMessage getMessageFromJSON(String json) throws JsonSyntaxException {
        Gson gson = new Gson();
        return gson.fromJson(json, new TypeToken<ServerLoginMessage>() {
        }.getType());
    }

    public String getJson() {
        Gson gson = new Gson();
        return gson.toJson(this);
    }
}


