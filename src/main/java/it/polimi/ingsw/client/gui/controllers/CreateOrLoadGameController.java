package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.GuiView;
import it.polimi.ingsw.client.gui.utils.GuiCharacterType;
import it.polimi.ingsw.languages.MessageResourceBundle;
import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.server.game_state.GameState;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;

import java.util.List;

public class CreateOrLoadGameController implements GuiController {

    @FXML
    private Text askCreateOrLoadTitle;
    @FXML
    private Button loadGameBtn;
    @FXML
    private Button newGameBtn;

    @FXML
    private void initialize() {
        askCreateOrLoadTitle.setText(MessageResourceBundle.getMessage("ask_create_or_load"));
        newGameBtn.setText(MessageResourceBundle.getMessage("new_game"));
        loadGameBtn.setText(MessageResourceBundle.getMessage("load_game"));
    }

    /**
     * This function is called when the user clicks on the Load Game button
     *
     * @param event the user's click on the load game button
     */
    @FXML
    private void onLoadGameButtonClicked(MouseEvent event) {
        GuiView.getGui().getCurrentObserverHandler().notifyAllLoadGameObservers();
    }

    /**
     * This function is called when the user clicks on the New Game button
     *
     * @param event the user's click on the new game button
     */
    @FXML
    private void onNewGameButtonClicked(MouseEvent event) {
        GuiView.getGui().askNumPlayersAndExpertMode();
    }

    @Override
    public void receiveData(GameLobby lobby, GameState gameState, List<String> actions, String username) {

    }

    @Override
    public void askCharacterParameters(CharacterName name, GuiCharacterType characterType) {

    }

    @FXML
    private void onKeyPressedOnLoadGame(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            onLoadGameButtonClicked(null);
        }
    }

    @FXML
    private void onKeyPressedOnNewGame(KeyEvent keyEvent) {
        if (keyEvent.getCode().equals(KeyCode.ENTER)) {
            onNewGameButtonClicked(null);
        }
    }
}
