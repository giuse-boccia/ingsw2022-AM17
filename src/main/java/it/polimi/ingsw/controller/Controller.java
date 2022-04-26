package it.polimi.ingsw.controller;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.exceptions.GameLoginException;
import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.messages.login.ClientActionMessage;
import it.polimi.ingsw.messages.login.ClientLoginMessage;
import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.messages.login.ServerLoginMessage;
import it.polimi.ingsw.server.ClientHandler;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Controller {
    private final Gson gson;

    private final Map<ClientHandler, String> chMapPlayer;
    private ArrayList<ClientHandler> clientHandlers;
    private int desiredNumberOfPlayers;

    private GameController gameController;

    public Controller() {
        chMapPlayer = new HashMap<>();
        gson = new Gson();
        gameController = null;
        clientHandlers = new ArrayList<>();
    }

    /**
     * Handles a message received from a Client and sends the appropriate response
     *
     * @param jsonMessage the message received from the client
     * @param ch          the {@code ClientHandler} of the client which sent the message
     */
    public void handleMessage(String jsonMessage, ClientHandler ch) {
        switch (getMessageType(jsonMessage)) {
            case "login" -> {
                ClientLoginMessage loginMessage = ClientLoginMessage.getMessageFromJSON(jsonMessage);
                handleLoginMessage(loginMessage, ch);
            }
            case "action" -> {
                ClientActionMessage actionMessage = ClientActionMessage.getMessageFromJSON(jsonMessage);
                handleActionMessage(actionMessage, ch);
            }
            default -> sendErrorMessage(ch, "Unrecognised type");
        }
    }

    private String getMessageType(String json) {
        Type type = new TypeToken<Message>() {
        }.getType();
        Message msg = gson.fromJson(json, type);
        return msg.getType();
    }

    /**
     * Handles a login message
     *
     * @param loginMessage deserialized message
     * @param ch           the {@code ClientHandler} of the client who sent the message
     */
    private synchronized void handleLoginMessage(ClientLoginMessage loginMessage, ClientHandler ch) {
        ServerLoginMessage msgToSend = new ServerLoginMessage();
        // TODO: check if game is already started
        if (chMapPlayer.isEmpty()) {
            if (loginMessage.getAction().equals("create game")) {
                String username = loginMessage.getUsername();
                int numPlayers = 2;
                if (username == null || username.equals("")) {
                    sendErrorMessage(ch, "Empty username field");
                }

                try {
                    numPlayers = loginMessage.getNumPlayers();
                    if (numPlayers < 2 || numPlayers > 4) {
                        sendErrorMessage(ch, "Num players must be between 2 and 4");
                        return;
                    }
                } catch (NumberFormatException e) {
                    sendErrorMessage(ch, "Invalid num players");
                }
                chMapPlayer.put(ch, username);
                desiredNumberOfPlayers = numPlayers;
                msgToSend.setMessage("game created");
            }
        } else if (gameController == null) {
            String username = loginMessage.getUsername();
            if (username == null || username.equals("")) {
                sendErrorMessage(ch, "Empty username field");
            } else if (chMapPlayer.containsValue(username)) {
                sendErrorMessage(ch, "Username already taken");
            }
            chMapPlayer.put(ch, username);
            msgToSend.setMessage("player has joined");

            if (chMapPlayer.size() == desiredNumberOfPlayers) {
                gameController = new GameController();
                // TODO start new game
                msgToSend.setMessage("a new game is starting");
            }

        } else {
            sendErrorMessage(ch, "A game is already in progress");
            return;
        }

        GameLobby lobby = new GameLobby(chMapPlayer.values().toArray(String[]::new), desiredNumberOfPlayers);
        msgToSend.setGameLobby(lobby);
        for (ClientHandler clientHandler : clientHandlers) {
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
    private void sendErrorMessage(ClientHandler ch, String errorMessage) {
        try {
            ch.sendMessageToClient("[ERROR] " + errorMessage);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Creates a welcome message based on how many players have already joined the game
     */
    public synchronized void sendWelcomeMessage(ClientHandler ch) throws IOException, GameLoginException {
        ServerLoginMessage res = new ServerLoginMessage();
        if (gameController != null) {
            res.setError(1);
            res.setMessage("A game is already in progress. The connection will be closed");
            throw new GameLoginException();
        }
        if (chMapPlayer.isEmpty()) {
            res.setAction("create game");
            res.setMessage("Insert username and desired number of players");
        } else {
            res.setAction("join game");
            res.setMessage("Insert username");
            res.setGameLobby(new GameLobby(chMapPlayer.values().toArray(String[]::new), desiredNumberOfPlayers));
        }
        ch.sendMessageToClient(gson.toJson(res));
    }


    public void addClientHandler(ClientHandler ch) {
        clientHandlers.add(ch);
    }

}