package it.polimi.ingsw.messages.login;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.messages.Message;

public class ServerLoginMessage extends Message {
    private String displayText;
    private String action;
    private GameLobby gameLobby;

    public ServerLoginMessage() {
        super();
        super.setStatus("LOGIN");
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
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
     * Returns a {@code ServerLoginMessage} object from a Json {@code String}
     *
     * @param json the Json {@code String}
     * @return a {@code ServerLoginMessage} object from a Json {@code String}
     */
    public static ServerLoginMessage fromJson(String json) throws JsonSyntaxException {
        Gson gson = new Gson();
        return gson.fromJson(json, new TypeToken<ServerLoginMessage>() {
        }.getType());
    }

}


