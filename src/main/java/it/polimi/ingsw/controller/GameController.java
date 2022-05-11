package it.polimi.ingsw.controller;

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
import it.polimi.ingsw.model.game_actions.PlayerActionPhase;
import it.polimi.ingsw.model.game_objects.Assistant;
import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.model.game_objects.dashboard_objects.DiningRoom;
import it.polimi.ingsw.model.game_objects.gameboard_objects.Island;
import it.polimi.ingsw.model.game_state.GameState;
import it.polimi.ingsw.server.ClientHandler;
import it.polimi.ingsw.server.PlayerClient;

import java.util.ArrayList;
import java.util.List;

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
        sendBroadcastUpdateMessage(firstPlayer);
        askForAssistant(firstPlayer);
    }

    /**
     * Handles a {@code ClientActionMessage} sent by a {@code Client}
     *
     * @param message the {@code ClientActionMessage} sent
     * @param ch      the {@code ClientHandler} to send the response message to
     * @throws GameEndedException if the {@code Game} is ended
     */
    public void handleActionMessage(ClientActionMessage message, ClientHandler ch) throws GameEndedException {
        if (message.getAction() == null) {
            sendErrorMessage(ch, "Invalid request", 3, "");
        }

        PlayerClient player = players.stream().filter(user -> ch.equals(user.getClientHandler())).toList().get(0);

        if (message.getAction().getName().equals("PLAY_ASSISTANT")) {
            handleAssistantPlayed(message.getAction(), player);
            return;
        }

        if (game.getCurrentRound().getCurrentPlayerActionPhase() != null && !isCorrectSender(message.getPlayer())) {
            sendErrorMessage(ch, "It's not your turn!", 1, "");
        }

        Action action = message.getAction();
        switch (action.getName()) {
            case "MOVE_STUDENT_TO_DINING" -> handleStudentMovedToDining(action, player);
            case "MOVE_STUDENT_TO_ISLAND" -> handleStudentMovedToIsland(action, player);
            case "MOVE_MN" -> handleMotherNatureMoved(action, player);
            case "FILL_FROM_CLOUD" -> handleFillFromCloud(action, player);
            case "PLAY_CHARACTER" -> handlePlayCharacter(action, player);
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
            sendErrorMessage(player.getClientHandler(), "Invalid argument", 2, action.getName());
        }

        Assistant assistant = player.getPlayer().getHand()[value - 1];
        try {
            game.getCurrentRound().getPlanningPhase().addAssistant(assistant);
        } catch (AlreadyPlayedAssistantException | SameAssistantPlayedException e) {
            sendErrorMessage(player.getClientHandler(), e.getMessage(), 2, action.getName());
            return;
        } catch (InvalidActionException e) {
            sendErrorMessage(player.getClientHandler(), e.getMessage(), 1, action.getName());
            return;
        }

        currentPlayerIndex = (currentPlayerIndex + 1) % game.getPlayers().size();
        PlayerClient curPlayer = players.get(currentPlayerIndex);


        if (game.getCurrentRound().getPlanningPhase().isEnded()) {
            // When I send the message to the nextPlayer, I just have to call currentPap - it's already the pap of the current player
            // When pap.currentPlayer == null I have to start another planning phase

            if (game.getCurrentRound().isLastRound()) {
                alertLastRound();
            }

            curPlayer = getPlayerClientFromPlayer(game.getCurrentRound().getCurrentPlayerActionPhase().getCurrentPlayer());
            sendBroadcastUpdateMessage(curPlayer);

            askForMoveInPAP(curPlayer);
        } else {
            sendBroadcastUpdateMessage(curPlayer);
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
            sendErrorMessage(player.getClientHandler(), "Invalid island index", 2, "MOVE_STUDENT_TO_ISLAND");
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
            sendErrorMessage(player.getClientHandler(), e.getMessage(), 1, actionName);
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
            sendErrorMessage(player.getClientHandler(), e.getMessage(), 1, "MOVE_MN");
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
            sendErrorMessage(player.getClientHandler(), e.getMessage(), 1, "FILL_FROM_CLOUD");
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
            sendErrorMessage(player.getClientHandler(), "This character is not in the game", 1, "PLAY_CHARACTER");
            return;
        }

        if (args.getIsland() != null) {
            selectedIsland = game.getGameBoard().getIslands().get(args.getIsland());
        }
        try {
            game.getCurrentRound().getCurrentPlayerActionPhase().playCharacter(
                    selectedCharacter, selectedIsland, args.getColor(), args.getSourceStudents(), args.getDstStudents()
            );
        } catch (InvalidCharacterException | CharacterAlreadyPlayedException | StudentNotOnTheCardException |
                 InvalidStudentException | NotEnoughCoinsException e) {
            sendErrorMessage(player.getClientHandler(), e.getMessage(), 2, "PLAY_CHARACTER");
            return;
        } catch (InvalidActionException e) {
            sendErrorMessage(player.getClientHandler(), e.getMessage(), 1, "PLAY_CHARACTER");
            return;
        }

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
        player.getClientHandler().sendMessageToClient(message.toJson());
    }

    /**
     * Sends an error message to the client
     *
     * @param ch The {@code ClientHandler} of the client who caused the error
     */
    private void sendErrorMessage(ClientHandler ch, String errorMessage, int errorCode, String action) {
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
        player.getClientHandler().sendMessageToClient(actionMessage.toJson());
    }

    /**
     * Sends to every {@code Player} except the one currently playing a message saying that the curPlayer is playing
     *
     * @param curPlayer the {@code Player} currently playing
     */
    private void sendBroadcastUpdateMessage(PlayerClient curPlayer) {
        UpdateMessage message = new UpdateMessage();
        message.setGameStatus(new GameState(game));
        message.setDisplayText(curPlayer.getUsername() + " is playing...");
        for (PlayerClient player : players) {
            player.getClientHandler().sendMessageToClient(message.toJson());
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

        sendBroadcastUpdateMessage(nextPlayer);

        askForMoveInPAP(nextPlayer);

    }

    /**
     * Alerts all the players that the current one is the last {@code Round}
     */
    private void alertLastRound() {
        ServerActionMessage actionMessage = new ServerActionMessage();
        actionMessage.setStatus("UPDATE");
        actionMessage.setDisplayText("Be aware! This is the last round");
        for (PlayerClient player : players) {
            player.getClientHandler().sendMessageToClient(actionMessage.toJson());
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

            player.getClientHandler().sendMessageToClient(actionMessage.toJson());
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
        ).findAny().get();
    }

    /**
     * Returns the {@code Player} who has the same username as the input one
     *
     * @param username the input username
     * @return the {@code Player} who has the same username as the input one
     */
    private Player getPlayerFromUsername(String username) {
        return players.stream().filter(
                (p) -> p.getUsername().equals(username)
        ).findAny().get().getPlayer();
    }
}
