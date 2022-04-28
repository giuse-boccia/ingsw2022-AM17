package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.messages.login.ClientActionMessage;
import it.polimi.ingsw.messages.login.ClientLoginMessage;
import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.messages.login.ServerLoginMessage;
import it.polimi.ingsw.server.ClientHandler;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class Controller {
    private final Gson gson;

    private final Map<ClientHandler, String> loggedUsers;
    private int desiredNumberOfPlayers;
    private GameController gameController;
    private ClientHandler firstPlayerConnected;

    public Controller() {
        loggedUsers = new HashMap<>();
        gson = new Gson();
        gameController = null;
        desiredNumberOfPlayers = -1;
        firstPlayerConnected = null;
    }

    /**
     * Handles a message received from a Client and sends the appropriate response
     *
     * @param jsonMessage the message received from the client
     * @param ch          the {@code ClientHandler} of the client which sent the message
     */
    public void handleMessage(String jsonMessage, ClientHandler ch) throws IOException {
        switch (getMessageType(jsonMessage)) {
            case "login" -> {
                ClientLoginMessage loginMessage = ClientLoginMessage.getMessageFromJSON(jsonMessage);
                handleLoginMessage(loginMessage, ch);
            }
            case "action" -> {
                ClientActionMessage actionMessage = ClientActionMessage.getMessageFromJSON(jsonMessage);
                handleActionMessage(actionMessage, ch);
            }
            default -> sendErrorMessage(ch, "Unrecognised type", 3);
        }
    }

    private String getMessageType(String json) {
        Type type = new TypeToken<Message>() {
        }.getType();
        Message msg = gson.fromJson(json, type);
        return msg.getStatus();
    }

    /**
     * Handles a login message
     *
     * @param loginMessage deserialized message
     * @param ch           the {@code ClientHandler} of the client who sent the message
     */
    private synchronized void handleLoginMessage(ClientLoginMessage loginMessage, ClientHandler ch) throws IOException {
        ServerLoginMessage msgToSend = new ServerLoginMessage();

        if (loginMessage.getAction() == null) {
            sendErrorMessage(ch, "Bad request", 3);
            return;
        }

        switch (loginMessage.getAction()) {
            case "set username" -> {
                if (loginMessage.getUsername() == null || loginMessage.getUsername().trim().equals("") || loginMessage.getUsername().length() > 32) {
                    sendErrorMessage(ch, "Invalid username field", 3);
                } else if (loggedUsers.containsValue(loginMessage.getUsername())) {
                    sendErrorMessage(ch, "Username already taken", 2);
                } else {
                    sendFirstResponse(ch);
                }
            }

            case "create game" -> {

            }

        }


        // TODO: check if game is already started
        if (loggedUsers.isEmpty()) {
            if (loginMessage.getAction().equals("create game")) {
                String username = loginMessage.getUsername();
                int numPlayers = 2;
                if (username == null || username.equals("")) {
                    sendErrorMessage(ch, "Empty username field", 3);
                }

                try {
                    numPlayers = loginMessage.getNumPlayers();
                    if (numPlayers < 2 || numPlayers > 4) {
                        sendErrorMessage(ch, "Num players must be between 2 and 4", 3);
                        return;
                    }
                } catch (NumberFormatException e) {
                    sendErrorMessage(ch, "Invalid num players", 3);
                }
                loggedUsers.put(ch, username);
                desiredNumberOfPlayers = numPlayers;
                msgToSend.setMessage("game created");
            }
        } else if (gameController == null) {
            String username = loginMessage.getUsername();
            if (username == null || username.equals("")) {
                sendErrorMessage(ch, "Empty username field", 3);
            } else if (loggedUsers.containsValue(username)) {
                sendErrorMessage(ch, "Username already taken", 2);
                return;
            }
            loggedUsers.put(ch, username);
            msgToSend.setMessage("player has joined");

            if (loggedUsers.size() == desiredNumberOfPlayers) {
                gameController = new GameController();
                // TODO start new game
                GameLobby lobby = new GameLobby(loggedUsers.values().toArray(String[]::new), desiredNumberOfPlayers);
                msgToSend.setGameLobby(lobby);
                msgToSend.setMessage("Lobby completed. A new game is starting...");
                for (ClientHandler clientHandler : loggedUsers.keySet()) {
                    try {
                        clientHandler.sendMessageToClient(msgToSend.getJson());
                    } catch (IOException e) {
                        System.err.println("Couldn't get I/O, connection will be closed...");
                        System.exit(-1);
                    }
                }
                return;
            }

        } else {
            sendErrorMessage(ch, "A game is already in progress", 1);
            return;
        }

        GameLobby lobby = new GameLobby(loggedUsers.values().toArray(String[]::new), desiredNumberOfPlayers);
        msgToSend.setGameLobby(lobby);
        for (ClientHandler clientHandler : loggedUsers.keySet()) {
            try {
                clientHandler.sendMessageToClient(msgToSend.getJson());
            } catch (IOException e) {
                System.err.println("Couldn't get I/O, connection will be closed...");
                System.exit(-1);
            }
        }
    }

    private void handleActionMessage(ClientActionMessage map, ClientHandler ch) {
        try {
            ch.sendMessageToClient("You sent an action message");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends an error message to the client
     *
     * @param ch The {@code ClientHandler} of the client which caused the error
     */
    private void sendErrorMessage(ClientHandler ch, String errorMessage, int errorCode) throws IOException {
        ServerLoginMessage message = new ServerLoginMessage();
        message.setError(errorCode);
        message.setMessage("[ERROR] " + errorMessage);
        ch.sendMessageToClient(message.getJson());
    }

    /**
     * Creates a welcome message based on how many players have already joined the game
     */
    public synchronized void sendFirstResponse(ClientHandler ch) throws IOException {
        ServerLoginMessage res = new ServerLoginMessage();
        if (gameController != null) {
            res.setError(1);
            res.setMessage("A game is already in progress. The connection will be closed");
        } else if (loggedUsers.isEmpty()) {
            res.setAction("create game");
            res.setMessage("Insert desired number of players");
            firstPlayerConnected = ch;
        } else {
            res.setMessage("player has joined");
            res.setGameLobby(new GameLobby(loggedUsers.values().toArray(String[]::new), desiredNumberOfPlayers));
            for (ClientHandler clientHandler : loggedUsers.keySet()) {
                if (clientHandler != firstPlayerConnected) {
                    clientHandler.sendMessageToClient(res.getJson());
                }
            }
            return;
        }
        ch.sendMessageToClient(res.getJson());
    }


}