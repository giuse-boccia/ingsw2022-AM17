package it.polimi.ingsw.client;

import it.polimi.ingsw.controller.ActionHandler;
import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.messages.action.ServerActionMessage;
import it.polimi.ingsw.messages.login.ClientLoginMessage;
import it.polimi.ingsw.messages.login.ServerLoginMessage;

import java.io.IOException;

/**
 * Class which handles a single message from the server within a separate thread
 */
public class MessageHandler {
    private final NetworkClient nc;
    private final Client client;

    public MessageHandler(NetworkClient nc) {
        this.nc = nc;
        this.client = nc.getClient();
    }

    /**
     * Handles a message from the server
     */
    public synchronized void handleMessage(String jsonMessage) throws IOException {
        Message message = Message.fromJson(jsonMessage);

        switch (message.getStatus()) {
            case "PING" -> handlePing();
            case "LOGIN" -> handleLogin(jsonMessage);
            case "ACTION" -> parseAction(jsonMessage);
            case "UPDATE" -> handleUpdate(jsonMessage);
            default -> client.gracefulTermination("Invalid response from server");
        }
    }

    /**
     * Handles an update message and updates the client interface
     */
    private void handleUpdate(String jsonMessage) {
        // to be implemented
    }

    /**
     * Handles a ping message
     */
    private void handlePing() {
        sendPong();
    }

    /**
     * Sends a "pong" message to the server
     */
    private void sendPong() {
        Message pong = new Message();
        pong.setStatus("PONG");
        nc.sendMessageToServer(pong.toJson());
    }

    /**
     * Handles a login message
     */
    private void handleLogin(String jsonMessage) {
        ServerLoginMessage message = ServerLoginMessage.fromJson(jsonMessage);

        if (message.getError() == 2) {
            client.showMessage("Username already taken");
            new Thread(() -> {
                try {
                    nc.askUsernameAndSend();
                } catch (IOException e) {
                    client.gracefulTermination("Connection to server went down");
                }
            }).start();
        } else if (message.getError() != 0) {
            client.gracefulTermination(message.getDisplayText());
        } else if (message.getAction() != null && message.getAction().equals("CREATE_GAME")) {
            new Thread(() -> {
                try {
                    int numPlayers = client.askNumPlayers();
                    boolean isExpert = client.askExpertMode();
                    sendGameParameters(numPlayers, isExpert);
                } catch (IOException e) {
                    client.gracefulTermination("Connection to server went down");
                }
            }).start();

        } else {    // action = null & error = 0 ----> this is a broadcast message
            client.showMessage(message.getDisplayText());
            client.showCurrentLobby(message.getGameLobby());
        }
    }

    /**
     * Sends a "CREATE_GAME" message to the server with the player preferences
     *
     * @param numPlayers an integer between 2 and 4
     * @param isExpert   true if the game should be in expert mode
     */
    private void sendGameParameters(int numPlayers, boolean isExpert) {
        ClientLoginMessage msg = new ClientLoginMessage();
        msg.setAction("CREATE_GAME");
        msg.setNumPlayers(numPlayers);
        msg.setExpert(isExpert);
        nc.sendMessageToServer(msg.toJson());
    }

    /**
     * Handles an action message
     */
    private void parseAction(String jsonMessage) throws IOException {
        // Action broadcast messages does not have to have an Action field: it
        // should receive the whole model
        ServerActionMessage actionMessage = ServerActionMessage.fromJson(jsonMessage);
        System.out.println(jsonMessage);

        if (actionMessage.getDisplayText() != null) {
            client.showMessage(actionMessage.getDisplayText());
        }

        // TODO: launch handleAction in a separate thread
        // TODO: REFACTOR switch+if in if+else if
        switch (actionMessage.getError()) {
            case 1, 2 -> {
                handleAction(actionMessage.getActions().get(0));
                return;
            }
            case 3 -> client.gracefulTermination("");
        }

        if (actionMessage.getActions() != null && !actionMessage.getActions().isEmpty()) {

            int index = 0;
            if (actionMessage.getActions().size() > 1) {
                client.showPossibleActions(actionMessage.getActions());
                index = client.chooseAction(actionMessage.getActions().size());
            } else {
                client.showMessage("Now you have to: " + actionMessage.getActions().get(0));
            }

            String chosenAction = actionMessage.getActions().get(index);
            handleAction(chosenAction);
        }

    }


    private void handleAction(String chosenAction) {
        new Thread(() -> {
            try {
                switch (chosenAction) {
                    case "PLAY_ASSISTANT" -> ActionHandler.handlePlayAssistant(nc);
                    case "MOVE_STUDENT_TO_DINING" -> ActionHandler.handleMoveStudentToDining(nc);
                    case "MOVE_STUDENT_TO_ISLAND" -> ActionHandler.handleMoveStudentToIsland(nc);
                    case "MOVE_MN" -> ActionHandler.handleMoveMotherNature(nc);
                    case "FILL_FROM_CLOUD" -> ActionHandler.handleFillFromCloud(nc);
                    case "PLAY_CHARACTER" -> ActionHandler.handlePlayCharacter(nc);
                    default -> client.gracefulTermination("Invalid message coming from server");
                }
            } catch (IOException e) {
                client.gracefulTermination("Connection lost");
            }


        }).start();
    }

}
