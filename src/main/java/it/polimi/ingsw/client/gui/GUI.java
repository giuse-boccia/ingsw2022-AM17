package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.MessageHandler;
import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.server.game_state.GameState;

import java.io.IOException;
import java.util.List;

public class GUI extends Client {

    private static final GuiView guiView = new GuiView();

    public static void main(String[] args) {

    }

    @Override
    public void showPossibleActions(List<String> actions) {
        guiView.sendMessageToController(null, null, actions, getUsername());
    }

    @Override
    public void getAssistantValue() {
        guiView.sendMessageToController(null, null, List.of("PLAY_ASSISTANT"), getUsername());
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
        guiView.sendMessageToController(null, gameState, null, getUsername());
    }

    @Override
    public void askColorListForSwapCharacters(int maxBound, String secondElement, CharacterName characterName) {
        guiView.askCharacterParameters(characterName, false, false, true, false);
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
        guiView.sendMessageToController(gameLobby, null, null, getUsername());
    }

    @Override
    public void gracefulTermination(String message) {
        if (MessageHandler.getServerUpTask() != null) {
            MessageHandler.getServerUpTask().cancel();
        }
        GuiView.showErrorDialog(message, true);
    }

    @Override
    public void showMessage(String message) {
        guiView.showToast(message);
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
        CharacterName name = toIsland ? CharacterName.move1FromCardToIsland : CharacterName.move1FromCardToDining;
        guiView.askCharacterParameters(name, false, false, false, true);
    }

    @Override
    public void pickColorForPassive(CharacterName characterName) {
        guiView.askCharacterParameters(characterName, true, false, false, false);
    }

    @Override
    public void askIslandIndexForCharacter(CharacterName characterName) {
        guiView.askCharacterParameters(characterName, false, true, false, false);
    }

    @Override
    public void askUsername() {

    }

    @Override
    public void playCharacterWithoutArguments(CharacterName characterName) {
        getCurrentObserver().sendActionParameters("PLAY_CHARACTER", null, null, null, null,
                null, characterName, null, null);
    }

    @Override
    public void askCreateOrLoad() throws IOException {
        // TODO: implement
    }

}
