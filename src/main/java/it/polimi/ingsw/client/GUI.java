package it.polimi.ingsw.client;

import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.model.game_state.GameState;
import javafx.scene.control.Alert;

import java.io.IOException;
import java.util.List;

public class GUI extends Client {

    private static final GuiView guiView = new GuiView();

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

    }

    @Override
    public void askMoveStudentToDining() throws IOException {

    }

    @Override
    public void askNumStepsOfMotherNature() throws IOException {

    }

    @Override
    public void askCloudIndex() throws IOException {

    }

    @Override
    public void askCharacterIndex() throws IOException {

    }

    @Override
    public void endGame(String message) {

    }

    @Override
    public void updateGameState(GameState gameState) {
        guiView.changeScene("game", true, gameState);
    }

    @Override
    public void askColorListForSwapCharacters(int maxBound, String secondElement, CharacterName characterName) throws IOException {

    }

    @Override
    public void showAllCharactersWithIndex() throws IOException {

    }


    @Override
    public void askNumPlayersAndExpertMode() throws IOException {
        guiView.changeScene("game_parameters", false, null);
    }

    @Override
    public void showCurrentLobby(GameLobby gameLobby) throws IOException {
        guiView.changeScene("lobby", false, gameLobby);
    }

    @Override
    public void gracefulTermination(String message) {
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
