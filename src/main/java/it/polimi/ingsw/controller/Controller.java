package it.polimi.ingsw.controller;

import com.google.gson.JsonSyntaxException;
import it.polimi.ingsw.constants.Constants;
import it.polimi.ingsw.constants.Messages;
import it.polimi.ingsw.exceptions.GameEndedException;
import it.polimi.ingsw.languages.MessageResourceBundle;
import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.messages.action.ClientActionMessage;
import it.polimi.ingsw.messages.action.ServerActionMessage;
import it.polimi.ingsw.messages.login.ClientLoginMessage;
import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.messages.login.ServerLoginMessage;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.server.Communicable;
import it.polimi.ingsw.server.PlayerClient;
import it.polimi.ingsw.server.game_state.SavedGameState;

import java.io.IOException;
import java.util.*;

public class Controller {
    private final Object boundLock = new Object();
    private final List<PlayerClient> loggedUsers;
    private final GameLobby gameLobby;
    private GameController gameController;
    private Game loadedGame;
    private int pongCount;
    private int bound = 0;

    public Controller() {
        this.loggedUsers = new ArrayList<>();
        this.gameLobby = new GameLobby();
        this.loadedGame = null;
        this.gameController = null;
    }

    /**
     * Sends an error message to the client
     *
     * @param ch           The {@code Communicable} interface of the client which caused the error
     * @param status       "LOGIN" or "ACTION"
     * @param errorMessage the string which will be shown to the user
     * @param errorCode    an integer representing the error which occurred
     */
    public static void sendErrorMessage(Communicable ch, String status, String errorMessage, int errorCode) {
        if (status.equals(Messages.STATUS_LOGIN)) {
            ServerLoginMessage message = new ServerLoginMessage();
            message.setError(errorCode);
            message.setDisplayText(MessageResourceBundle.getMessage("error_tag") + errorMessage);
            ch.sendMessageToClient(message.toJson());
        } else if (status.equals(Messages.STATUS_ACTION)) {
            ServerActionMessage message = new ServerActionMessage();
            message.setError(errorCode);
            message.setDisplayText(MessageResourceBundle.getMessage("error_tag") + errorMessage);
            ch.sendMessageToClient(message.toJson());
        }

    }

    /**
     * Handles a message received from a Client and sends the appropriate response
     *
     * @param jsonMessage the message received from the client
     * @param ch          the {@code Communicable} interface of the client which sent the message
     */
    public synchronized void handleMessage(String jsonMessage, Communicable ch) throws GameEndedException {
        switch (getMessageStatus(jsonMessage)) {
            case Messages.STATUS_LOGIN -> handleLoginMessage(jsonMessage, ch);
            case Messages.STATUS_ACTION -> handleActionMessage(jsonMessage, ch);
            case Messages.STATUS_PONG -> handlePong();
            default -> sendErrorMessage(ch, Messages.STATUS_LOGIN, MessageResourceBundle.getMessage("unrecognised_type"), 3);
        }
    }

    /**
     * Return the status field of the given json message
     *
     * @param json a JSON string containing the message to get the status of
     * @return the status field of the given json message
     */
    private String getMessageStatus(String json) {
        try {
            Message msg = Message.fromJson(json);
            return msg.getStatus();
        } catch (JsonSyntaxException e) {
            return "";
        }
    }

    /**
     * Handles a pong message
     */
    private void handlePong() {
        synchronized (boundLock) {
            pongCount++;
        }
    }

    /**
     * Deserializes and handles a login message
     *
     * @param jsonMessage Json string which contains a login message
     * @param ch          the {@code Communicable} interface of the client who sent the message
     */
    private void handleLoginMessage(String jsonMessage, Communicable ch) {
        try {
            ClientLoginMessage loginMessage = ClientLoginMessage.fromJSON(jsonMessage);

            if (loginMessage.getAction() == null) {
                sendErrorMessage(ch, Messages.STATUS_LOGIN, MessageResourceBundle.getMessage("bad_request"), 3);
                return;
            }

            switch (loginMessage.getAction()) {
                case Messages.ACTION_SET_USERNAME -> handleSetUsername(ch, loginMessage.getUsername());
                case Messages.ACTION_CREATE_GAME ->
                        setGameParameters(ch, loginMessage.getNumPlayers(), loginMessage.isExpert());
                case Messages.ACTION_LOAD_GAME -> setLoadedGameParameters(ch);
                default -> sendErrorMessage(ch, Messages.STATUS_LOGIN, MessageResourceBundle.getMessage("bad_request"), 3);

            }
        } catch (JsonSyntaxException e) {
            sendErrorMessage(ch, Messages.STATUS_LOGIN, MessageResourceBundle.getMessage("invalid_format_num_player"), 3);
        }
    }

    /**
     * Handles the SET_USERNAME login message.
     * If the user is not authenticated, tries to add him to loggedUsers.
     * If the user is already authenticated, tries to change his username
     *
     * @param ch       the {@code Communicable} interface of the client who sent the message
     * @param username a username
     */
    private void handleSetUsername(Communicable ch, String username) {
        if (loggedUsers.stream().anyMatch(user -> user.getCommunicable() == ch)) {
            renameUser(ch, username);
        } else {
            addUser(ch, username);
        }
    }

    /**
     * Sets the desired number of players of the game and whether to play in expert mode
     *
     * @param ch         the {@code Communicable} interface of the client who sent the message
     * @param numPlayers the value contained in the message received
     */
    private void setGameParameters(Communicable ch, int numPlayers, boolean isExpert) {
        if (loggedUsers.isEmpty() || loggedUsers.get(0).getCommunicable() != ch) {
            sendErrorMessage(ch, Messages.STATUS_LOGIN, MessageResourceBundle.getMessage("invalid_player_creating_game"), 3);
            return;
        }
        if (numPlayers < Constants.MIN_PLAYERS || numPlayers > Constants.MAX_PLAYERS) {
            sendErrorMessage(ch, Messages.STATUS_LOGIN, MessageResourceBundle.getMessage("invalid_num_players"), 3);
            return;
        }

        gameLobby.setNumPlayers(numPlayers);
        gameLobby.setIsExpert(isExpert);
        System.out.println("GAME CREATED | " + numPlayers + " players | " + (isExpert ? "expert" : "non expert") + " mode");

        if (!startGameIfReady()) {
            ServerLoginMessage message = getServerLoginMessage(Messages.GAME_CREATED);
            for (PlayerClient player : loggedUsers) {
                player.getCommunicable().sendMessageToClient(message.toJson());
            }
        }
    }

    private void setLoadedGameParameters(Communicable ch) {
        // Only the "host" loggedUsers[0] can load a game
        if (loggedUsers.isEmpty() || loggedUsers.get(0).getCommunicable() != ch) {
            sendErrorMessage(ch, Messages.STATUS_LOGIN, MessageResourceBundle.getMessage("invalid_player_creating_game"), 3);
            return;
        }

        // try to load the saved game, in case of failure send error
        try {
            String username = loggedUsers.get(0).getUsername();
            loadedGame = SavedGameState.loadFromFile();
            String[] loadedPlayers = loadedGame.getPlayers().stream().map(Player::getName).toArray(String[]::new);
            gameLobby.setPlayersFromSavedGame(loadedPlayers);
            if (!Arrays.asList(loadedPlayers).contains(username)) {
                loadedGame = null;
                gameLobby.resetPreferences();

                // send "you are not present in the loaded game" error and logout user (he has to log in again)
                sendErrorMessage(ch, Messages.STATUS_LOGIN, MessageResourceBundle.getMessage("username_not_in_loaded_game"), 5);
            } else {
                gameLobby.setNumPlayers(loadedPlayers.length);
                gameLobby.setIsExpert(loadedGame.isExpert());
                System.out.println("GAME LOADED FROM FILE | " + gameLobby.getNumPlayers() + " players | " + (gameLobby.isExpert() ? "expert" : "non expert") + " mode");
                sanitizePlayers();  // ensures players are in the same order as in the loaded game

                if (!startGameIfReady()) {
                    ServerLoginMessage message = getServerLoginMessage(Messages.GAME_LOADED);
                    for (PlayerClient player : loggedUsers) {
                        player.getCommunicable().sendMessageToClient(message.toJson());
                    }
                }
            }
        } catch (IOException | NoSuchElementException e) {
            // send "load failed" error
            System.out.println(Messages.LOAD_ERR);
            sendErrorMessage(ch, Messages.STATUS_LOGIN, MessageResourceBundle.getMessage("load_game_failed"), 4);
        }
    }

    /**
     * Adds the user who sent the message to the list of logged users
     *
     * @param ch       the {@code Communicable} interface of the client who sent the message
     * @param username the username of the user to be added
     */
    private void addUser(Communicable ch, String username) {
        if (username == null || username.trim().equals("")) {
            sendErrorMessage(ch, Messages.STATUS_LOGIN, MessageResourceBundle.getMessage("invalid_username"), 3);
        } else if ((gameLobby.getNumPlayers() != -1 && loggedUsers.size() >= gameLobby.getNumPlayers()) || loggedUsers.size() >= Constants.MAX_PLAYERS || gameController != null) {
            sendErrorMessage(ch, Messages.STATUS_LOGIN, MessageResourceBundle.getMessage("lobby_full"), 1);
        } else if (username.length() > Constants.MAX_USERNAME_LENGTH) {
            sendErrorMessage(ch, Messages.STATUS_LOGIN, MessageResourceBundle.getMessage("username_too_long"), 3);
        } else if (loggedUsers.stream().anyMatch(u -> u.getUsername().equals(username))) {
            sendErrorMessage(ch, Messages.STATUS_LOGIN, MessageResourceBundle.getMessage("username_already_taken"), 2);
        } else if (gameLobby.isFromSavedGame() && !Arrays.asList(gameLobby.getPlayersFromSavedGame()).contains(username)) {
            sendErrorMessage(ch, Messages.STATUS_LOGIN, MessageResourceBundle.getMessage("player_not_in_loaded_game"), 5);
        } else {
            PlayerClient newUser = new PlayerClient(ch, username);
            loggedUsers.add(newUser);
            gameLobby.addPlayer(username);
            System.out.println(MessageResourceBundle.getMessage("added_player") + newUser.getUsername());

            if (newUser == loggedUsers.get(0)) {
                askDesiredNumberOfPlayers(ch);
                return;
            }

            if (!startGameIfReady()) {
                sendBroadcastMessage();     // signals everyone that a new player has joined
            }
        }
    }

    /**
     * Changes the username of the user who sent the message
     *
     * @param ch          the {@code Communicable} interface of the client who sent the message
     * @param newUsername the username of the user to be added
     */
    private void renameUser(Communicable ch, String newUsername) {
        if (newUsername == null || newUsername.trim().equals("")) {
            sendErrorMessage(ch, Messages.STATUS_LOGIN, MessageResourceBundle.getMessage("invalid_username"), 5);
        } else if ((gameLobby.getNumPlayers() != -1 && loggedUsers.size() >= gameLobby.getNumPlayers()) || loggedUsers.size() >= Constants.MAX_PLAYERS || gameController != null) {
            sendErrorMessage(ch, Messages.STATUS_LOGIN, MessageResourceBundle.getMessage("lobby_full"), 5);
        } else if (newUsername.length() > Constants.MAX_USERNAME_LENGTH) {
            sendErrorMessage(ch, Messages.STATUS_LOGIN, MessageResourceBundle.getMessage("username_too_long"), 5);
        } else if (loggedUsers.stream().anyMatch(u -> u.getUsername().equals(newUsername))) {
            sendErrorMessage(ch, Messages.STATUS_LOGIN, MessageResourceBundle.getMessage("username_already_taken"), 5);
        } else if (gameLobby.isFromSavedGame() && !Arrays.asList(gameLobby.getPlayersFromSavedGame()).contains(newUsername)) {
            sendErrorMessage(ch, Messages.STATUS_LOGIN, MessageResourceBundle.getMessage("username_not_in_loaded_game"), 5);
        } else {
            try {
                PlayerClient userToRename = loggedUsers.stream()
                        .filter(user -> user.getCommunicable() == ch)
                        .findFirst().orElseThrow();

                String oldUsername = userToRename.getUsername();

                userToRename.setUsername(newUsername);

                // replaces old username with new one in game lobby
                gameLobby.removePlayer(oldUsername);
                gameLobby.addPlayer(newUsername);
                System.out.println("player " + oldUsername + " is now player " + newUsername);

                if (userToRename == loggedUsers.get(0)) {
                    askDesiredNumberOfPlayers(ch);
                }

                if (!startGameIfReady()) {
                    sendBroadcastMessage();
                }
            } catch (Exception e) {
                sendErrorMessage(ch, Messages.STATUS_LOGIN, MessageResourceBundle.getMessage("internal_server_error"), 5);
            }
        }
    }


    /**
     * Sends a message to every logged user containing the usernames of all logged users and the desired number of players
     * of the game, which can be -1 (not specified yet), 2, 3 or 4
     */
    private void sendBroadcastMessage() {
        ServerLoginMessage res = getServerLoginMessage(MessageResourceBundle.getMessage("new_player_joined"));

        // Notify the "host" only if he already picked the game preferences
        if (gameLobby.getNumPlayers() != -1) {
            loggedUsers.get(0).getCommunicable().sendMessageToClient(res.toJson());
        }

        for (int i = 1; i < loggedUsers.size(); i++) {
            loggedUsers.get(i).getCommunicable().sendMessageToClient(res.toJson());
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
        res.setGameLobby(gameLobby);
        return res;
    }

    /**
     * Sends a message asking for the desired number of players and the mode of the game
     *
     * @param ch the {@code Communicable} interface of the player to send the message to
     */
    private void askDesiredNumberOfPlayers(Communicable ch) {
        ServerLoginMessage res = new ServerLoginMessage();
        res.setAction(Messages.ACTION_CREATE_GAME);
        res.setDisplayText(MessageResourceBundle.getMessage("set_game_parameters"));

        ch.sendMessageToClient(res.toJson());

    }

    /**
     * Checks if a game can be started; if so, every client in the lobby is notified
     *
     * @return true if the game was started, false otherwise
     */
    private boolean startGameIfReady() {
        int numberOfPlayers = gameLobby.getNumPlayers();
        boolean isExpert = gameLobby.isExpert();

        if (numberOfPlayers == -1 || loggedUsers.size() < numberOfPlayers) return false;

        while (numberOfPlayers < loggedUsers.size()) {
            // Alert player that game is full and removes him
            PlayerClient toRemove = loggedUsers.get(numberOfPlayers);
            String errorMessage = "A new game for " + numberOfPlayers + " players is starting. Your connection will be closed";
            sendErrorMessage(toRemove.getCommunicable(), Messages.STATUS_LOGIN, errorMessage, 1);
            loggedUsers.remove(toRemove);
        }

        if (gameLobby.isFromSavedGame()) {
            String message = MessageResourceBundle.getMessage("game_resuming");
            ServerLoginMessage toSend = getServerLoginMessage(message);

            for (PlayerClient playerClient : loggedUsers) {
                // Alert player that game is resuming
                playerClient.getCommunicable().sendMessageToClient(toSend.toJson());
                playerClient.setPlayer(
                        loadedGame.getPlayers().stream()
                                .filter(player -> player.getName().equals(playerClient.getUsername()))
                                .findFirst().orElseThrow()
                );
            }

            gameController = new GameController(loggedUsers, loadedGame);
            gameController.resume();

            System.out.println(MessageResourceBundle.getMessage("game_resuming"));
            return true;
        }

        String message = MessageResourceBundle.getMessage("game_starting");
        if (numberOfPlayers == 4) {
            message += ". The teams are: " + loggedUsers.get(0).getUsername() + " and " + loggedUsers.get(2).getUsername() +
                    " [WHITE team]  VS  " + loggedUsers.get(1).getUsername() + " and " + loggedUsers.get(3).getUsername() + " [BLACK team]";
        }
        ServerLoginMessage toSend = getServerLoginMessage(message);

        for (PlayerClient playerClient : loggedUsers) {
            // Alert player that game is starting
            playerClient.getCommunicable().sendMessageToClient(toSend.toJson());
            playerClient.setPlayer(new Player(playerClient.getUsername(), numberOfPlayers % 2 == 0 ? Constants.TOWERS_IN_TWO_OR_FOUR_PLAYER_GAME : Constants.TOWERS_IN_THREE_PLAYER_GAME));
        }

        gameController = new GameController(loggedUsers, isExpert);
        gameController.start();

        System.out.println(Messages.GAME_IS_STARTING);
        return true;
    }

    /**
     * If loading from a saved game, assures that the loggedUsers are in the same order as in the loaded game
     * and kicks any player who was not in the loaded game
     */
    private void sanitizePlayers() {
        if (!gameLobby.isFromSavedGame()) return;

        List<PlayerClient> loggedUsersInOrder = new ArrayList<>();
        // Add each player client to the list in the same order as they appear in the loaded game
        for (int i = 0; i < gameLobby.getNumPlayers(); i++) {
            String playerName = gameLobby.getPlayersFromSavedGame()[i];
            if (gameLobby.getPlayers().contains(playerName)) {
                loggedUsersInOrder.add(loggedUsers.stream()
                        .filter(u -> u.getUsername().equals(playerName))
                        .findAny()
                        .orElseThrow());
            }
        }

        // Kick all clients who are not in the loaded game
        List<PlayerClient> clientsToKick = loggedUsers.stream()
                .filter(user -> !loggedUsersInOrder.contains(user))
                .toList();

        clientsToKick.forEach(clientToKill -> {
            String errorMessage = "A new game for " + gameLobby.getNumPlayers() + " players is starting. Your connection will be closed";
            sendErrorMessage(clientToKill.getCommunicable(), Messages.STATUS_LOGIN, errorMessage, 1);
        });

        loggedUsers.clear();
        loggedUsers.addAll(loggedUsersInOrder);
    }

    /**
     * Deserializes and handles an action message
     *
     * @param jsonMessage Json string which contains an action message
     * @param ch          the {@code Communicable} interface of the client who sent the message
     */
    private void handleActionMessage(String jsonMessage, Communicable ch) throws GameEndedException {
        if (gameController == null) {
            sendErrorMessage(ch, Messages.STATUS_ACTION, Messages.GAME_NOT_STARTED, 1);
            return;
        }

        try {
            ClientActionMessage actionMessage = ClientActionMessage.fromJSON(jsonMessage);
            gameController.handleActionMessage(actionMessage, ch);
        } catch (JsonSyntaxException e) {
            sendErrorMessage(ch, Messages.STATUS_ACTION, MessageResourceBundle.getMessage("bad_request_syntax"), 3);
        }
    }

    /**
     * Periodically sends a "ping" message to every client and awaits for a "pong" response.
     * This is done on a parallel thread
     */
    public void startPingPong() {
        Timer timer = new Timer("PING THREAD");
        bound = sendPingAndReturnBound();

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                synchronized (boundLock) {
                    if (pongCount < bound) {
                        for (PlayerClient user : loggedUsers) {
                            sendErrorMessage(user.getCommunicable(), Messages.STATUS_LOGIN, MessageResourceBundle.getMessage("connection_with_client_lost"), 3);
                        }
                        loggedUsers.clear();
                        gameLobby.clear();
                        gameController = null;
                        System.out.println(Messages.CLEARING_GAME);
                    }
                }

                bound = sendPingAndReturnBound();
            }
        };

        timer.schedule(task, 0, Constants.PING_INTERVAL);
    }

    /**
     * Sends a "PING" message to every client and returns how many messages have been sent
     *
     * @return how many "PING" messages have been sent
     */
    private int sendPingAndReturnBound() {
        synchronized (boundLock) {
            pongCount = 0;
        }
        Message ping = new Message();
        ping.setStatus(Messages.STATUS_PING);
        int bound = 0;
        for (PlayerClient user : loggedUsers) {
            user.getCommunicable().sendMessageToClient(ping.toJson());
            bound++;
        }
        return bound;
    }

    public int getPongCount() {
        return pongCount;
    }

    public ArrayList<PlayerClient> getLoggedUsers() {
        return new ArrayList<>(loggedUsers);
    }

    public Game getGame() {
        if (gameController == null) return null;
        return gameController.getGame();
    }
}