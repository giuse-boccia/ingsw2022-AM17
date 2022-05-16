package it.polimi.ingsw.controller;

import it.polimi.ingsw.constants.Messages;
import it.polimi.ingsw.exceptions.*;
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
import it.polimi.ingsw.model.game_state.GameState;
import it.polimi.ingsw.server.Communicable;
import it.polimi.ingsw.server.PlayerClient;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class GameController {

    private final Game game;
    private final List<PlayerClient> players;
    private int currentPlayerIndex;

    public GameController(List<PlayerClient> players, boolean expert) {
        game = new Game(getPlayers(players), expert);
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
        sendBroadcastUpdateMessage(firstPlayer.getUsername() + " is playing...");
        askForAssistant(firstPlayer);
    }

    /**
     * Handles a {@code ClientActionMessage} sent by a {@code Client}
     *
     * @param message the {@code ClientActionMessage} sent
     * @param ch      the {@code Communicable} interface to send the response message to
     * @throws GameEndedException if the {@code Game} is ended
     */
    public void handleActionMessage(ClientActionMessage message, Communicable ch) throws GameEndedException {
        if (message.getAction() == null) {
            sendActionErrorMessage(ch, Messages.INVALID_REQUEST, 3, "");
            return;
        }

        List<PlayerClient> playersMatchingCh = players.stream().filter(user -> ch.equals(user.getCommunicable())).toList();
        if (playersMatchingCh.isEmpty()) {
            sendActionErrorMessage(ch, Messages.NOT_LOGGED_IN, 3, "");
            return;
        }
        PlayerClient player = playersMatchingCh.get(0);

        if (!Objects.equals(player.getPlayer().getName(), message.getPlayer())) {
            sendActionErrorMessage(ch, Messages.INVALID_IDENTITY, 3, "");
            return;
        }

        if (message.getAction().getName().equals("PLAY_ASSISTANT")) {
            handleAssistantPlayed(message.getAction(), player);
            return;
        }

        if (game.getCurrentRound().getCurrentPlayerActionPhase() != null && !isCorrectSender(message.getPlayer())) {
            sendActionErrorMessage(ch, Messages.NOT_YOUR_TURN, 1, "");
            return;
        }

        Action action = message.getAction();
        switch (action.getName()) {
            case "MOVE_STUDENT_TO_DINING" -> handleStudentMovedToDining(action, player);
            case "MOVE_STUDENT_TO_ISLAND" -> handleStudentMovedToIsland(action, player);
            case "MOVE_MN" -> handleMotherNatureMoved(action, player);
            case "FILL_FROM_CLOUD" -> handleFillFromCloud(action, player);
            case "PLAY_CHARACTER" -> handlePlayCharacter(action, player);
            default -> sendActionErrorMessage(ch, Messages.INVALID_REQUEST, 3, "");
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
        if (value < 1 || value > 10) {
            sendActionErrorMessage(player.getCommunicable(), Messages.INVALID_ARGUMENT, 2, action.getName());
            return;
        }

        Assistant assistant = player.getPlayer().getHand()[value - 1];
        try {
            game.getCurrentRound().getPlanningPhase().addAssistant(assistant);
        } catch (AlreadyPlayedAssistantException | SameAssistantPlayedException e) {
            sendActionErrorMessage(player.getCommunicable(), e.getMessage(), 2, action.getName());
            return;
        } catch (InvalidActionException e) {
            sendActionErrorMessage(player.getCommunicable(), e.getMessage(), 1, action.getName());
            return;
        }

        PlayerClient curPlayer;

        if (game.getCurrentRound().getPlanningPhase().isEnded()) {
            // When I send the message to the nextPlayer, I just have to call currentPap - it's already the pap of the current player
            // When pap.currentPlayer == null I have to start another planning phase

            if (game.getCurrentRound().isLastRound()) {
                alertLastRound();
            }

            curPlayer = getPlayerClientFromPlayer(game.getCurrentRound().getCurrentPlayerActionPhase().getCurrentPlayer());
            sendBroadcastUpdateMessage(curPlayer.getUsername() + " is playing...");

            askForMoveInPAP(curPlayer);
        } else {
            curPlayer = getPlayerClientFromPlayer(game.getCurrentRound().getPlanningPhase().getNextPlayer());
            sendBroadcastUpdateMessage(curPlayer.getUsername() + " is playing...");
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

        moveStudent(color, dining, player, "MOVE_STUDENT_TO_DINING");
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

        if (islandIndex < 0 || islandIndex >= game.getGameBoard().getIslands().size()) {
            sendActionErrorMessage(player.getCommunicable(), "Invalid island index", 2, "MOVE_STUDENT_TO_ISLAND");
            return;
        }

        moveStudent(color, game.getGameBoard().getIslands().get(islandIndex), player, "MOVE_STUDENT_TO_ISLAND");
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
    private void moveStudent(Color color, Place destination, PlayerClient player, String actionName) throws GameEndedException {
        try {
            game.getCurrentRound().getCurrentPlayerActionPhase().moveStudent(color, destination);
        } catch (InvalidActionException | InvalidStudentException e) {
            sendActionErrorMessage(player.getCommunicable(), e.getMessage(), 1, actionName);
            return;
        }

        sendMessagesInPAP();
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

        try {
            game.getCurrentRound().getCurrentPlayerActionPhase().moveMotherNature(steps);
        } catch (InvalidActionException | InvalidStepsForMotherNatureException e) {
            sendActionErrorMessage(player.getCommunicable(), e.getMessage(), 1, "MOVE_MN");
            return;
        }

        sendMessagesInPAP();
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

        try {
            game.getCurrentRound().getCurrentPlayerActionPhase().chooseCloud(cloudNumber);
        } catch (InvalidActionException | InvalidCloudException e) {
            sendActionErrorMessage(player.getCommunicable(), e.getMessage(), 1, "FILL_FROM_CLOUD");
            return;
        }

        if (game.getCurrentRound().getCurrentPlayerActionPhase() != null || game.isEnded()) {
            sendMessagesInPAP();
        } else {
            currentPlayerIndex = game.getCurrentRound().getFirstPlayerIndex();
            PlayerClient nextPlayer = players.get(currentPlayerIndex);
            askForAssistant(nextPlayer);
            //sendBroadcastWaitingMessage(nextPlayer);
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
        Character selectedCharacter = null;
        Island selectedIsland = null;
        for (Character character : game.getGameBoard().getCharacters()) {
            if (character.getCardName() == args.getCharacterName())
                selectedCharacter = character;
        }

        if (selectedCharacter == null) {
            sendActionErrorMessage(player.getCommunicable(), Messages.CHARACTER_NOT_IN_GAME, 1, "PLAY_CHARACTER");
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
            sendActionErrorMessage(player.getCommunicable(), e.getMessage(), 2, "PLAY_CHARACTER");
            return;
        } catch (InvalidActionException e) {
            sendActionErrorMessage(player.getCommunicable(), e.getMessage(), 1, "PLAY_CHARACTER");
            return;
        }

        String text = selectedCharacter.getCardName() == CharacterName.everyOneMove3FromDiningRoomToBag ?
                "Everyone lost up to three " + args.getColor() + " students from their dining room" : player.getUsername() + " is playing...";

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
        if (action.equals("MOVE_STUDENT")) {
            message.addAction(action + "_TO_DINING");
            message.addAction(action + "_TO_ISLAND");
        } else {
            message.addAction(action);
        }
        if (game.isExpert() && currentPAP.canPlayCharacter()) {
            message.addAction("PLAY_CHARACTER");
        }
        message.setPlayer(player.getUsername());
        player.getCommunicable().sendMessageToClient(message.toJson());
    }

    /**
     * Sends an error message to the client
     *
     * @param ch The {@code Communicable} interface of the client who caused the error
     */
    private void sendActionErrorMessage(Communicable ch, String errorMessage, int errorCode, String action) {
        ServerActionMessage message = new ServerActionMessage();
        message.setError(errorCode);
        message.setDisplayText("[ERROR] " + errorMessage);
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
        actionMessage.addAction("PLAY_ASSISTANT");
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
    private void sendMessagesInPAP() throws GameEndedException {

        if (game.isEnded()) {
            alertGameEnded();
            throw new GameEndedException("The game is ended");
        }

        PlayerClient nextPlayer = getPlayerClientFromPlayer(
                game.getCurrentRound().getCurrentPlayerActionPhase().getCurrentPlayer()
        );

        sendBroadcastUpdateMessage(nextPlayer.getUsername() + " is playing...");

        askForMoveInPAP(nextPlayer);

    }

    /**
     * Alerts all the players that the current one is the last {@code Round}
     */
    private void alertLastRound() {
        UpdateMessage updateMessage = new UpdateMessage();
        updateMessage.setStatus("UPDATE");
        updateMessage.setDisplayText("Be aware! This is the last round");
        for (PlayerClient player : players) {
            player.getCommunicable().sendMessageToClient(updateMessage.toJson());
        }
    }

    /**
     * Alerts all the players that the {@code Game} is finished and shows the winners
     */
    private void alertGameEnded() {
        //TODO Show everyone who won the game (if i lose i want to know who won)
        ArrayList<Player> winners = game.getWinners();
        for (PlayerClient player : players) {
            ServerActionMessage actionMessage = new ServerActionMessage();
            actionMessage.setStatus("END");
            actionMessage.setPlayer(player.getUsername());

            if (winners.contains(player.getPlayer())) {
                // Win message
                actionMessage.setDisplayText("Congratulations, you won the game!");
            } else {
                // Defeat message
                actionMessage.setDisplayText("Game is ended. You lost");
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
