package it.polimi.ingsw.messages.action;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.utils.constants.Messages;
import it.polimi.ingsw.messages.Message;

import java.util.ArrayList;
import java.util.List;

public class ServerActionMessage extends Message {

    private String displayText;
    private String player;
    private final List<String> actions;

    public ServerActionMessage() {
        super();
        setStatus(Messages.STATUS_ACTION);
        actions = new ArrayList<>();
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
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

    /**
     * Returns a {@code ServerActionMessage} object from a Json {@code String}
     *
     * @param json the Json {@code String}
     * @return a {@code ServerActionMessage} object from a Json {@code String}
     */
    public static ServerActionMessage fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, new TypeToken<ServerActionMessage>() {
        }.getType());
    }
}
