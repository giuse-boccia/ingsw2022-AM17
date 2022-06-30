package it.polimi.ingsw.client;

import it.polimi.ingsw.client.observers.chat.SendChatMessageObserver;
import it.polimi.ingsw.client.observers.choices.action.SendActionChoiceObserver;
import it.polimi.ingsw.client.observers.choices.character.SendCharacterChoiceObserver;
import it.polimi.ingsw.client.observers.game_actions.choose_cloud.SendChooseCloudObserver;
import it.polimi.ingsw.client.observers.game_actions.move_mn.SendMoveMNObserver;
import it.polimi.ingsw.client.observers.game_actions.move_student.SendMoveStudentObserver;
import it.polimi.ingsw.client.observers.game_actions.play_assistant.SendPlayAssistantObserver;
import it.polimi.ingsw.client.observers.game_actions.play_character.SendPlayCharacterObserver;
import it.polimi.ingsw.client.observers.login.game_parameters.SendGameParametersObserver;
import it.polimi.ingsw.client.observers.login.load_game.ExecuteLoadGameObserver;
import it.polimi.ingsw.client.observers.login.username.SendUsernameObserver;
import it.polimi.ingsw.languages.Messages;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class NetworkClient extends Thread {
    private final Client client;

    private final String serverAddress;
    private final int serverPort;

    private BufferedReader socketIn;
    private PrintWriter socketOut;

    public NetworkClient(Client client, String serverAddress, int serverPort) {
        this.client = client;
        this.serverAddress = serverAddress;
        this.serverPort = serverPort;
    }

    public Client getClient() {
        return client;
    }

    /**
     * Asks the client for the username and tries to open a connection with the server
     */
    public void connectToServer() {
        try {
            Socket server = new Socket(serverAddress, serverPort);
            socketIn = new BufferedReader(new InputStreamReader(server.getInputStream()));
            socketOut = new PrintWriter(server.getOutputStream(), true);

        } catch (IOException e) {
            client.gracefulTermination(Messages.getMessage("cannot_connect_to_server"));
        }
    }

    /**
     * Sends a message to the server
     *
     * @param message a JSON string containing the message
     */
    public void sendMessageToServer(String message) {
        socketOut.println(message);
    }

    private void attachObserversToMessageHandler(MessageHandler mh) {
        // Login observers
        new SendUsernameObserver(mh);
        new SendGameParametersObserver(mh);
        new ExecuteLoadGameObserver(mh);

        // Choice observers
        new SendActionChoiceObserver(mh);
        new SendCharacterChoiceObserver(mh);

        // Game actions observers
        new SendChooseCloudObserver(mh);
        new SendMoveMNObserver(mh);
        new SendMoveStudentObserver(mh);
        new SendPlayAssistantObserver(mh);
        new SendPlayCharacterObserver(mh);

        // Chat observers
        new SendChatMessageObserver(mh);
    }

    @Override
    public void run() {
        MessageHandler mh = new MessageHandler(NetworkClient.this);
        attachObserversToMessageHandler(mh);

        try {
            mh.askUsernameAndSend();
            mh.startPongThread();

            // Couldn't connect to server, application will be shut down
            if (socketIn == null) return;

            while (true) {
                String jsonMessage = socketIn.readLine();
                mh.handleMessage(jsonMessage);
            }
        } catch (IOException e) {
            // Server connection error
            client.gracefulTermination(Messages.getMessage("server_lost"));
        }
    }
}
