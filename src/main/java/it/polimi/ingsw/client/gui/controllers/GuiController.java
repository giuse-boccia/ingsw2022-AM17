package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.utils.GuiCharacterType;
import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.server.game_state.GameState;

import java.util.List;

public interface GuiController {

    void receiveData(GameLobby lobby, GameState gameState, List<String> actions, String username);

    void askCharacterParameters(CharacterName name, GuiCharacterType characterType);
}
