package it.polimi.ingsw.controller;

import it.polimi.ingsw.utils.constants.Constants;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.languages.Messages;
import it.polimi.ingsw.messages.action.Action;
import it.polimi.ingsw.messages.action.ActionArgs;
import it.polimi.ingsw.messages.action.ClientActionMessage;
import it.polimi.ingsw.messages.action.ServerActionMessage;
import it.polimi.ingsw.messages.update.UpdateMessage;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Place;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.model.game_actions.PlayerActionPhase;
import it.polimi.ingsw.model.game_objects.Assistant;
import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.model.game_objects.dashboard_objects.DiningRoom;
import it.polimi.ingsw.model.game_objects.gameboard_objects.Island;
import it.polimi.ingsw.server.game_state.GameState;
import it.polimi.ingsw.server.Communicable;
import it.polimi.ingsw.server.PlayerClient;
import it.polimi.ingsw.server.game_state.SavedGameState;

import java.util.*;

public class GameController {

    private final Game game;
    private final List<PlayerClient> players;
    private int currentPlayerIndex;

    public GameController(List<PlayerClient> players, boolean expert) {
        game = new Game(getPlayers(players), expert);
        this.players = players;
    }

    public GameController(List<PlayerClient> players, Game game) {
        this.game = game;
        this.players = players;
    }

    private ArrayList<Player> getPlayers(List<PlayerClient> players) {
        List<Player> playersList = players.stream().map(PlayerClient::getPlayer).toList();
        return new ArrayList<>(playersList);
    }

    /**
     * Starts the {@code Game} and asks the first {@code Player} to play an {@code Assistant}
     */
    public void start() {
        game.start();
        currentPlayerIndex = game.getCurrentRound().getFirstPlayerIndex();
        PlayerClient firstPlayer = players.get(currentPlayerIndex);
        sendBroadcastUpdateMessage(firstPlayer.getUsername() + Messages.getMessage("is_playing"));
        askForAssistant(firstPlayer);
    }

    /**
     * Resumes the  {@code Game} and asks the next player to play
     */
    public void resume() {
        game.resume();

        if (game.getCurrentRound().getPlayedAssistants() != null) {
            // We are in someone's action phase
            String nextPlayerName = game.getCurrentRound().getCurrentPlayerActionPhase().getCurrentPlayer().getName();
            currentPlayerIndex = getIndexFromPlayerName(nextPlayerName);
            PlayerClient nextPlayer = players.get(currentPlayerIndex);
            sendBroadcastUpdateMessage(nextPlayer.getUsername() + Messages.getMessage("is_playing"));
            askForMoveInPAP(nextPlayer);
        } else {
            // We are in the planning phase
            currentPlayerIndex = game.getCurrentRound().getFirstPlayerIndex();
            PlayerClient nextPlayer = players.get(currentPlayerIndex);
            sendBroadcastUpdateMessage(nextPlayer.getUsername() + Messages.getMessage("is_playing"));
            askForAssistant(nextPlayer);
        }
    }

    /**
     * Given a player name, return his index in the players array
     *
     * @param playerToFind the name of the player to find
     * @return the index of the found player
     */
    private int getIndexFromPlayerName(String playerToFind) {
        for (int i = 0; i < players.size(); i++) {
            if (players.get(i).getPlayer().getName().equals(playerToFind)) {
                return i;
            }
        }

        throw new RuntimeException(playerToFind + " not in players!");    // shouldn't happen, just for debugging
    }

    /**
     * Handles a {@code ClientActionMessage} sent by a {@code Client}
     *
     * @param message the {@code ClientActionMessage} sent
     * @param ch      the {@code Communicable} interface to send the response message to
     * @throws GameEndedException if the {@code Game} is ended
     */
    public void handleActionMessage(ClientActionMessage message, Communicable ch) throws GameEndedException {
        List<PlayerClient> playersMatchingCh = players.stream().filter(user -> ch.equals(user.getCommunicable())).toList();
        if (playersMatchingCh.isEmpty()) {
            sendActionErrorMessage(ch, Messages.getMessage("not_logged_in"), 3, "", Locale.ENGLISH);
            return;
        }
        PlayerClient player = playersMatchingCh.get(0);
        Locale locale = player.getLanguageTag();

        if (message.getAction() == null) {
            sendActionErrorMessage(ch, Messages.getMessage("invalid_request", locale), 3, "", locale);
            return;
        }

        if (!Objects.equals(player.getPlayer().getName(), message.getPlayer())) {
            sendActionErrorMessage(ch, Messages.getMessage("invalid_identity", locale), 3, "", locale);
            return;
        }

        if (message.getAction().getName().equals(Constants.ACTION_PLAY_ASSISTANT)) {
            handleAssistantPlayed(message.getAction(), player);
            return;
        }

        if (game.getCurrentRound().getCurrentPlayerActionPhase() != null && !isCorrectSender(message.getPlayer())) {
            sendActionErrorMessage(ch, Messages.getMessage("not_your_turn", locale), 1, "", locale);
            return;
        }

        Action action = message.getAction();
        switch (action.getName()) {
            case Constants.ACTION_MOVE_STUDENT_TO_DINING -> handleStudentMovedToDining(action, player);
            case Constants.ACTION_MOVE_STUDENT_TO_ISLAND -> handleStudentMovedToIsland(action, player);
            case Constants.ACTION_MOVE_MN -> handleMotherNatureMoved(action, player);
            case Constants.ACTION_FILL_FROM_CLOUD -> handleFillFromCloud(action, player);
            case Constants.ACTION_PLAY_CHARACTER -> handlePlayCharacter(action, player);
            default -> sendActionErrorMessage(ch, Messages.getMessage("invalid_request", locale), 3, "", locale);
        }
    }

    /**
     * Handles a message with status PLAY_ASSISTANT
     *
     * @param action the {@code Action} object contained in the message being handled
     * @param player the {@code PlayerClient} who sent the message being handled
     */
    private void handleAssistantPlayed(Action action, PlayerClient player) {
        Integer value = action.getArgs().getValue();
        Locale locale = player.getLanguageTag();
        if (value < Constants.MIN_ASSISTANT_VALUE || value > Constants.MAX_ASSISTANT_VALUE) {
            sendActionErrorMessage(player.getCommunicable(), Messages.getMessage("invalid_argument", locale), 2, action.getName(), locale);
            return;
        }

        Assistant assistant = player.getPlayer().getHand()[value - 1];
        try {
            game.getCurrentRound().getPlanningPhase().addAssistant(assistant);
        } catch (AlreadyPlayedAssistantException | SameAssistantPlayedException e) {
            sendActionErrorMessage(player.getCommunicable(), Messages.getMessage(e.getMessage(), locale), 2, action.getName(), locale);
            return;
        } catch (InvalidActionException e) {
            sendActionErrorMessage(player.getCommunicable(), Messages.getMessage(e.getMessage(), locale), 1, action.getName(), locale);
            return;
        }

        PlayerClient curPlayer;
        String text = player.getUsername() + Messages.getMessage("broadcast_assistant") + (value) + Messages.getMessage("broadcast_separator");

        if (game.getCurrentRound().getPlanningPhase().isEnded()) {
            SavedGameState.saveToFile(game);
            // When I send the message to the nextPlayer, I just have to call currentPap - it's already the pap of the current player
            // When pap.currentPlayer == null I have to start another planning phase

            curPlayer = getPlayerClientFromPlayer(game.getCurrentRound().getCurrentPlayerActionPhase().getCurrentPlayer());

            text += curPlayer.getUsername() + Messages.getMessage("is_playing");
            if (game.getCurrentRound().isLastRound()) {
                text = Messages.getMessage("last_round") + " ";
            }
            sendBroadcastUpdateMessage(text);

            askForMoveInPAP(curPlayer);
        } else {
            curPlayer = getPlayerClientFromPlayer(game.getCurrentRound().getPlanningPhase().getNextPlayer());
            sendBroadcastUpdateMessage(text + curPlayer.getUsername() + Messages.getMessage("is_playing"));
            askForAssistant(curPlayer);
        }


    }

    /**
     * Handles a message with status MOVE_STUDENT_TO_DINING
     *
     * @param action the {@code Action} object contained in the message being handled
     * @param player the {@code PlayerClient} who sent the message being handled
     * @throws GameEndedException if the {@code Game} is ended
     */
    private void handleStudentMovedToDining(Action action, PlayerClient player) throws GameEndedException {
        Color color = action.getArgs().getColor();
        DiningRoom dining = player.getPlayer().getDashboard().getDiningRoom();

        String text = player.getUsername() + " moved a " + color + Messages.getMessage("broadcast_to_dining");
        moveStudent(color, dining, player, Constants.ACTION_MOVE_STUDENT_TO_DINING, text);
    }

    /**
     * Handles a message with status MOVE_STUDENT_TO_ISLAND
     *
     * @param action the {@code Action} object contained in the message being handled
     * @param player the {@code PlayerClient} who sent the message being handled
     * @throws GameEndedException if the {@code Game} is ended
     */
    private void handleStudentMovedToIsland(Action action, PlayerClient player) throws GameEndedException {
        Color color = action.getArgs().getColor();
        Integer islandIndex = action.getArgs().getIsland();
        Locale locale = player.getLanguageTag();

        if (islandIndex < 0 || islandIndex >= game.getGameBoard().getIslands().size()) {
            sendActionErrorMessage(player.getCommunicable(), Messages.getMessage("invalid_island", locale), 2, Constants.ACTION_MOVE_STUDENT_TO_ISLAND, locale);
            return;
        }

        String text = player.getUsername() + " moved a " + color + Messages.getMessage("broadcast_to_island") + (islandIndex + 1);
        moveStudent(color, game.getGameBoard().getIslands().get(islandIndex), player, Constants.ACTION_MOVE_STUDENT_TO_ISLAND, text);
    }

    /**
     * Moves a {@code Student} of the selected {@code Color} to the correct destination
     *
     * @param color       the selected {@code Color}
     * @param destination the selected destination
     * @param player      the {@code Player} who wants to move the {@code Student}
     * @param actionName  the action name to send to the {@code Player} if there was an error in their choice
     * @throws GameEndedException if the {@code Game} is ended
     */
    private void moveStudent(Color color, Place destination, PlayerClient player, String actionName, String message) throws GameEndedException {
        try {
            game.getCurrentRound().getCurrentPlayerActionPhase().moveStudent(color, destination);
        } catch (InvalidActionException | InvalidStudentException e) {
            Locale locale = player.getLanguageTag();
            sendActionErrorMessage(player.getCommunicable(), Messages.getMessage(e.getMessage(), locale), 1, actionName, locale);
            return;
        }

        sendMessagesInPAP(message);
    }

    /**
     * Handles a message with status MOVE_MN
     *
     * @param action the {@code Action} object contained in the message being handled
     * @param player the {@code PlayerClient} who sent the message being handled
     * @throws GameEndedException if the {@code Game} is ended
     */
    private void handleMotherNatureMoved(Action action, PlayerClient player) throws GameEndedException {
        Integer steps = action.getArgs().getNum_steps();
        Locale locale = player.getLanguageTag();

        try {
            game.getCurrentRound().getCurrentPlayerActionPhase().moveMotherNature(steps);
        } catch (InvalidActionException | InvalidStepsForMotherNatureException e) {
            sendActionErrorMessage(player.getCommunicable(), Messages.getMessage(e.getMessage(), locale), 1, Constants.ACTION_MOVE_MN, locale);
            return;
        }

        String text = player.getUsername() + Messages.getMessage("broadcast_for_mother_nature") + steps + " steps";
        sendMessagesInPAP(text);
    }

    /**
     * Handles a message with status FILL_FROM_CLOUD
     *
     * @param action the {@code Action} object contained in the message being handled
     * @param player the {@code PlayerClient} who sent the message being handled
     * @throws GameEndedException if the {@code Game} is ended
     */
    private void handleFillFromCloud(Action action, PlayerClient player) throws GameEndedException {
        Integer cloudNumber = action.getArgs().getCloud();
        Locale locale = player.getLanguageTag();

        try {
            game.getCurrentRound().getCurrentPlayerActionPhase().chooseCloud(cloudNumber);
        } catch (InvalidActionException | InvalidCloudException e) {
            sendActionErrorMessage(player.getCommunicable(), Messages.getMessage(e.getMessage(), locale), 1, Constants.ACTION_FILL_FROM_CLOUD, locale);
            return;
        }

        SavedGameState.saveToFile(game);

        String text = player.getUsername() + Messages.getMessage("broadcast_fill_from_cloud") + (cloudNumber + 1);
        if (game.getCurrentRound().getCurrentPlayerActionPhase() != null || game.isEnded()) {
            sendMessagesInPAP(text);
        } else {
            currentPlayerIndex = game.getCurrentRound().getFirstPlayerIndex();
            PlayerClient nextPlayer = players.get(currentPlayerIndex);
            sendBroadcastUpdateMessage(text + Messages.getMessage("broadcast_separator") + nextPlayer.getUsername() + Messages.getMessage("is_playing"));
            askForAssistant(nextPlayer);
        }
    }

    /**
     * Handles a message with status PLAY_CHARACTER
     *
     * @param action the {@code Action} object contained in the message being handled
     * @param player the {@code PlayerClient} who sent the message being handled
     */
    private void handlePlayCharacter(Action action, PlayerClient player) {
        ActionArgs args = action.getArgs();
        Locale locale = player.getLanguageTag();
        Character selectedCharacter = null;
        Island selectedIsland = null;
        for (Character character : game.getGameBoard().getCharacters()) {
            if (character.getCardName() == args.getCharacterName())
                selectedCharacter = character;
        }

        if (selectedCharacter == null) {
            sendActionErrorMessage(player.getCommunicable(), Messages.getMessage("character_not_in_game", locale), 1, Constants.ACTION_PLAY_CHARACTER, locale);
            return;
        }

        if (args.getIsland() != null && args.getIsland() >= 0 && args.getIsland() < game.getGameBoard().getIslands().size()) {
            selectedIsland = game.getGameBoard().getIslands().get(args.getIsland());
        }
        try {
            game.getCurrentRound().getCurrentPlayerActionPhase().playCharacter(
                    selectedCharacter, selectedIsland, args.getColor(), args.getSourceStudents(), args.getDstStudents()
            );
        } catch (InvalidCharacterException | CharacterAlreadyPlayedException | StudentNotOnTheCardException |
                InvalidStudentException | NotEnoughCoinsException e) {
            sendActionErrorMessage(player.getCommunicable(), Messages.getMessage(e.getMessage(), locale), 2, Constants.ACTION_PLAY_CHARACTER, locale);
            return;
        } catch (InvalidActionException e) {
            sendActionErrorMessage(player.getCommunicable(), Messages.getMessage(e.getMessage(), locale), 1, Constants.ACTION_PLAY_CHARACTER, locale);
            return;
        }

        String text = selectedCharacter.getCardName() == CharacterName.everyOneMove3FromDiningRoomToBag ?
                "Everyone lost up to three " + args.getColor() + " students from their dining room" : player.getUsername() + Messages.getMessage("is_playing");

        sendBroadcastUpdateMessage(text);
        askForMoveInPAP(player);
    }

    /**
     * Sends the list of actions that the {@code Player} can do in the {@code PlayerActionPhase}
     *
     * @param player the {@code PlayerClient} to send the message to
     */
    private void askForMoveInPAP(PlayerClient player) {
        PlayerActionPhase currentPAP = game.getCurrentRound().getCurrentPlayerActionPhase();
        ServerActionMessage message = new ServerActionMessage();
        String action = currentPAP.getExpectedAction();
        if (action.equals(Constants.ACTION_MOVE_STUDENT)) {
            message.addAction(action + "_TO_DINING");
            message.addAction(action + "_TO_ISLAND");
        } else {
            message.addAction(action);
        }
        if (game.isExpert() && currentPAP.canPlayCharacter()) {
            message.addAction(Constants.ACTION_PLAY_CHARACTER);
        }
        message.setPlayer(player.getUsername());
        player.getCommunicable().sendMessageToClient(message.toJson());
    }

    /**
     * Sends an error message to the client
     *
     * @param ch The {@code Communicable} interface of the client who caused the error
     */
    private void sendActionErrorMessage(Communicable ch, String errorMessage, int errorCode, String action, Locale locale) {
        ServerActionMessage message = new ServerActionMessage();
        message.setError(errorCode);
        message.setDisplayText(Messages.getMessage("error_tag", locale) + errorMessage);
        if (action.equals(Constants.ACTION_PLAY_CHARACTER)) {
            String expectedAction = game.getCurrentRound().getCurrentPlayerActionPhase().getExpectedAction();
            if (expectedAction.equals(Constants.ACTION_MOVE_STUDENT)) {
                message.addAction(expectedAction + "_TO_DINING");
                message.addAction(expectedAction + "_TO_ISLAND");
            } else {
                message.addAction(expectedAction);
            }
        }
        message.addAction(action);
        ch.sendMessageToClient(message.toJson());
    }

    /**
     * Sends a message to the {@code PlayerClient} asking to play an {@code Assistant}
     *
     * @param player the {@code PlayerClient} to send the message to
     */
    private void askForAssistant(PlayerClient player) {
        ServerActionMessage actionMessage = new ServerActionMessage();
        actionMessage.addAction(Constants.ACTION_PLAY_ASSISTANT);
        actionMessage.setPlayer(player.getUsername());
        player.getCommunicable().sendMessageToClient(actionMessage.toJson());
    }

    /**
     * Sends to every {@code Player} except the one currently playing a message saying that the curPlayer is playing
     */
    private void sendBroadcastUpdateMessage(String text) {
        UpdateMessage message = new UpdateMessage();
        message.setGameState(new GameState(game));
        message.setDisplayText(text);
        for (PlayerClient player : players) {
            player.getCommunicable().sendMessageToClient(message.toJson());
        }
    }

    /**
     * Sends the messages in the {@code PlayerActionPhase}
     *
     * @throws GameEndedException if the game is ended
     */
    private void sendMessagesInPAP(String message) throws GameEndedException {

        if (game.isEnded()) {
            alertGameEnded();
            throw new GameEndedException("game_ended");
        }

        PlayerClient nextPlayer = getPlayerClientFromPlayer(
                game.getCurrentRound().getCurrentPlayerActionPhase().getCurrentPlayer()
        );

        sendBroadcastUpdateMessage(message + Messages.getMessage("broadcast_separator") + nextPlayer.getUsername() + Messages.getMessage("is_playing"));

        askForMoveInPAP(nextPlayer);

    }

    /**
     * Alerts all the players that the {@code Game} is finished and shows the winners
     */
    private void alertGameEnded() {
        List<Player> winners = game.getWinners();
        String winnersText;
        for (PlayerClient player : players) {
            Locale locale = player.getLanguageTag();
            if (winners.size() == 2) {
                winnersText = winners.get(0).getName() + Messages.getMessage("and", locale) + winners.get(1).getName() + Messages.getMessage("broadcast_game_won", locale);
            } else {
                winnersText = winners.get(0).getName() + Messages.getMessage("broadcast_game_won", locale);
            }
            ServerActionMessage actionMessage = new ServerActionMessage();
            actionMessage.setStatus(Constants.STATUS_END);
            actionMessage.setPlayer(player.getUsername());

            if (winners.contains(player.getPlayer())) {
                // Win message
                actionMessage.setDisplayText(Messages.getMessage("game_won", locale));
            } else {
                // Defeat message
                actionMessage.setDisplayText(Messages.getMessage("game_lost", locale) + winnersText);
            }

            player.getCommunicable().sendMessageToClient(actionMessage.toJson());
        }
    }

    /**
     * Checks if the username of the message being handled is the same of the current player
     *
     * @param username the username of the message being handled
     * @return true if the usernames are the same
     */
    private boolean isCorrectSender(String username) {
        return getPlayerFromUsername(username).equals(game.getCurrentRound().getCurrentPlayerActionPhase().getCurrentPlayer());
    }

    /**
     * Returns the {@code PlayerClient} corresponding to the selected {@code Player}
     *
     * @param player the selected {@code Player}
     * @return the {@code PlayerClient} corresponding to the selected {@code Player}
     */
    private PlayerClient getPlayerClientFromPlayer(Player player) {
        return players.stream().filter(
                (p) -> p.getPlayer() == player
        ).findAny().orElse(null);
    }

    /**
     * Returns the {@code Player} who has the same username as the input one
     *
     * @param username the input username
     * @return the {@code Player} who has the same username as the input one
     */
    private Player getPlayerFromUsername(String username) {
        Optional<PlayerClient> playerClient = players.stream().filter(
                (p) -> p.getUsername().equals(username)
        ).findAny();
        if (playerClient.isPresent()) {
            return playerClient.get().getPlayer();
        }
        // If the message comes from a player which is not in the game the function returns a new, invalid, Player
        return new Player("", 6);
    }

    public Game getGame() {
        return game;
    }
}
