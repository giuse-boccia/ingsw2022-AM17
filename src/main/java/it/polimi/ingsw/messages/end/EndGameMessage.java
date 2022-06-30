package it.polimi.ingsw.messages.end;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.server.game_state.GameState;
import it.polimi.ingsw.utils.constants.Constants;

public class EndGameMessage extends Message {

    private String displayText;
    private GameState gameState;

    public EndGameMessage() {
        super();
        setStatus(Constants.STATUS_END);
    }

    /**
     * Returns an {@code EndGameMessage} object from a Json {@code String}
     *
     * @param json the Json {@code String}
     * @return an {@code EndGameMessage} object from a Json {@code String}
     */
    public static EndGameMessage fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, new TypeToken<EndGameMessage>() {
        }.getType());
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }

    public GameState getGameState() {
        return gameState;
    }

    public void setGameState(GameState gameState) {
        this.gameState = gameState;
    }
}
