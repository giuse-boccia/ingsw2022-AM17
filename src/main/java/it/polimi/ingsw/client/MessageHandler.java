package it.polimi.ingsw.client;

import it.polimi.ingsw.client.observers.choices.action.ActionChoiceObserver;
import it.polimi.ingsw.client.observers.choices.character.CharacterChoiceObserver;
import it.polimi.ingsw.client.observers.game_actions.choose_cloud.ChooseCloudObserver;
import it.polimi.ingsw.client.observers.game_actions.move_mn.MoveMNObserver;
import it.polimi.ingsw.client.observers.game_actions.move_student.MoveStudentObserver;
import it.polimi.ingsw.client.observers.game_actions.play_assistant.PlayAssistantObserver;
import it.polimi.ingsw.client.observers.game_actions.play_character.PlayCharacterObserver;
import it.polimi.ingsw.client.observers.login.game_parameters.GameParametersObserver;
import it.polimi.ingsw.client.observers.login.load_game.LoadGameObserver;
import it.polimi.ingsw.client.observers.login.username.UsernameObserver;
import it.polimi.ingsw.constants.Constants;
import it.polimi.ingsw.constants.Messages;
import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.messages.action.ServerActionMessage;
import it.polimi.ingsw.messages.login.ClientLoginMessage;
import it.polimi.ingsw.messages.login.ServerLoginMessage;
import it.polimi.ingsw.messages.update.UpdateMessage;
import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.model.game_objects.Color;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Class which handles a single message from the server within a separate thread
 */
public class MessageHandler implements ObserverHandler {
    private final NetworkClient nc;
    private final Client client;
    private int serverUpCalls = 0;
    private static TimerTask serverUpTask;
    private final List<UsernameObserver> usernameObservers = new ArrayList<>();
    private final List<GameParametersObserver> gameParametersObservers = new ArrayList<>();
    private final List<LoadGameObserver> loadGameObservers = new ArrayList<>();
    private final List<MoveStudentObserver> moveStudentObservers = new ArrayList<>();
    private final List<MoveMNObserver> moveMNObservers = new ArrayList<>();
    private final List<ChooseCloudObserver> chooseCloudObservers = new ArrayList<>();
    private final List<PlayAssistantObserver> playAssistantObservers = new ArrayList<>();
    private final List<PlayCharacterObserver> playCharactersObservers = new ArrayList<>();
    private final List<ActionChoiceObserver> actionChoiceObservers = new ArrayList<>();
    private final List<CharacterChoiceObserver> characterChoiceObservers = new ArrayList<>();

    public MessageHandler(NetworkClient nc) {
        this.nc = nc;
        this.client = nc.getClient();
    }

    public static TimerTask getServerUpTask() {
        return serverUpTask;
    }

    /**
     * Handles a message from the server
     */
    public synchronized void handleMessage(String jsonMessage) throws IOException {
        Message message = Message.fromJson(jsonMessage);

        if (message == null) return;

        switch (message.getStatus()) {
            case Messages.STATUS_PING -> handlePing();
            case Messages.STATUS_LOGIN -> handleLogin(jsonMessage);
            case Messages.STATUS_ACTION -> parseAction(jsonMessage);
            case Messages.STATUS_UPDATE -> handleUpdate(jsonMessage);
            case Messages.STATUS_END -> handleEndGame(jsonMessage);
            default -> client.gracefulTermination(Messages.INVALID_SERVER_MESSAGE);
        }
    }

    /**
     * Handles an update message and updates the client interface
     */
    private void handleUpdate(String jsonMessage) {
        UpdateMessage updateMessage = UpdateMessage.fromJson(jsonMessage);
        client.setCharacters(updateMessage.getGameState().getCharacters());
        client.updateGameState(updateMessage.getGameState());
        client.showMessage(updateMessage.getDisplayText());
    }

    /**
     * Handles a ping message
     */
    private void handlePing() {
        sendPong();
        serverUpCalls = 0;
    }

    /**
     * Sends a "pong" message to the server
     */
    private void sendPong() {
        Message pong = new Message();
        pong.setStatus(Messages.STATUS_PONG);
        nc.sendMessageToServer(pong.toJson());
    }

    /**
     * Handles a login message
     */
    private void handleLogin(String jsonMessage) throws IOException {
        ServerLoginMessage message = ServerLoginMessage.fromJson(jsonMessage);

        if (message.getError() == 2) {
            client.showWarningMessage(Messages.USERNAME_ALREADY_TAKEN);
            new Thread(() -> {
                try {
                    askUsernameAndSend();
                } catch (IOException e) {
                    client.gracefulTermination(Messages.SERVER_LOST);
                }
            }).start();
        } else if (message.getError() == 5) {
            client.showWarningMessage(message.getDisplayText());    // username doesn't match in saved_game.json
            new Thread(() -> {
                try {
                    askUsernameAndSend();
                } catch (IOException e) {
                    client.gracefulTermination(Messages.SERVER_LOST);
                }
            }).start();
        } else if (message.getError() == 4) {
            client.showWarningMessage(message.getDisplayText());    // error while loading saved game
            new Thread(() -> {
                try {
                    client.askCreateOrLoad();
                } catch (IOException e) {
                    client.gracefulTermination("Connection to server went down");
                }
            }).start();
        } else if (message.getError() != 0) {
            client.gracefulTermination(message.getDisplayText());
        } else if (message.getAction() != null && message.getAction().equals(Messages.ACTION_CREATE_GAME)) {
            client.setUsername(client.getTmpUsername());
            new Thread(() -> {
                try {
                    client.askCreateOrLoad();
                } catch (IOException e) {
                    client.gracefulTermination(Messages.SERVER_LOST);
                }
            }).start();

        } else {    // action = null & error = 0 ----> this is a broadcast message
            client.setUsername(client.getTmpUsername());
            client.showMessage(message.getDisplayText());
            client.showCurrentLobby(message.getGameLobby());
        }
    }

    /**
     * Asks for a username and sends a login message to the server
     */
    public void askUsernameAndSend() throws IOException {
        client.setCurrentObserverHandler(this);
        client.showMessage(Messages.CONNECTING);
        client.askUsername();
    }

    /**
     * Sends a "CREATE_GAME" message to the server with the player preferences
     */
    public void sendLoadGame() {
        ClientLoginMessage msg = new ClientLoginMessage();
        msg.setUsername(client.getUsername());
        msg.setAction(Messages.ACTION_LOAD_GAME);
        nc.sendMessageToServer(msg.toJson());
    }

    public NetworkClient getNetworkClient() {
        return nc;
    }

    public void attachUsernameObserver(UsernameObserver usernameObserver) {
        usernameObservers.add(usernameObserver);
    }

    public void attachGameParametersObserver(GameParametersObserver parametersObserver) {
        gameParametersObservers.add(parametersObserver);
    }

    public void attachMoveStudentObserver(MoveStudentObserver moveStudentObserver) {
        moveStudentObservers.add(moveStudentObserver);
    }

    @Override
    public void attachMoveMNObserver(MoveMNObserver moveStudentObserver) {
        moveMNObservers.add(moveStudentObserver);
    }

    @Override
    public void attachActionChoiceObserver(ActionChoiceObserver actionChoiceObserver) {
        actionChoiceObservers.add(actionChoiceObserver);
    }

    @Override
    public void attachCharacterChoiceObserver(CharacterChoiceObserver characterChoiceObserver) {
        characterChoiceObservers.add(characterChoiceObserver);
    }

    @Override
    public void notifyActionChoiceObservers(String name) {
        actionChoiceObservers.forEach(observer -> observer.onActionChosen(name));
    }

    @Override
    public void notifyCharacterChoiceObservers(CharacterName name) {
        characterChoiceObservers.forEach(observer -> {
            try {
                observer.onCharacterChosen(name);
            } catch (IOException e) {
                client.gracefulTermination(Messages.SERVER_LOST);
            }
        });
    }

    @Override
    public void notifyMoveMNObservers(int numSteps) {
        moveMNObservers.forEach(observer -> observer.onMotherNatureMoved(numSteps));
    }

    @Override
    public void attachLoadGameObserver(LoadGameObserver loadGameObserver) {
        loadGameObservers.add(loadGameObserver);
    }

    @Override
    public void notifyAllLoadGameObservers() {
        loadGameObservers.forEach(LoadGameObserver::loadGame);
    }

    @Override
    public void attachChooseCloudObserver(ChooseCloudObserver cloudObserver) {
        chooseCloudObservers.add(cloudObserver);
    }

    @Override
    public void attachPlayAssistantObserver(PlayAssistantObserver playAssistantObserver) {
        playAssistantObservers.add(playAssistantObserver);
    }

    @Override
    public void attachPlayCharacterObserver(PlayCharacterObserver playCharacterObserver) {
        playCharactersObservers.add(playCharacterObserver);
    }

    @Override
    public void notifyPlayCharacterObservers(CharacterName name, Color color, Integer island, List<Color> srcStudents, List<Color> dstStudents) {
        playCharactersObservers.forEach(observer -> observer.onCharacterPlayed(name, color, island, srcStudents, dstStudents));
    }

    @Override
    public void notifyPlayAssistantObservers(int index) {
        playAssistantObservers.forEach(observer -> observer.onAssistantPlayed(index));
    }

    @Override
    public void notifyChooseCloudObservers(int index) {
        chooseCloudObservers.forEach(observer -> observer.onCloudChosen(index));
    }

    @Override
    public void notifyAllUsernameObservers(String message) {
        usernameObservers.forEach(observer -> observer.onUsernameEntered(message));
    }

    @Override
    public void notifyAllGameParametersObservers(int numPlayers, boolean isExpert) {
        gameParametersObservers.forEach(observer -> observer.onGameParametersSet(numPlayers, isExpert));
    }

    @Override
    public void notifyMoveStudentObservers(Color color, Integer islandIndex) {
        moveStudentObservers.forEach(observer -> observer.onStudentMoved(color, islandIndex));
    }

    /**
     * Parses an action message sent by the server
     */
    private void parseAction(String jsonMessage) {
        // Action broadcast messages does not have to have an Action field: it
        // should receive the whole model
        ServerActionMessage actionMessage = ServerActionMessage.fromJson(jsonMessage);

        if (actionMessage.getDisplayText() != null) {
            client.showMessage(actionMessage.getDisplayText());
        }

        if (actionMessage.getError() == 3) {
            client.gracefulTermination("");
            return;
        }

        if (actionMessage.getActions() == null || actionMessage.getActions().isEmpty()) {
            return;
        }

        if (actionMessage.getError() == 2 || actionMessage.getError() == 1) {
            if (actionMessage.getActions().size() == 1) {
                handleAction(actionMessage.getActions().get(0));
            } else {
                handleMultipleActions(actionMessage.getActions());
            }
            return;
        }
        if (actionMessage.getActions().size() > 1) {
            handleMultipleActions(actionMessage.getActions());
            return;
        }

        client.showMessage("Now you have to: " + actionMessage.getActions().get(0));
        String chosenAction = actionMessage.getActions().get(0);
        handleAction(chosenAction);
    }

    /**
     * Handles an action message
     */
    public void handleAction(String chosenAction) {
        new Thread(() -> {
            try {
                switch (chosenAction) {
                    case Messages.ACTION_PLAY_ASSISTANT -> ActionHandler.handlePlayAssistant(nc);
                    case Messages.ACTION_MOVE_STUDENT_TO_DINING -> ActionHandler.handleMoveStudentToDining(nc);
                    case Messages.ACTION_MOVE_STUDENT_TO_ISLAND -> ActionHandler.handleMoveStudentToIsland(nc);
                    case Messages.ACTION_MOVE_MN -> ActionHandler.handleMoveMotherNature(nc);
                    case Messages.ACTION_FILL_FROM_CLOUD -> ActionHandler.handleFillFromCloud(nc);
                    case Messages.ACTION_PLAY_CHARACTER -> ActionHandler.handlePlayCharacter(nc);
                    default -> client.gracefulTermination(Messages.INVALID_SERVER_MESSAGE);
                }
            } catch (IOException e) {
                client.gracefulTermination(Messages.SERVER_LOST);
            }

        }).start();
    }

    /**
     * Shows the user the list of possible actions they can select from and obtains the chosen one
     *
     * @param actions the list of actions the user can choose from
     */
    private void handleMultipleActions(List<String> actions) {
        new Thread(() -> {
            try {
                client.showPossibleActions(actions);
                client.chooseAction(actions);
            } catch (IOException e) {
                client.gracefulTermination(Messages.SERVER_LOST);
            }
        }).start();
    }

    /**
     * Handles the end of the game and shows a message
     *
     * @param json the Json {@code String} to put into the message
     */
    private void handleEndGame(String json) {
        ServerActionMessage actionMessage = ServerActionMessage.fromJson(json);
        client.endGame(actionMessage.getDisplayText());
    }

    /**
     * Starts a new {@code Thread} to check if the {@code Server} is still up and sending "PING" meessages
     */
    public void startPongThread() {
        Timer timer = new Timer("PONG THREAD");
        serverUpTask = new TimerTask() {
            @Override
            public void run() {
                if (serverUpCalls >= Constants.MAX_ATTEMPTS_TO_CONTACT_SERVER && client.getUsername() != null) {
                    client.gracefulTermination(Messages.SERVER_LOST);
                    this.cancel();
                } else if (client.getUsername() != null) {
                    serverUpCalls++;
                }
            }
        };
        timer.schedule(serverUpTask, Constants.PONG_INITIAL_DELAY, Constants.PONG_INTERVAL);
    }
}
