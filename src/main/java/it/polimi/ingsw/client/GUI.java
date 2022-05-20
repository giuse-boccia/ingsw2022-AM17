package it.polimi.ingsw.client;

import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.model.game_state.GameState;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.util.List;

public class GUI extends Client {

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


    @Override
    public void askNumPlayersAndExpertMode() throws IOException {
        GuiView.changeScene("game_parameters", false, null);
    }

    @Override
    public void showCurrentLobby(GameLobby gameLobby) throws IOException {
        GuiView.changeScene("lobby", false, null);
    }

    @Override
    public void gracefulTermination(String message) {
        // TODO show a popup before crashing
        GuiView.showErrorDialog(message, true);
    }

    @Override
    public void showMessage(String message) {

    }

    @Override
    public void showWarningMessage(String message) {
        GuiView.showErrorDialog(message, false);
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
    public void askUsername() {

    }

    @Override
    public void playCharacterWithoutArguments(CharacterName characterName) throws IOException {

    }

}
