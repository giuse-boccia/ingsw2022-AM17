package it.polimi.ingsw.client;

import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.messages.login.ClientLoginMessage;
import it.polimi.ingsw.messages.login.ServerLoginMessage;

import java.io.IOException;

/**
 * Class which handles a single message from the server within a separate thread
 */
public class MessageHandler implements Runnable {
    private final String jsonMessage;
    private final NetworkClient nc;
    private final Client client;

    public MessageHandler(String jsonMessage, NetworkClient nc) {
        this.jsonMessage = jsonMessage;
        this.nc = nc;
        this.client = nc.getClient();
    }

    @Override
    public void run() {

        try {
            handleMessage();
        } catch (IOException e) {
            client.gracefulTermination("Lost connection to server");
        }

    }

    /**
     * Handles a message from the server
     */
    private void handleMessage() throws IOException {
        Message message = Message.fromJson(jsonMessage);

        switch (message.getStatus()) {
            case "PING" -> handlePing();
            case "LOGIN" -> handleLogin();
            case "ACTION" -> handleAction();
            default -> client.gracefulTermination("Invalid response from server");
        }
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
    private void handleLogin() throws IOException {
        ServerLoginMessage message = ServerLoginMessage.fromJson(jsonMessage);

        if (message.getError() == 2) {
            client.showMessage("Username already taken");
            nc.askUsernameAndSend();
        } else if (message.getError() != 0) {
            client.gracefulTermination(message.getDisplayText());
        } else if (message.getAction() != null && message.getAction().equals("CREATE_GAME")) {
            int numPlayers = client.askNumPlayers();
            boolean isExpert = client.askExpertMode();
            sendGameParameters(numPlayers, isExpert);
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
    private void handleAction() {
    }


}
