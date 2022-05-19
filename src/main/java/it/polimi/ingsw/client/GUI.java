package it.polimi.ingsw.client;

import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.model.game_state.GameState;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.List;

public class GUI extends Client {

    private final Object lock = new Object();
    private String username;

    public static void main(String[] args) {

    }

    @Override
    public void showPossibleActions(List<String> actions) {

    }

    @Override
    public void getAssistantValue() throws IOException {

    }

    @Override
    public void chooseAction(List<String> actions) throws IOException {
        return;
    }

    @Override
    public void askMoveStudentToDining() throws IOException {
        return;
    }

    @Override
    public void askNumStepsOfMotherNature() throws IOException {
        return;
    }

    @Override
    public void askCloudIndex() throws IOException {
        return;
    }

    @Override
    public void askCharacterIndex() throws IOException {
        return;
    }

    @Override
    public void endGame(String message) {

    }

    @Override
    public void updateGameState(GameState gameState) {

    }

    @Override
    public void askColorListForSwapCharacters(int maxBound, String secondElement, CharacterName characterName) throws IOException {
        return;
    }

    @Override
    public void showAllCharactersWithIndex() throws IOException {

    }

    @FXML
    private TextField usernameTextField;

    @Override
    public void askNumPlayersAndExpertMode() throws IOException {
        return;
    }

    @Override
    public void showCurrentLobby(GameLobby gameLobby) {

    }

    @Override
    public void gracefulTermination(String message) {

    }

    @Override
    public void showMessage(String message) {

    }

    @Override
    public void askMoveStudentToIsland() throws IOException {

    }

    @Override
    public void askToMoveOneStudentFromCard(boolean toIsland) throws IOException {

    }

    @Override
    public void pickColorForPassive(CharacterName characterName) throws IOException {

    }

    @Override
    public void askIslandIndexForCharacter(CharacterName characterName) throws IOException {

    }

    @Override
    public void askUsername() throws IOException {
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("This is the username: " + username);
    }

    public void onLoginBtnPressed(ActionEvent event) {
        username = usernameTextField.getText();
        synchronized (lock) {
            lock.notifyAll();
        }
    }

}
