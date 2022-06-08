package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.utils.GuiCharacterType;
import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.server.game_state.GameState;

import java.util.List;

public interface GuiController {

    /**
     * Receives the data from the server and draws the correct scene
     *
     * @param lobby     the current {@code GameLobby} of the game
     * @param gameState the current {@code GameState}
     * @param actions   the list of actions the user can choose from
     * @param username  the username of the user who receives the data
     */
    void receiveData(GameLobby lobby, GameState gameState, List<String> actions, String username);

    /**
     * Asks the user the correct parameters related to the selected character
     *
     * @param name          the {@code CharacterName} of the selected character
     * @param characterType the {@code GuiCharacterType} of the selected character
     */
    void askCharacterParameters(CharacterName name, GuiCharacterType characterType);
}
