package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.exceptions.GameLoginException;
import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.messages.login.LoginError;
import it.polimi.ingsw.messages.login.LoginMessage;
import it.polimi.ingsw.server.ClientHandler;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

public class Controller {
    private final Gson gson;

    private final Map<ClientHandler, String> chMapPlayer;
    private int desiredNumberOfPlayers;

    private GameController gameController;

    public Controller() {
        chMapPlayer = new HashMap<>();
        gson = new Gson();
        gameController = null;
    }

    /**
     * Handles a message received from a Client and sends the appropriate response
     *
     * @param message the message received from the client
     * @param ch      the {@code ClientHandler} of the client which sent the message
     */
    public void handleMessage(String message, ClientHandler ch) {
        Type type = new TypeToken<Map<String, String>>() {
        }.getType();
        Map<String, String> map = gson.fromJson(message, type);
        switch (map.get("type")) {
            case "login" -> handleLoginMessage(map, ch);
            case "action" -> handleActionMessage(map, ch);
            default -> sendErrorMessage(ch, "unrecognised type");
        }
    }

    /**
     * Handles a login message
     *
     * @param map deserialized message
     * @param ch  the {@code ClientHandler} of the client who sent the message
     */
    private void handleLoginMessage(Map<String, String> map, ClientHandler ch) {
        // TODO: check if game is already started
        if (chMapPlayer.isEmpty()) {
            if (map.get("action").equals("create game")) {
                String username = map.get("username");
                if (username == null || username.equals("")) {
                    sendErrorMessage(ch, "empty username field");
                }
                try {
                    int numPlayers = Integer.parseInt(map.get("num players"));
                    if (numPlayers < 2 || numPlayers > 4) {
                        sendErrorMessage(ch, "num players must be between 2 and 4");
                    }
                } catch (NumberFormatException e) {
                    sendErrorMessage(ch, "invalid num players");
                }
                // TODO: create game and send broadcast message
            }
        }
    }

    private void handleActionMessage(Map<String, String> map, ClientHandler ch) {
        try {
            ch.sendResponse("You sent an action message");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sends an error message to the client
     *
     * @param ch The {@code ClientHandler} of the client which caused the error
     */
    private void sendErrorMessage(ClientHandler ch, String errorMessage) {
        try {
            ch.sendResponse("[ERROR] " + errorMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a welcome message based on how many players have already joined the game
     */
    public void sendWelcomeMessage(ClientHandler ch) throws IOException, GameLoginException {
        LoginMessage res = new LoginMessage();
        if (gameController != null) {
            res.setLoginError(new LoginError(1, "A game is already in progress. The connection will be closed"));
            throw new GameLoginException();
        }
        if (chMapPlayer.isEmpty()) {
            res.setAction("create game");
            res.setMessage("insert username and desired number of players");
        } else {
            res.setAction("join game");
            res.setMessage("insert username");
            res.setGameLobby(new GameLobby(chMapPlayer.values().toArray(String[]::new), desiredNumberOfPlayers));
        }
        ch.sendResponse(gson.toJson(res));
    }


    public void addPlayer(String name, ClientHandler ch) {
        // check if player with same name is in game and eventually add player to chMapPlayer
    }

}
