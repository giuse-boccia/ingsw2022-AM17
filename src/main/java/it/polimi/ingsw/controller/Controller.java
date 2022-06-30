package it.polimi.ingsw.controller;

import com.google.gson.JsonSyntaxException;
import it.polimi.ingsw.exceptions.GameEndedException;
import it.polimi.ingsw.languages.Messages;
import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.messages.action.ClientActionMessage;
import it.polimi.ingsw.messages.action.ServerActionMessage;
import it.polimi.ingsw.messages.chat.SimpleChatMessage;
import it.polimi.ingsw.messages.login.ClientLoginMessage;
import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.messages.login.ServerLoginMessage;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.server.Communicable;
import it.polimi.ingsw.server.PlayerClient;
import it.polimi.ingsw.server.game_state.SavedGameState;
import it.polimi.ingsw.utils.constants.Constants;

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
    public static void sendErrorMessage(Communicable ch, String status, String errorMessage, int errorCode, Locale languageTag) {
        if (status.equals(Constants.STATUS_LOGIN)) {
            ServerLoginMessage message = new ServerLoginMessage();
            message.setError(errorCode);
            message.setDisplayText(Messages.getMessage("error_tag", languageTag) + errorMessage);
            ch.sendMessageToClient(message.toJson());
        } else if (status.equals(Constants.STATUS_ACTION)) {
            ServerActionMessage message = new ServerActionMessage();
            message.setError(errorCode);
            message.setDisplayText(Messages.getMessage("error_tag", languageTag) + errorMessage);
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
            case Constants.STATUS_LOGIN -> handleLoginMessage(jsonMessage, ch);
            case Constants.STATUS_ACTION -> handleActionMessage(jsonMessage, ch);
            case Constants.STATUS_PONG -> handlePong();
            case Constants.STATUS_CHAT -> handleChatMessage(jsonMessage, ch);
            default ->
                    sendErrorMessage(ch, Constants.STATUS_LOGIN, Messages.getMessage("unrecognised_type"), 3, Locale.ENGLISH);
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
            String languageTag = loginMessage.getLanguageTag();
            Locale locale = Locale.ENGLISH;
            if (languageTag != null && Constants.LANGUAGE_TAGS.contains(languageTag)) {
                locale = new Locale(loginMessage.getLanguageTag());
            }

            if (loginMessage.getAction() == null) {
                sendErrorMessage(ch, Constants.STATUS_LOGIN, Messages.getMessage("bad_request", locale), 3, locale);
                return;
            }

            switch (loginMessage.getAction()) {
                case Constants.ACTION_SET_USERNAME -> handleSetUsername(ch, loginMessage.getUsername(), locale);
                case Constants.ACTION_CREATE_GAME ->
                        setGameParameters(ch, loginMessage.getNumPlayers(), loginMessage.isExpert(), locale);
                case Constants.ACTION_LOAD_GAME -> setLoadedGameParameters(ch, locale);
                default ->
                        sendErrorMessage(ch, Constants.STATUS_LOGIN, Messages.getMessage("bad_request", locale), 3, locale);

            }
        } catch (JsonSyntaxException e) {
            sendErrorMessage(ch, Constants.STATUS_LOGIN, Messages.getMessage("invalid_format_num_player"), 3, Locale.ENGLISH);
        }
    }

    /**
     * Handles the SET_USERNAME login message.
     * If the user is not authenticated, tries to add him to loggedUsers.
     * If the user is already authenticated, tries to change his username
     *
     * @param ch       the {@code Communicable} interface of the client who sent the message
     * @param username a username
     * @param locale   the {@code Locale} of the user who sent the login message
     */
    private void handleSetUsername(Communicable ch, String username, Locale locale) {
        if (loggedUsers.stream().anyMatch(user -> user.getCommunicable() == ch)) {
            renameUser(ch, username, locale);
        } else {
            addUser(ch, username, locale);
        }
    }

    /**
     * Sets the desired number of players of the game and whether to play in expert mode
     *
     * @param ch         the {@code Communicable} interface of the client who sent the message
     * @param numPlayers the value contained in the message received
     * @param isExpert   true if the expert mode is active
     * @param locale     the {@code Locale} of the client who sent the message
     */
    private void setGameParameters(Communicable ch, int numPlayers, boolean isExpert, Locale locale) {
        if (loggedUsers.isEmpty() || loggedUsers.get(0).getCommunicable() != ch) {
            sendErrorMessage(ch, Constants.STATUS_LOGIN, Messages.getMessage("invalid_player_creating_game", locale), 3, locale);
            return;
        }
        if (numPlayers < Constants.MIN_PLAYERS || numPlayers > Constants.MAX_PLAYERS) {
            sendErrorMessage(ch, Constants.STATUS_LOGIN, Messages.getMessage("invalid_num_players", locale), 3, locale);
            return;
        }

        gameLobby.setNumPlayers(numPlayers);
        gameLobby.setIsExpert(isExpert);
        System.out.println(Messages.getMessage("create_ok") + numPlayers + " " + Messages.getMessage("players") + Messages.getMessage("expert_mode") + (isExpert ? "" : Messages.getMessage("not_with_space")) + Messages.getMessage("active"));

        if (!startGameIfReady()) {
            for (PlayerClient player : loggedUsers) {
                ServerLoginMessage message = getServerLoginMessage(Messages.getMessage("game_created", player.getLanguageTag()));
                player.getCommunicable().sendMessageToClient(message.toJson());
            }
        }
    }

    /**
     * Sets the parameters of a loaded game
     *
     * @param ch     the {@code Communicable} interface of the user who sent the message
     * @param locale {@code Locale} of the user who sent the message
     */
    private void setLoadedGameParameters(Communicable ch, Locale locale) {
        // Only the "host" loggedUsers[0] can load a game
        if (loggedUsers.isEmpty() || loggedUsers.get(0).getCommunicable() != ch) {
            sendErrorMessage(ch, Constants.STATUS_LOGIN, Messages.getMessage("invalid_player_creating_game", locale), 3, locale);
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
                sendErrorMessage(ch, Constants.STATUS_LOGIN, Messages.getMessage("username_not_in_loaded_game", locale), 5, locale);
            } else {
                gameLobby.setNumPlayers(loadedPlayers.length);
                gameLobby.setIsExpert(loadedGame.isExpert());
                System.out.println(Messages.getMessage("load_ok") + gameLobby.getNumPlayers() + " " + Messages.getMessage("players") + Messages.getMessage("expert_mode") + (gameLobby.isExpert() ? "" : Messages.getMessage("not_with_space")) + Messages.getMessage("active"));
                sanitizePlayers();  // ensures players are in the same order as in the loaded game

                if (!startGameIfReady()) {
                    for (PlayerClient player : loggedUsers) {
                        ServerLoginMessage message = getServerLoginMessage(Messages.getMessage("game_loaded", player.getLanguageTag()));
                        player.getCommunicable().sendMessageToClient(message.toJson());
                    }
                }
            }
        } catch (IOException | NoSuchElementException e) {
            // send "load failed" error
            System.out.println(Messages.getMessage("load_err"));
            sendErrorMessage(ch, Constants.STATUS_LOGIN, Messages.getMessage("load_game_failed", locale), 4, locale);
        }
    }

    /**
     * Adds the user who sent the message to the list of logged users
     *
     * @param ch       the {@code Communicable} interface of the client who sent the message
     * @param username the username of the user to be added
     * @param locale   the {@code Locale} of the user who sent the login message
     */
    private void addUser(Communicable ch, String username, Locale locale) {
        if (username == null || username.trim().equals("")) {
            sendErrorMessage(ch, Constants.STATUS_LOGIN, Messages.getMessage("invalid_username", locale), 3, locale);
        } else if ((gameLobby.getNumPlayers() != -1 && loggedUsers.size() >= gameLobby.getNumPlayers()) || loggedUsers.size() >= Constants.MAX_PLAYERS || gameController != null) {
            sendErrorMessage(ch, Constants.STATUS_LOGIN, Messages.getMessage("lobby_full", locale), 1, locale);
        } else if (username.length() > Constants.MAX_USERNAME_LENGTH) {
            sendErrorMessage(ch, Constants.STATUS_LOGIN, Messages.getMessage("username_too_long", locale), 3, locale);
        } else if (loggedUsers.stream().anyMatch(u -> u.getUsername().equals(username))) {
            sendErrorMessage(ch, Constants.STATUS_LOGIN, Messages.getMessage("username_already_taken", locale), 2, locale);
        } else if (gameLobby.isFromSavedGame() && !Arrays.asList(gameLobby.getPlayersFromSavedGame()).contains(username)) {
            sendErrorMessage(ch, Constants.STATUS_LOGIN, Messages.getMessage("player_not_in_loaded_game", locale), 5, locale);
        } else {
            PlayerClient newUser = new PlayerClient(ch, username);
            newUser.setLanguageTag(locale);
            loggedUsers.add(newUser);
            gameLobby.addPlayer(username);
            System.out.println(Messages.getMessage("added_player") + newUser.getUsername());

            if (newUser == loggedUsers.get(0)) {
                askDesiredNumberOfPlayers(ch, locale);
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
     * @param locale      the {@code Locale} of the user who sent the login message
     */
    private void renameUser(Communicable ch, String newUsername, Locale locale) {
        if (newUsername == null || newUsername.trim().equals("")) {
            sendErrorMessage(ch, Constants.STATUS_LOGIN, Messages.getMessage("invalid_username", locale), 5, locale);
        } else if ((gameLobby.getNumPlayers() != -1 && loggedUsers.size() >= gameLobby.getNumPlayers()) || loggedUsers.size() >= Constants.MAX_PLAYERS || gameController != null) {
            sendErrorMessage(ch, Constants.STATUS_LOGIN, Messages.getMessage("lobby_full", locale), 5, locale);
        } else if (newUsername.length() > Constants.MAX_USERNAME_LENGTH) {
            sendErrorMessage(ch, Constants.STATUS_LOGIN, Messages.getMessage("username_too_long", locale), 5, locale);
        } else if (loggedUsers.stream().anyMatch(u -> u.getUsername().equals(newUsername))) {
            sendErrorMessage(ch, Constants.STATUS_LOGIN, Messages.getMessage("username_already_taken", locale), 5, locale);
        } else if (gameLobby.isFromSavedGame() && !Arrays.asList(gameLobby.getPlayersFromSavedGame()).contains(newUsername)) {
            sendErrorMessage(ch, Constants.STATUS_LOGIN, Messages.getMessage("username_not_in_loaded_game", locale), 5, locale);
        } else {
            try {
                PlayerClient userToRename = loggedUsers.stream()
                        .filter(user -> user.getCommunicable() == ch)
                        .findFirst().orElseThrow();

                String oldUsername = userToRename.getUsername();

                userToRename.setUsername(newUsername);
                userToRename.setLanguageTag(locale);

                // replaces old username with new one in game lobby
                gameLobby.removePlayer(oldUsername);
                gameLobby.addPlayer(newUsername);
                System.out.println(Messages.getMessage("player") + oldUsername + Messages.getMessage("is_now") + newUsername);


                if (userToRename == loggedUsers.get(0)) {
                    askDesiredNumberOfPlayers(ch, locale);
                }

                if (!startGameIfReady()) {
                    sendBroadcastMessage();
                }
            } catch (Exception e) {
                sendErrorMessage(ch, Constants.STATUS_LOGIN, Messages.getMessage("internal_server_error", locale), 5, locale);
            }
        }
    }


    /**
     * Sends a message to every logged user containing the usernames of all logged users and the desired number of players
     * of the game, which can be -1 (not specified yet), 2, 3 or 4
     */
    private void sendBroadcastMessage() {
        // Notify the "host" only if he already picked the game preferences
        if (gameLobby.getNumPlayers() != -1) {
            Locale firstLocale = loggedUsers.get(0).getLanguageTag();
            ServerLoginMessage res = getServerLoginMessage(Messages.getMessage("new_player_joined", firstLocale));
            loggedUsers.get(0).getCommunicable().sendMessageToClient(res.toJson());
        }

        for (int i = 1; i < loggedUsers.size(); i++) {
            Locale locale = loggedUsers.get(i).getLanguageTag();
            ServerLoginMessage res = getServerLoginMessage(Messages.getMessage("new_player_joined", locale));
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
    private void askDesiredNumberOfPlayers(Communicable ch, Locale locale) {
        ServerLoginMessage res = new ServerLoginMessage();
        res.setAction(Constants.ACTION_CREATE_GAME);
        res.setDisplayText(Messages.getMessage("set_game_parameters", locale));

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
            Locale toRemoveLocale = toRemove.getLanguageTag();
            String errorMessage = Messages.getMessage("new_game_error_start", toRemoveLocale)
                    + numberOfPlayers +
                    Messages.getMessage("new_game_error_end", toRemoveLocale);
            sendErrorMessage(toRemove.getCommunicable(), Constants.STATUS_LOGIN, errorMessage, 1, toRemoveLocale);
            loggedUsers.remove(toRemove);
        }

        if (gameLobby.isFromSavedGame()) {

            for (PlayerClient playerClient : loggedUsers) {
                String message = Messages.getMessage("game_resuming", playerClient.getLanguageTag());
                ServerLoginMessage toSend = getServerLoginMessage(message);
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

            System.out.println(Messages.getMessage("game_resuming"));
            return true;
        }

        if (numberOfPlayers == 4) {
            for (PlayerClient playerClient : loggedUsers) {
                // Alert player that game is starting
                Locale locale = playerClient.getLanguageTag();
                String message = Messages.getMessage("game_starting", locale) + Messages.getMessage("teams", locale) +
                        loggedUsers.get(0).getUsername() + Messages.getMessage("and", locale) +
                        loggedUsers.get(2).getUsername() + Messages.getMessage("white", locale) +
                        loggedUsers.get(1).getUsername() + Messages.getMessage("and", locale) +
                        loggedUsers.get(3).getUsername() + Messages.getMessage("black", locale);
                ServerLoginMessage toSend = getServerLoginMessage(message);
                playerClient.getCommunicable().sendMessageToClient(toSend.toJson());
                playerClient.setPlayer(new Player(playerClient.getUsername(), Constants.TOWERS_IN_TWO_OR_FOUR_PLAYER_GAME));
            }
        } else {
            for (PlayerClient playerClient : loggedUsers) {
                // Alert player that game is starting
                Locale locale = playerClient.getLanguageTag();
                String message = Messages.getMessage("game_starting", locale);
                ServerLoginMessage toSend = getServerLoginMessage(message);
                playerClient.getCommunicable().sendMessageToClient(toSend.toJson());
                playerClient.setPlayer(new Player(playerClient.getUsername(), numberOfPlayers % 2 == 0 ? Constants.TOWERS_IN_TWO_OR_FOUR_PLAYER_GAME : Constants.TOWERS_IN_THREE_PLAYER_GAME));
            }
        }

        gameController = new GameController(loggedUsers, isExpert);
        gameController.start();

        System.out.println(Messages.getMessage("game_is_starting"));
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
            Locale locale = clientToKill.getLanguageTag();
            String errorMessage = Messages.getMessage("new_game_error_start", locale) + gameLobby.getNumPlayers() +
                    Messages.getMessage("new_game_error_end", locale);
            sendErrorMessage(clientToKill.getCommunicable(), Constants.STATUS_LOGIN, errorMessage, 1, locale);
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
            sendErrorMessage(ch, Constants.STATUS_ACTION, Messages.getMessage("game_not_started"), 1, Locale.ENGLISH);
            return;
        }

        try {
            ClientActionMessage actionMessage = ClientActionMessage.fromJSON(jsonMessage);
            gameController.handleActionMessage(actionMessage, ch);
        } catch (JsonSyntaxException e) {
            sendErrorMessage(ch, Constants.STATUS_ACTION, Messages.getMessage("bad_request_syntax"), 3, Locale.ENGLISH);
        }
    }

    /**
     * Deserializes and handles a chat message
     *
     * @param jsonMessage Json string which contains a chat message
     * @param ch          the {@code Communicable} interface of the client who sent the message
     */
    private void handleChatMessage(String jsonMessage, Communicable ch) {
        try {
            SimpleChatMessage chatMessage = SimpleChatMessage.fromJson(jsonMessage);
            for (PlayerClient user : loggedUsers) {
                if (user.getCommunicable().equals(ch)) continue;
                user.getCommunicable().sendMessageToClient(chatMessage.toJson());
            }
        } catch (JsonSyntaxException ignored) {
            // The chat message simply gets ignored
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
                            sendErrorMessage(user.getCommunicable(), Constants.STATUS_LOGIN, Messages.getMessage("connection_with_client_lost", user.getLanguageTag()), 3, user.getLanguageTag());
                        }
                        loggedUsers.clear();
                        gameLobby.clear();
                        gameController = null;
                        System.out.println(Messages.getMessage("clearing_game"));
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
        ping.setStatus(Constants.STATUS_PING);
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