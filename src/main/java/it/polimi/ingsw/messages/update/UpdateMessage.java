package it.polimi.ingsw.messages.update;

import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.model.game_state.GameState;

public class UpdateMessage extends Message {
    private GameState gameState;
    private String displayText;

    public UpdateMessage() {
        super();
        setStatus("Update");
    }

    public GameState getGameStatus() {
        return gameState;
    }

    public void setGameStatus(GameState gameState) {
        this.gameState = gameState;
    }

    public String getDisplayText() {
        return displayText;
    }

    public void setDisplayText(String displayText) {
        this.displayText = displayText;
    }
}
