package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.GuiView;
import it.polimi.ingsw.client.gui.utils.GuiCharacterType;
import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.server.game_state.GameState;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;

import java.util.List;

public class CreateOrLoadGameController implements GuiController {

    /**
     * This function is called when the user clicks on the Load Game button
     *
     * @param event the user's click on the load game button
     */
    @FXML
    void onLoadGameButtonClicked(MouseEvent event) {
        GuiView.getGui().getCurrentObserverHandler().notifyAllLoadGameObservers();
    }

    /**
     * This function is called when the user clicks on the New Game button
     *
     * @param event the user's click on the new game button
     */
    @FXML
    void onNewGameButtonClicked(MouseEvent event) {
        GuiView.getGui().askNumPlayersAndExpertMode();
    }

    @Override
    public void receiveData(GameLobby lobby, GameState gameState, List<String> actions, String username) {

    }

    @Override
    public void askCharacterParameters(CharacterName name, GuiCharacterType characterType) {

    }
}
