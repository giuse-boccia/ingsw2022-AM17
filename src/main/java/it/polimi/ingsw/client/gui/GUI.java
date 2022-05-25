package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.MessageHandler;
import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.server.game_state.GameState;

import java.io.IOException;
import java.util.List;

public class GUI extends Client {

    private static final GuiView guiView = new GuiView();

    public static void main(String[] args) {

    }

    @Override
    public void showPossibleActions(List<String> actions) {
        guiView.sendMessageToController(null, null, actions, null, getUsername());
    }

    @Override
    public void getAssistantValue() {

    }

    @Override
    public void chooseAction(List<String> actions) {

    }

    @Override
    public void askMoveStudentToDining() {

    }

    @Override
    public void askNumStepsOfMotherNature() {

    }

    @Override
    public void askCloudIndex() {

    }

    @Override
    public void askCharacterIndex() {

    }

    @Override
    public void endGame(String message) {

    }

    @Override
    public void updateGameState(GameState gameState) {
        guiView.changeScene("game", true);
        guiView.sendMessageToController(null, gameState, null, null, getUsername());
    }

    @Override
    public void askColorListForSwapCharacters(int maxBound, String secondElement, CharacterName characterName) throws IOException {

    }

    @Override
    public void showAllCharactersWithIndex() {

    }


    @Override
    public void askNumPlayersAndExpertMode() {
        guiView.changeScene("game_parameters", false);
    }

    @Override
    public void showCurrentLobby(GameLobby gameLobby) {
        guiView.changeScene("lobby", false);
        guiView.sendMessageToController(gameLobby, null, null, null, getUsername());
    }

    @Override
    public void gracefulTermination(String message) {
        MessageHandler.getServerUpTask().cancel();
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
    public void askMoveStudentToIsland() {

    }

    @Override
    public void askToMoveOneStudentFromCard(boolean toIsland) {

    }

    @Override
    public void pickColorForPassive(CharacterName characterName) {

    }

    @Override
    public void askIslandIndexForCharacter(CharacterName characterName) {

    }

    @Override
    public void askUsername() {

    }

    @Override
    public void playCharacterWithoutArguments(CharacterName characterName) {

    }

}
