package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.languages.Messages;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

public class EndGameController {

    @FXML
    private Button closeAppButton;
    @FXML
    private Text endGameText;

    @FXML
    void initialize() {
        closeAppButton.setText(Messages.getMessage("end_game_button_text"));
    }

    public void setEndGameMessage(String message) {
        endGameText.setText(message);
    }

    public void closeApplication(MouseEvent event) {
        System.out.println(Messages.getMessage("game_ended_message"));
        System.exit(0);
    }
}
