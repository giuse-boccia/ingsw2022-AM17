package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.GuiView;
import it.polimi.ingsw.client.gui.utils.GuiCharacterType;
import it.polimi.ingsw.client.gui.utils.languages.FlagListCell;
import it.polimi.ingsw.languages.Messages;
import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.server.game_state.GameState;
import it.polimi.ingsw.utils.RandomNicknameGenerator;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.text.Text;

import java.util.List;

public class LoginController implements GuiController {

    @FXML
    private ComboBox<String> languagePicker;
    @FXML
    private Button requestUsernameButton;
    @FXML
    private Text usernameText;
    @FXML
    private TextField usernameTextField;

    @FXML
    private void initialize() {
        setTextToElements();
        languagePicker.setItems(FXCollections.observableArrayList("en", "it"));
        ListCell<String> cell = new FlagListCell();
        languagePicker.setButtonCell(cell);
        languagePicker.setCellFactory(stringListView -> new FlagListCell());
        languagePicker.setValue(Messages.getCurrentLanguageTag());
        languagePicker.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldTag, newTag) -> onLanguageSelected(newTag)
        );
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
        if (!checkConnectionSuccess()) return;
        if (username.isBlank()) {
            onRandomUsernameRequested(null);
            return;
        }
        GuiView.getGui().setTmpUsername(username);
        GuiView.getGui().getCurrentObserverHandler().notifyAllUsernameObservers(username);
    }

    /**
     * Function called when the user clicks on the Random username button
     *
     * @param event the user's click on the Random username button
     */
    public void onRandomUsernameRequested(MouseEvent event) {
        if (!checkConnectionSuccess()) return;
        String username = RandomNicknameGenerator.getRandomNickname();
        GuiView.getGui().setTmpUsername(username);
        GuiView.getGui().getCurrentObserverHandler().notifyAllUsernameObservers(username);
    }

    /**
     * Changes the language of the application according to the selected tag
     *
     * @param tag {@code String} identifying the selected language
     */
    private void onLanguageSelected(String tag) {
        Messages.initializeBundle(tag);
        setTextToElements();
    }

    /**
     * Writes the correct text message for each element in the scene
     */
    private void setTextToElements() {
        usernameText.setText(Messages.getMessage("insert_username_title"));
        requestUsernameButton.setText(Messages.getMessage("get_random_username"));
    }

    /**
     * Checks if the user has successfully connected to server; if not, the user is kicked with a popup error message
     *
     * @return true if the connection has been established successfully
     */
    private boolean checkConnectionSuccess() {
        boolean connectionSuccessful = GuiView.getGui().getCurrentObserverHandler() != null;
        if (!connectionSuccessful) {
            // There was an error connecting to server
            GuiView.getGui().gracefulTermination(Messages.getMessage("connection_failed"));
        }
        return connectionSuccessful;
    }

    @Override
    public void receiveData(GameLobby lobby, GameState gameState, List<String> actions, String username) {

    }

    @Override
    public void askCharacterParameters(CharacterName name, GuiCharacterType characterType) {

    }
}
