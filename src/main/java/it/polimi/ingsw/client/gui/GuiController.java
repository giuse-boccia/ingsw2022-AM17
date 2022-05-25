package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.server.game_state.GameState;

import java.util.List;

public interface GuiController {

    void receiveData(GameLobby lobby, GameState gameState, List<String> actions, String message, String username);

}
