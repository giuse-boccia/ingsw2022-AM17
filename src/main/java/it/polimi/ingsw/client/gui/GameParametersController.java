package it.polimi.ingsw.client.gui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

public class GameParametersController implements GuiController {

    @FXML
    public ChoiceBox<String> numPlayersChoiceBox;
    @FXML
    public CheckBox isGameExpert;
    @FXML
    public Button startGameBtn;
    @FXML
    private Label title;


    public GameParametersController() {
    }


    @Override
    public void receiveData(Object data) {

    }
}
