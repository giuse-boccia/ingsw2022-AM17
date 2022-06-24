package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.GuiView;
import it.polimi.ingsw.client.gui.utils.GuiCharacterType;
import it.polimi.ingsw.languages.MessageResourceBundle;
import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.server.game_state.GameState;
import it.polimi.ingsw.utils.RandomNicknameGenerator;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.util.List;

public class LoginController implements GuiController {

    @FXML
    private Button requestUsernameButton;
    @FXML
    private Text usernameText;
    @FXML
    private TextField usernameTextField;

    @FXML
    private void initialize() {
        usernameText.setText(MessageResourceBundle.getMessage("insert_username_title"));
        requestUsernameButton.setText(MessageResourceBundle.getMessage("get_random_username"));
    }

    public LoginController() {

    }

    /**
     * This function is called when the username TextField is focused and the user presses a key
     * Pressing the Enter key has the same effect of clicking the Login button
     *
     * @param keyEvent the user's press of a key
     */
    public void onKeyPressed(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            onLoginBtnPressed(null);
        }
    }

    /**
     * Notifies all the correct attached observers when the user clicks on the login button
     *
     * @param event the user's click on the login button
     */
    public void onLoginBtnPressed(ActionEvent event) {
        String username = usernameTextField.getText();
        if (username.isBlank()) {
            onRandomUsernameRequested(null);
            return;
        }
        GuiView.getGui().setTmpUsername(username);
        GuiView.getGui().getCurrentObserverHandler().notifyAllUsernameObservers(username);
    }

    public void onRandomUsernameRequested(MouseEvent event) {
        String username = RandomNicknameGenerator.getRandomNickname();
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
