package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.Client;
import it.polimi.ingsw.client.MessageHandler;
import it.polimi.ingsw.client.gui.utils.DrawingConstants;
import it.polimi.ingsw.client.gui.utils.GuiCharacterType;
import it.polimi.ingsw.constants.Constants;
import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.server.game_state.GameState;
import java.util.List;

public class GUI extends Client {

    private static final GuiView guiView = new GuiView();
    private boolean isGameEnded = false, isGameStarted = false;

    @Override
    public void showPossibleActions(List<String> actions) {
        guiView.sendMessageToController(null, null, actions, getUsername());
    }

    @Override
    public void getAssistantValue() {
        guiView.sendMessageToController(null, null, List.of(Constants.ACTION_PLAY_ASSISTANT), getUsername());
    }

    @Override
    public void askNumStepsOfMotherNature() {
        guiView.sendMessageToController(null, null, List.of(Constants.ACTION_MOVE_MN), getUsername());
    }

    @Override
    public void askCloudIndex() {
        guiView.sendMessageToController(null, null, List.of(Constants.ACTION_FILL_FROM_CLOUD), getUsername());
    }

    @Override
    public void endGame(String message) {
        isGameEnded = true;
        MessageHandler.getServerUpTask().cancel();
        guiView.endGame(message);
    }

    @Override
    public void updateGameState(GameState gameState) {
        if (!isGameStarted) {
            isGameStarted = true;
            guiView.startGameScene();
        }
        guiView.sendMessageToController(null, gameState, null, getUsername());
    }

    @Override
    public void askColorListForSwapCharacters(int maxBound, String secondElement, CharacterName characterName) {
        guiView.askCharacterParameters(characterName, GuiCharacterType.SWAP);
    }

    @Override
    public void askNumPlayersAndExpertMode() {
        guiView.changeScene(DrawingConstants.RESOURCE_PARAMETERS, false);
    }

    @Override
    public void showCurrentLobby(GameLobby gameLobby) {
        guiView.changeScene(DrawingConstants.RESOURCE_LOBBY, false);
        guiView.sendMessageToController(gameLobby, null, null, getUsername());
    }

    @Override
    public void gracefulTermination(String message) {
        if (isGameEnded) return;
        if (MessageHandler.getServerUpTask() != null) {
            MessageHandler.getServerUpTask().cancel();
        }
        guiView.showErrorDialog(message, true);
    }

    @Override
    public void showMessage(String message) {
        guiView.showToast(message);
    }

    @Override
    public void showWarningMessage(String message) {
        guiView.showErrorDialog(message, false);
    }

    @Override
    public void askToMoveOneStudentFromCard(boolean toIsland) {
        CharacterName name = toIsland ? CharacterName.move1FromCardToIsland : CharacterName.move1FromCardToDining;
        guiView.askCharacterParameters(name, GuiCharacterType.MOVE_ONE_STUDENT_AWAY);
    }

    @Override
    public void pickColorForPassive(CharacterName characterName) {
        guiView.askCharacterParameters(characterName, GuiCharacterType.COLOR);
    }

    @Override
    public void askIslandIndexForCharacter(CharacterName characterName) {
        guiView.askCharacterParameters(characterName, GuiCharacterType.ISLAND);
    }

    @Override
    public void playCharacterWithoutArguments(CharacterName characterName) {
        getCurrentObserverHandler().notifyPlayCharacterObservers(characterName, null, null, null, null);
    }

    @Override
    public void askCreateOrLoad() {
        guiView.changeScene(DrawingConstants.RESOURCE_CREATE_OR_LOAD_GAME, false);
    }

    @Override
    public void askUsername() {

    }

    @Override
    public void chooseAction(List<String> actions) {

    }

    @Override
    public void askMoveStudentToDining() {

    }

    @Override
    public void askCharacterIndex() {

    }

    @Override
    public void showAllCharactersWithIndex() {

    }

    @Override
    public void askMoveStudentToIsland() {

    }
}
