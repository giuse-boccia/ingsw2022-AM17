package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.messages.action.ClientActionMessage;
import it.polimi.ingsw.messages.login.ClientLoginMessage;
import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.messages.login.ServerLoginMessage;
import it.polimi.ingsw.server.ClientHandler;
import it.polimi.ingsw.server.PlayerClient;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
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
    private void handleLoginMessage(String jsonMessage, ClientHandler ch) {
        try {
            ClientLoginMessage loginMessage = ClientLoginMessage.getMessageFromJSON(jsonMessage);

            if (loginMessage.getAction() == null) {
                sendErrorMessage(ch, "Bad request", 3);
                return;
            }

            switch (loginMessage.getAction()) {
                case "SET_USERNAME" -> addUser(ch, loginMessage.getUsername());
                case "CREATE_GAME" -> setGameParameters(ch, loginMessage.getNumPlayers(), loginMessage.isExpert());
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
    private void setGameParameters(ClientHandler ch, int numPlayers, boolean isExpert) {
        if (numPlayers < 2 || numPlayers > 4) {
            sendErrorMessage(ch, "Num players must be between 2 and 4", 3);
        } else {
            desiredNumberOfPlayers = numPlayers;
            this.isExpert = isExpert;
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
            sendErrorMessage(ch, "Invalid username field", 3);
        } else if ((desiredNumberOfPlayers != -1 && loggedUsers.size() >= desiredNumberOfPlayers) || loggedUsers.size() >= 4 || gameController != null) {
            sendErrorMessage(ch, "The lobby is full", 1);
        } else if (username.length() > 32) {
            sendErrorMessage(ch, "Username is too long (max 32 characters)", 3);
        } else if (loggedUsers.stream().anyMatch(u -> u.getUsername().equals(username))) {
            sendErrorMessage(ch, "Username already taken", 2);
        } else {
            PlayerClient newUser = new PlayerClient(ch, username);
            loggedUsers.add(newUser);

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
     * Checks if a game can be started; if so, every client in the lobby is notified
     */
    private boolean isGameReady() {
        if (desiredNumberOfPlayers == -1 || loggedUsers.size() < desiredNumberOfPlayers) return false;

        for (int i = desiredNumberOfPlayers; i < loggedUsers.size(); ) {
            // Alert player that game is full
            PlayerClient toRemove = loggedUsers.get(i);
            String errorMessage = "A new game for " + desiredNumberOfPlayers + " players is starting. Your connection will be closed";
            sendErrorMessage(toRemove.getClientHandler(), errorMessage, 1);
            loggedUsers.remove(toRemove);
        }

        ServerLoginMessage toSend = getServerLoginMessage("A new game is starting");

        for (PlayerClient playerClient : loggedUsers) {
            // Alert player that game is starting
            playerClient.getClientHandler().sendMessageToClient(toSend.toJson());
        }

        //Start a new Game
        gameController = new GameController(loggedUsers, isExpert);

        return true;
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
        res.setGameLobby(new GameLobby(playersArray, desiredNumberOfPlayers));
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
     * Deserializes and handles an action message
     *
     * @param jsonMessage Json string which contains an action message
     * @param ch          the {@code ClientHandler} of the client who sent the message
     */
    private void handleActionMessage(String jsonMessage, ClientHandler ch) {
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
    private void sendErrorMessage(ClientHandler ch, String errorMessage, int errorCode) {
        ServerLoginMessage message = new ServerLoginMessage();
        message.setError(errorCode);
        message.setDisplayText("[ERROR] " + errorMessage);
        ch.sendMessageToClient(message.toJson());
    }

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
                            sendErrorMessage(user.getClientHandler(), "Connection with one client lost", 3);
                        }
                        loggedUsers.clear();
                        System.out.println("id thread: " + Thread.currentThread().getId());
                        System.out.println("Ho tolto tutti i socket " + Arrays.toString(loggedUsers.toArray()));
                        desiredNumberOfPlayers = -1;
                        System.out.println("Numero di giocatori: " + desiredNumberOfPlayers);

                    }
                }
            }
        }).start();
    }
}