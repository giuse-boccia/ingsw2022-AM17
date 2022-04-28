package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.messages.login.ClientActionMessage;
import it.polimi.ingsw.messages.login.ClientLoginMessage;
import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.messages.login.ServerLoginMessage;
import it.polimi.ingsw.server.ClientHandler;
import it.polimi.ingsw.server.PlayerClient;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;

public class Controller {
    private final Gson gson;

    private final ArrayList<PlayerClient> loggedUsers;
    private int desiredNumberOfPlayers;
    private GameController gameController;

    public Controller() {
        loggedUsers = new ArrayList<>();
        gson = new Gson();
        gameController = null;
        desiredNumberOfPlayers = -1;
    }

    /**
     * Handles a message received from a Client and sends the appropriate response
     *
     * @param jsonMessage the message received from the client
     * @param ch          the {@code ClientHandler} of the client which sent the message
     */
    public void handleMessage(String jsonMessage, ClientHandler ch) throws IOException {
        switch (getMessageStatus(jsonMessage)) {
            case "LOGIN" -> handleLoginMessage(jsonMessage, ch);
            case "ACTION" -> handleActionMessage(jsonMessage, ch);
            default -> sendErrorMessage(ch, "Unrecognised type", 3);
        }
    }

    /**
     * Return the status field of the given json message
     *
     * @param json a JSON string containing the message to get the status of
     * @return the status field of the given json message
     */
    private String getMessageStatus(String json) {
        Type type = new TypeToken<Message>() {
        }.getType();
        Message msg = gson.fromJson(json, type);
        return msg.getStatus();
    }

    /**
     * Deserializes and handles a login message
     *
     * @param jsonMessage Json string which contains a login message
     * @param ch          the {@code ClientHandler} of the client who sent the message
     */
    private synchronized void handleLoginMessage(String jsonMessage, ClientHandler ch) throws IOException {
        try {
            ClientLoginMessage loginMessage = ClientLoginMessage.getMessageFromJSON(jsonMessage);

            if (loginMessage.getAction() == null) {
                sendErrorMessage(ch, "Bad request", 3);
                return;
            }

            switch (loginMessage.getAction()) {
                case "SET_USERNAME" -> addUser(ch, loginMessage.getUsername());
                case "CREATE_GAME" -> setDesiredNumberOfPlayers(ch, loginMessage.getNumPlayers());
                default -> sendErrorMessage(ch, "Bad request", 3);

            }
        } catch (JsonSyntaxException e) {
            sendErrorMessage(ch, "Num players must be a number", 3);
        }
    }

    /**
     * Sets the desired number of players of the game if possible
     *
     * @param ch         the {@code ClientHandler} of the client who sent the message
     * @param numPlayers the value contained in the message received
     */
    private void setDesiredNumberOfPlayers(ClientHandler ch, int numPlayers) throws IOException {
        if (numPlayers < 2 || numPlayers > 4) {
            sendErrorMessage(ch, "Num players must be between 2 and 4", 3);
        } else {
            desiredNumberOfPlayers = numPlayers;
        }
        checkGameReady();
    }

    /**
     * Adds the user who sent the message to the loggedUsers {@code ArrayList} if possible
     *
     * @param ch       the {@code ClientHandler} of the client who sent the message
     * @param username the username of the user to be added
     */
    private void addUser(ClientHandler ch, String username) throws IOException {

        if (username == null || username.trim().equals("")) {
            sendErrorMessage(ch, "Invalid username field", 3);
        } else if (username.length() > 32) {
            sendErrorMessage(ch, "Username is too long (max 32 characters)", 3);
        } else if (loggedUsers.stream().anyMatch(u -> u.getUsername().equals(username))) {
            sendErrorMessage(ch, "Username already taken", 2);
        } else if ((desiredNumberOfPlayers != -1 && loggedUsers.size() >= desiredNumberOfPlayers) || loggedUsers.size() >= 4) {
            sendErrorMessage(ch, "The lobby is full", 1);
        } else {
            PlayerClient newUser = new PlayerClient(ch, username);
            loggedUsers.add(newUser);
            if (newUser == loggedUsers.get(0)) {
                askDesiredNumberOfPlayers(ch);
            }
            sendBroadcastMessage();     // signals everyone that a new player has joined
            checkGameReady();
        }
    }

    /**
     * Checks if a game can be started; if so, every client in the lobby is notified
     */
    private void checkGameReady() throws IOException {
        if (desiredNumberOfPlayers == -1 || loggedUsers.size() < desiredNumberOfPlayers) return;

        ServerLoginMessage toSend = getServerLoginMessage("A new game is starting");

        for (int i = 0; i < loggedUsers.size(); i++) {
            ClientHandler clientHandler = loggedUsers.get(i).getClientHandler();

            if (i < desiredNumberOfPlayers) {
                // Alert player that game is starting
                clientHandler.sendMessageToClient(toSend.toJson());
            } else {
                // Alert player that game is full
                String errorMessage = "A new game for " + desiredNumberOfPlayers + " is starting. Your connection will be closed";
                sendErrorMessage(clientHandler, errorMessage, 1);
            }
        }
    }


    /**
     * Sends a message to every logged user containing the usernames of all logged users and the desired number of players
     * of the game, which can be -1 (not specified yet), 2, 3 or 4
     */
    private void sendBroadcastMessage() throws IOException {

        ServerLoginMessage res = getServerLoginMessage("A new player has joined");

        if (desiredNumberOfPlayers != -1) {
            loggedUsers.get(0).getClientHandler().sendMessageToClient(res.toJson());
        }

        for (int i = 1; i < loggedUsers.size(); i++) {
            loggedUsers.get(i).getClientHandler().sendMessageToClient(res.toJson());
        }
    }

    /**
     * Returns a {@code ServerLoginMessage} with the current {@code GameLobby} and number of players and a custom message
     *
     * @param message the {@code String} to put in the field displayText of the message
     * @return a {@code ServerLoginMessage} object
     */
    private ServerLoginMessage getServerLoginMessage(String message) {
        ServerLoginMessage res = new ServerLoginMessage();
        res.setMessage(message);
        res.setGameLobby(new GameLobby((String[]) loggedUsers.stream().map(PlayerClient::getUsername).toArray(), desiredNumberOfPlayers));
        return res;
    }

    /**
     * Sends a message asking for the desired number of players
     *
     * @param ch the {@code ClientHandler} of the player to send the message to
     */
    private void askDesiredNumberOfPlayers(ClientHandler ch) throws IOException {
        ServerLoginMessage res = new ServerLoginMessage();
        res.setAction("CREATE_GAME");
        res.setMessage("Insert desired number of players");

        ch.sendMessageToClient(res.toJson());
    }

    /**
     * Deserializes and handles an action message
     *
     * @param jsonMessage Json string which contains an action message
     * @param ch          the {@code ClientHandler} of the client who sent the message
     */
    private void handleActionMessage(String jsonMessage, ClientHandler ch) throws IOException {
        try {
            ClientActionMessage actionMessage = ClientActionMessage.getMessageFromJSON(jsonMessage);
            ch.sendMessageToClient("You sent an action message");
        } catch (JsonSyntaxException e) {
            sendErrorMessage(ch, "Bad request (syntax error)", 3);
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
        ch.sendMessageToClient(message.toJson());
    }
}