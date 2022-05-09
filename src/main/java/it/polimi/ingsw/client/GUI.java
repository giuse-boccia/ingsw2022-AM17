package it.polimi.ingsw.client;

import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.model.game_objects.Color;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class GUI extends Client {

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
    public ArrayList<Color> askColorListForSwapCharacters(int maxBound, String secondElement) throws IOException {
        return null;
    }

    @Override
    public void showAllCharactersWithIndex() throws IOException {

    }

    /**
     * Asks the player to input a username
     *
     * @return the username string chosen by the player
     */
    @Override
    public String askUsername() throws IOException {
        return null;
    }

    /**
     * Asks the player to input the number of players
     *
     * @return an integer from 2 to 4 indicating the desired number of player
     */
    @Override
    public int askNumPlayers() throws IOException {
        return 0;
    }

    /**
     * Asks the player to choose whether to play in expert mode or not
     *
     * @return true if the player wants to play in expert mode, false otherwise
     */
    @Override
    public boolean askExpertMode() throws IOException {
        return false;
    }

    /**
     * Shows the current state of the lobby
     *
     * @param gameLobby the {@code GameLobby} object containing the list of players
     */
    @Override
    public void showCurrentLobby(GameLobby gameLobby) {

    }

    /**
     * Asks the player to pick a color
     *
     * @return the picked color
     */
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

}
