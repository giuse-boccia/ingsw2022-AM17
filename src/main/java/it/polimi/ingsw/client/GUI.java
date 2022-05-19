package it.polimi.ingsw.client;

import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.model.game_state.GameState;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.TextField;

import java.io.IOException;
import java.util.ArrayList;
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
    public int getAssistantValue() throws IOException {
        return 0;
    }

    @Override
    public int chooseAction(int bound) throws IOException {
        return 0;
    }

    @Override
    public Color askStudentColor() throws IOException {
        return null;
    }

    @Override
    public int askIslandIndex() throws IOException {
        return 0;
    }

    @Override
    public int askNumStepsOfMotherNature() throws IOException {
        return 0;
    }

    @Override
    public int askCloudIndex() throws IOException {
        return 0;
    }

    @Override
    public int askCharacterIndex() throws IOException {
        return 0;
    }

    @Override
    public void endGame(String message) {

    }

    @Override
    public void updateGameState(GameState gameState) {

    }

    @Override
    public ArrayList<Color> askColorListForSwapCharacters(int maxBound, String secondElement) throws IOException {
        return null;
    }

    @Override
    public void showAllCharactersWithIndex() throws IOException {

    }

    @FXML
    private TextField usernameTextField;

    @Override
    public int askNumPlayers() throws IOException {
        return 0;
    }

    @Override
    public boolean askExpertMode() throws IOException {
        return false;
    }

    @Override
    public void showCurrentLobby(GameLobby gameLobby) {

    }

    @Override
    public Color pickColor() {
        return null;
    }

    @Override
    public void gracefulTermination(String message) {

    }

    @Override
    public void showMessage(String message) {

    }

    @Override
    public String askUsername() throws IOException {
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        System.out.println("This is the username: " + username);
        return username;
    }

    public void onLoginBtnPressed(ActionEvent event) {
        username = usernameTextField.getText();
        synchronized (lock) {
            lock.notifyAll();
        }
    }

}
