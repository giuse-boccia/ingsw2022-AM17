package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.utils.GuiCharacterType;
import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.server.game_state.GameState;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.util.List;

public class LoginController implements GuiController {

    @FXML
    private TextField usernameTextField;

    public LoginController() {

    }

    public void onLoginBtnPressed(ActionEvent event) {
        String username = usernameTextField.getText();
        GuiView.getGui().setTmpUsername(username);
        GuiView.getGui().getCurrentObserverHandler().notifyAllUsernameObservers(username);
    }

    @Override
    public void receiveData(GameLobby lobby, GameState gameState, List<String> actions, String username) {

    }

    @Override
    public void askCharacterParameters(CharacterName name, GuiCharacterType characterType) {

    }
}
