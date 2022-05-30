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

public class NetworkClient extends Thread {
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

        } catch (IOException e) {
            client.gracefulTermination("Cannot connect to server, check server_address argument");
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

    @Override
    public void run() {
        MessageHandler mh = new MessageHandler(NetworkClient.this);

        try {
            mh.askUsernameAndSend();
            mh.startPongThread();

            while (true) {
                String jsonMessage = socketIn.readLine();
                mh.handleMessage(jsonMessage);
            }
        } catch (IOException e) {
            // Server connection error
            client.gracefulTermination("Connection to server lost");
        }
    }
}
