package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.GuiView;
import it.polimi.ingsw.client.gui.utils.GuiCharacterType;
import it.polimi.ingsw.languages.Messages;
import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.server.game_state.GameState;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;

import java.util.List;

public class GameParametersController implements GuiController {

    private final ObservableList<Integer> numPlayersChoices = FXCollections.observableArrayList(2, 3, 4);
    @FXML
    private Label numPlayersLabel;
    @FXML
    private ChoiceBox<Integer> numPlayersChoiceBox;
    @FXML
    private Label expertGameLabel;
    @FXML
    private CheckBox isGameExpert;
    @FXML
    private Label title;

    /**
     * Initializes the page which the user selects the game parameters from
     */
    @FXML
    void initialize() {
        title.setText(Messages.getMessage("welcome") + ", " + GuiView.getGui().getUsername());

        numPlayersLabel.setText(Messages.getMessage("num_players_title"));
        expertGameLabel.setText(Messages.getMessage("expert_mode_title"));

        numPlayersChoiceBox.setValue(2);
        numPlayersChoiceBox.setItems(numPlayersChoices);

        isGameExpert.setAllowIndeterminate(false);
    }

    public GameParametersController() {
    }

    /**
     * Notifies all the correct attached observers when the user clicks the start button
     *
     * @param event the user's click on the start button
     */
    public void onStartButtonClicked(ActionEvent event) {
        GuiView.getGui().getCurrentObserverHandler().notifyAllGameParametersObservers(
                numPlayersChoiceBox.getValue(), isGameExpert.isSelected()
        );
    }


    @Override
    public void receiveData(GameLobby lobby, GameState gameState, List<String> actions, String username) {

    }

    @Override
    public void askCharacterParameters(CharacterName name, GuiCharacterType characterType) {

    }
}
