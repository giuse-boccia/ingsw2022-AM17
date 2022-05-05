package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.messages.action.ClientActionMessage;
import it.polimi.ingsw.messages.action.ServerActionMessage;
import it.polimi.ingsw.messages.login.ClientLoginMessage;
import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.messages.login.ServerLoginMessage;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.server.ClientHandler;
import it.polimi.ingsw.server.PlayerClient;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

public class Controller {
    private final Gson gson;

    private final ArrayList<PlayerClient> loggedUsers;
    private int desiredNumberOfPlayers;
    private GameController gameController;
    private int pongCount;
    private final Object boundLock = new Object();
    private boolean isExpert;

    public Controller() {
        loggedUsers = new ArrayList<>();
        gson = new Gson();
        gameController = null;
        desiredNumberOfPlayers = -1;
    }

    /**
     * Sends an error message to the client
     *
     * @param ch           The {@code ClientHandler} of the client which caused the error
     * @param status       "LOGIN" or "ACTION"
     * @param errorMessage the string which will be shown to the user
     * @param errorCode    an integer representing the error which occurred
     */
    public static void sendErrorMessage(ClientHandler ch, String status, String errorMessage, int errorCode) {
        if (status.equals("LOGIN")) {
            ServerLoginMessage message = new ServerLoginMessage();
            message.setError(errorCode);
            message.setDisplayText("[ERROR] " + errorMessage);
            ch.sendMessageToClient(message.toJson());
        } else if (status.equals("ACTION")) {
            ServerActionMessage message = new ServerActionMessage();
            message.setError(errorCode);
            message.setDisplayText("[ERROR] " + errorMessage);
            ch.sendMessageToClient(message.toJson());
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
     * Handles a message received from a Client and sends the appropriate response
     *
     * @param jsonMessage the message received from the client
     * @param ch          the {@code ClientHandler} of the client which sent the message
     */
    public synchronized void handleMessage(String jsonMessage, ClientHandler ch) {
        switch (getMessageStatus(jsonMessage)) {
            case "LOGIN" -> handleLoginMessage(jsonMessage, ch);
            case "ACTION" -> handleActionMessage(jsonMessage, ch);
            case "PONG" -> {
                synchronized (boundLock) {
                    pongCount++;
                }
            }
            default -> sendErrorMessage(ch, "LOGIN", "Unrecognised type", 3);
        }
    }

    /**
     * Deserializes and handles a login message
     *
     * @param jsonMessage Json string which contains a login message
     * @param ch          the {@code ClientHandler} of the client who sent the message
     */
    private void handleLoginMessage(String jsonMessage, ClientHandler ch) {
        try {
            ClientLoginMessage loginMessage = ClientLoginMessage.getMessageFromJSON(jsonMessage);

            if (loginMessage.getAction() == null) {
                sendErrorMessage(ch, "LOGIN", "Bad request", 3);
                return;
            }

            switch (loginMessage.getAction()) {
                case "SET_USERNAME" -> addUser(ch, loginMessage.getUsername());
                case "CREATE_GAME" -> setGameParameters(ch, loginMessage.getNumPlayers(), loginMessage.isExpert());
                default -> sendErrorMessage(ch, "LOGIN", "Bad request", 3);

            }
        } catch (JsonSyntaxException e) {
            sendErrorMessage(ch, "LOGIN", "Num players must be a number", 3);
        }
    }

    /**
     * Sets the desired number of players of the game if possible
     *
     * @param ch         the {@code ClientHandler} of the client who sent the message
     * @param numPlayers the value contained in the message received
     */
    private void setGameParameters(ClientHandler ch, int numPlayers, boolean isExpert) {
        if (numPlayers < 2 || numPlayers > 4) {
            sendErrorMessage(ch, "LOGIN", "Num players must be between 2 and 4", 3);
        } else {
            desiredNumberOfPlayers = numPlayers;
            this.isExpert = isExpert;
            System.out.println("GAME CREATED | " + numPlayers + " players | " + (isExpert ? "expert" : "non expert") + " mode");
        }
        if (!isGameReady()) {
            ServerLoginMessage message = getServerLoginMessage("A new game was created!");
            for (PlayerClient player : loggedUsers) {
                player.getClientHandler().sendMessageToClient(message.toJson());
            }
        }
    }

    /**
     * Adds the user who sent the message to the loggedUsers {@code ArrayList} if possible
     *
     * @param ch       the {@code ClientHandler} of the client who sent the message
     * @param username the username of the user to be added
     */
    private void addUser(ClientHandler ch, String username) {

        if (username == null || username.trim().equals("")) {
            sendErrorMessage(ch, "LOGIN", "Invalid username field", 3);
        } else if ((desiredNumberOfPlayers != -1 && loggedUsers.size() >= desiredNumberOfPlayers) || loggedUsers.size() >= 4 || gameController != null) {
            sendErrorMessage(ch, "LOGIN", "The lobby is full", 1);
        } else if (username.length() > 32) {
            sendErrorMessage(ch, "LOGIN", "Username is too long (max 32 characters)", 3);
        } else if (loggedUsers.stream().anyMatch(u -> u.getUsername().equals(username))) {
            sendErrorMessage(ch, "LOGIN", "Username already taken", 2);
        } else {
            PlayerClient newUser = new PlayerClient(ch, username);
            loggedUsers.add(newUser);
            System.out.println("Added player " + newUser.getUsername());

            if (newUser == loggedUsers.get(0)) {
                askDesiredNumberOfPlayers(ch);
                return;
            }
            if (!isGameReady()) {
                sendBroadcastMessage();     // signals everyone that a new player has joined
            }
        }
    }


    /**
     * Sends a message to every logged user containing the usernames of all logged users and the desired number of players
     * of the game, which can be -1 (not specified yet), 2, 3 or 4
     */
    private void sendBroadcastMessage() {

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
        res.setDisplayText(message);
        Collection<String> playersList = loggedUsers.stream().map(PlayerClient::getUsername).toList();
        String[] playersArray = playersList.toArray(new String[0]);
        res.setGameLobby(new GameLobby(playersArray, desiredNumberOfPlayers, isExpert));
        return res;
    }

    /**
     * Sends a message asking for the desired number of players and the mode of the game
     *
     * @param ch the {@code ClientHandler} of the player to send the message to
     */
    private void askDesiredNumberOfPlayers(ClientHandler ch) {
        ServerLoginMessage res = new ServerLoginMessage();
        res.setAction("CREATE_GAME");
        res.setDisplayText("You are the first player. Set game parameters");

        ch.sendMessageToClient(res.toJson());

    }

    /**
     * Checks if a game can be started; if so, every client in the lobby is notified
     */
    private boolean isGameReady() {
        if (desiredNumberOfPlayers == -1 || loggedUsers.size() < desiredNumberOfPlayers) return false;

        while (desiredNumberOfPlayers < loggedUsers.size()) {
            // Alert player that game is full and removes him
            PlayerClient toRemove = loggedUsers.get(desiredNumberOfPlayers);
            String errorMessage = "A new game for " + desiredNumberOfPlayers + " players is starting. Your connection will be closed";
            sendErrorMessage(toRemove.getClientHandler(), "LOGIN", errorMessage, 1);
            loggedUsers.remove(toRemove);
        }

        ServerLoginMessage toSend = getServerLoginMessage("A new game is starting");

        for (PlayerClient playerClient : loggedUsers) {
            // Alert player that game is starting
            playerClient.getClientHandler().sendMessageToClient(toSend.toJson());
            playerClient.setPlayer(new Player(playerClient.getUsername(), desiredNumberOfPlayers % 2 == 0 ? 8 : 6));
        }

        //Start a new Game
        gameController = new GameController(loggedUsers, isExpert);
        gameController.start();


        return true;
    }

    /**
     * Deserializes and handles an action message
     *
     * @param jsonMessage Json string which contains an action message
     * @param ch          the {@code ClientHandler} of the client who sent the message
     */
    private void handleActionMessage(String jsonMessage, ClientHandler ch) {
        if (gameController == null) {
            sendErrorMessage(ch, "ACTION", "Game is not started yet", 1);
        }

        try {
            ClientActionMessage actionMessage = ClientActionMessage.getMessageFromJSON(jsonMessage);
            gameController.handleActionMessage(actionMessage, ch);
        } catch (JsonSyntaxException e) {
            sendErrorMessage(ch, "ACTION", "Bad request (syntax error)", 3);
        }
    }

    /**
     * Periodically sends a "ping" message to every client and awaits for a "pong" response.
     * This is done on a parallel thread
     */
    public void startPingPong() {
        new Thread(() -> {
            while (true) {
                synchronized (boundLock) {
                    pongCount = 0;
                }
                Message ping = new Message();
                ping.setStatus("PING");
                int bound = 0;
                for (PlayerClient user : loggedUsers) {
                    user.getClientHandler().sendMessageToClient(ping.toJson());
                    bound++;
                }
                try {
                    Thread.sleep(2000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                    return;
                }
                synchronized (boundLock) {
                    if (pongCount < bound) {
                        for (PlayerClient user : loggedUsers) {
                            sendErrorMessage(user.getClientHandler(), "LOGIN", "Connection with one client lost", 3);
                        }
                        loggedUsers.clear();
                        gameController = null;
                        desiredNumberOfPlayers = -1;
                        System.out.println("Connection with one client lost, clearing the game...");
                    }
                }
            }
        }).start();
    }
}