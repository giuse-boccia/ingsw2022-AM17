package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.utils.GuiCharacterType;
import it.polimi.ingsw.languages.MessageResourceBundle;
import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.server.game_state.GameState;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.util.List;

public class EndGameController {

    @FXML
    private Button closeAppButton;
    @FXML
    private Text endGameText;

    @FXML
    void initialize() {
        closeAppButton.setText(MessageResourceBundle.getMessage("end_game_button_text"));
    }

    public void setEndGameMessage(String message) {
        endGameText.setText(message);
    }

    public void closeApplication(MouseEvent event) {
        System.out.println(MessageResourceBundle.getMessage("game_ended_message"));
        System.exit(0);
    }
}
