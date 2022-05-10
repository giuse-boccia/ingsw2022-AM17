package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.messages.action.Action;
import it.polimi.ingsw.messages.action.ActionArgs;
import it.polimi.ingsw.messages.action.ClientActionMessage;
import it.polimi.ingsw.messages.action.ServerActionMessage;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Place;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.model.game_actions.PlayerActionPhase;
import it.polimi.ingsw.model.game_objects.Assistant;
import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.model.game_objects.Student;
import it.polimi.ingsw.model.game_objects.dashboard_objects.DiningRoom;
import it.polimi.ingsw.model.game_objects.gameboard_objects.Island;
import it.polimi.ingsw.model.utils.Students;
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

    public void start() {
        game.start();
        currentPlayerIndex = game.getCurrentRound().getFirstPlayerIndex();
        PlayerClient firstPlayer = players.get(currentPlayerIndex);
        askForAssistant(firstPlayer);
    }

    public void handleActionMessage(ClientActionMessage message, ClientHandler ch) {
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
            curPlayer = getPlayerClientFromPlayer(game.getCurrentRound().getCurrentPlayerActionPhase().getCurrentPlayer());
            askForMoveInPAP(curPlayer);
        } else {
            askForAssistant(curPlayer);
        }

        //sendBroadcastWaitingMessage(curPlayer);

    }

    private void handleStudentMovedToDining(Action action, PlayerClient player) {
        Color color = action.getArgs().getColor();
        DiningRoom dining = player.getPlayer().getDashboard().getDiningRoom();

        moveStudent(color, dining, player, "MOVE_STUDENT_TO_DINING");
    }

    private void handleStudentMovedToIsland(Action action, PlayerClient player) {
        Color color = action.getArgs().getColor();
        Integer islandIndex = action.getArgs().getIsland();

        if (islandIndex < 0 || islandIndex >= game.getGameBoard().getIslands().size()) {
            sendErrorMessage(player.getClientHandler(), "Invalid island index", 2, "MOVE_STUDENT_TO_ISLAND");
            return;
        }

        moveStudent(color, game.getGameBoard().getIslands().get(islandIndex), player, "MOVE_STUDENT_TO_ISLAND");
    }

    private void moveStudent(Color color, Place destination, PlayerClient player, String actionName) {
        try {
            game.getCurrentRound().getCurrentPlayerActionPhase().moveStudent(color, destination);
        } catch (InvalidActionException | InvalidStudentException e) {
            sendErrorMessage(player.getClientHandler(), e.getMessage(), 1, actionName);
            return;
        }

        sendMessagesInPAP();
    }

    private void handleMotherNatureMoved(Action action, PlayerClient player) {
        Integer steps = action.getArgs().getNum_steps();

        try {
            game.getCurrentRound().getCurrentPlayerActionPhase().moveMotherNature(steps);
        } catch (InvalidActionException | InvalidStepsForMotherNatureException e) {
            sendErrorMessage(player.getClientHandler(), e.getMessage(), 1, "MOVE_MN");
            return;
        }

        sendMessagesInPAP();
    }

    private void handleFillFromCloud(Action action, PlayerClient player) {
        Integer cloudNumber = action.getArgs().getCloud();

        try {
            game.getCurrentRound().getCurrentPlayerActionPhase().chooseCloud(cloudNumber);
        } catch (InvalidActionException | InvalidCloudException e) {
            sendErrorMessage(player.getClientHandler(), e.getMessage(), 1, "FILL_FROM_CLOUD");
            return;
        }

        if (game.getCurrentRound().getCurrentPlayerActionPhase() != null) {
            sendMessagesInPAP();
        } else {
            currentPlayerIndex = game.getCurrentRound().getFirstPlayerIndex();
            PlayerClient nextPlayer = players.get(currentPlayerIndex);
            askForAssistant(nextPlayer);
            //sendBroadcastWaitingMessage(nextPlayer);
        }
    }

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
        } catch (InvalidCharacterException | CharacterAlreadyPlayedException | StudentNotOnTheCardException | InvalidStudentException | NotEnoughCoinsException e) {
            sendErrorMessage(player.getClientHandler(), e.getMessage(), 2, "PLAY_CHARACTER");
            return;
        } catch (InvalidActionException e) {
            sendErrorMessage(player.getClientHandler(), e.getMessage(), 1, "PLAY_CHARACTER");
            return;
        }

        askForMoveInPAP(player);
    }

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
     * @param ch The {@code ClientHandler} of the client which caused the error
     */
    private void sendErrorMessage(ClientHandler ch, String errorMessage, int errorCode, String action) {
        ServerActionMessage message = new ServerActionMessage();
        message.setError(errorCode);
        message.setDisplayText("[ERROR] " + errorMessage);
        message.addAction(action);
        ch.sendMessageToClient(message.toJson());
    }

    private void askForAssistant(PlayerClient player) {
        ServerActionMessage actionMessage = new ServerActionMessage();
        actionMessage.addAction("PLAY_ASSISTANT");
        actionMessage.setPlayer(player.getUsername());
        player.getClientHandler().sendMessageToClient(actionMessage.toJson());
    }

    private void sendBroadcastWaitingMessage(PlayerClient curPlayer) {
        ServerActionMessage message = new ServerActionMessage();
        message.setPlayer(curPlayer.getUsername());
        message.setDisplayText(curPlayer.getUsername() + " is playing...");
        for (PlayerClient player : players) {
            if (player != curPlayer) {
                player.getClientHandler().sendMessageToClient(message.toJson());
            }
        }
    }

    private void sendMessagesInPAP() {
        PlayerClient nextPlayer = getPlayerClientFromPlayer(
                game.getCurrentRound().getCurrentPlayerActionPhase().getCurrentPlayer()
        );

        askForMoveInPAP(nextPlayer);

        //sendBroadcastWaitingMessage(nextPlayer);
    }

    private boolean isCorrectSender(String username) {
        return getPlayerFromUsername(username).equals(game.getCurrentRound().getCurrentPlayerActionPhase().getCurrentPlayer());
    }

    private PlayerClient getPlayerClientFromPlayer(Player player) {
        return players.stream().filter(
                (p) -> p.getPlayer() == player
        ).findAny().get();
    }

    private Player getPlayerFromUsername(String username) {
        return players.stream().filter(
                (p) -> p.getUsername().equals(username)
        ).findAny().get().getPlayer();
    }


}
