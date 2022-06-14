package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.GuiView;
import it.polimi.ingsw.client.gui.utils.GuiCharacterType;
import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.server.game_state.GameState;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;

import java.util.List;

public class CreateOrLoadGameController implements GuiController {

    @FXML
    private Button createGameBtn;
    @FXML
    private Button loadGame;

    @FXML
    void onLoadGameButtonClicked(MouseEvent event) {
        GuiView.getGui().getCurrentObserverHandler().notifyAllLoadGameObservers();
    }

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
