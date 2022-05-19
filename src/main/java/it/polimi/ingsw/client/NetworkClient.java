package it.polimi.ingsw.client;

import it.polimi.ingsw.messages.login.ClientLoginMessage;
import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.model.game_objects.Color;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;

public class NetworkClient extends Thread implements Observer {
    private final Client client;

    private final String serverAddress;
    private final int serverPort;

    private Socket server;
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
            server = new Socket(serverAddress, serverPort);
            socketIn = new BufferedReader(new InputStreamReader(server.getInputStream()));
            socketOut = new PrintWriter(server.getOutputStream(), true);

            askUsernameAndSend();

        } catch (IOException e) {
            client.gracefulTermination("Cannot connect to server, check server_address argument");
        }
    }

    /**
     * Asks for a username and sends a login message to the server
     */
    public void askUsernameAndSend() throws IOException {
        client.setCurrentObserver(this);
        client.showMessage("Connecting to server...");
        client.askUsername();
    }

    /**
     * Sends a message to the server
     *
     * @param message a JSON string containing the message
     */
    public void sendMessageToServer(String message) {
        socketOut.println(message);
    }

    @Override
    public void run() {
        MessageHandler mh = new MessageHandler(NetworkClient.this);
        mh.startPongThread();
        try {
            while (true) {
                String jsonMessage = socketIn.readLine();
                mh.handleMessage(jsonMessage);
            }
        } catch (IOException e) {
            // Server connection error
            client.gracefulTermination("Connection to server lost");
        }
    }

    @Override
    public void sendLoginParameters(String username, Integer numPlayers, Boolean isExpert) {
        if (username != null) {
            ClientLoginMessage loginMessage = new ClientLoginMessage();
            loginMessage.setUsername(username);
            loginMessage.setAction("SET_USERNAME");

            sendMessageToServer(loginMessage.toJson());
        }
    }

    @Override
    public void sendActionParameters(String actionName, Color color, Integer island, Integer num_steps, Integer cloud, Integer value, CharacterName characterName, List<Color> sourceStudents, List<Color> dstStudents) {

    }

    @Override
    public void sendActionName(String action) {

    }

    @Override
    public void sendCharacterName(CharacterName name) {

    }
}
