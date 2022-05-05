package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.AlreadyPlayedAssistantException;
import it.polimi.ingsw.exceptions.InvalidActionException;
import it.polimi.ingsw.exceptions.SameAssistantPlayedException;
import it.polimi.ingsw.messages.action.Action;
import it.polimi.ingsw.messages.action.ClientActionMessage;
import it.polimi.ingsw.messages.action.ServerActionMessage;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.game_objects.Assistant;
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
        PlayerClient firstPlayer = players.get(game.getCurrentRound().getFirstPlayerIndex());
        askForAssistant(firstPlayer);
    }

    public void handleActionMessage(ClientActionMessage message, ClientHandler ch) {
        if (message.getAction() == null) {
            sendErrorMessage(ch, "Invalid request", 3, null);
        }

        PlayerClient player = players.stream().filter(user -> ch.equals(user.getClientHandler())).toList().get(0);

        if (message.getAction().getName().equals("PLAY_ASSISTANT")) {
            handleAssistantPlayed(message.getAction(), player);
            return;
        }

        // TODO check if the message was sent by the correct player ONLY IN PAP

        switch (message.getAction().getName()) {
            case "MOVE_STUDENT_TO_DINING" -> {
            }
            case "MOVE_STUDENT_TO_ISLAND" -> {
            }
            case "MOVE_MN" -> {
            }
            case "FILL_FROM_CLOUD" -> {
            }
            case "PLAY_CHARACTER" -> {
            }
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
        } catch (InvalidActionException e) {
            sendErrorMessage(player.getClientHandler(), e.getMessage(), 1, action.getName());
        }

        if (game.getCurrentRound().getPlanningPhase().isEnded()) {
            // I am in PlayerActionPhase, send message to first user
            System.out.println("I am waiting for PAP to start");
        } else {
            currentPlayerIndex = (currentPlayerIndex + 1) % game.getPlayers().size();
            PlayerClient curPlayer = players.get(currentPlayerIndex);
            askForAssistant(curPlayer);
        }

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
        System.out.println("I sent " + actionMessage.toJson());
        player.getClientHandler().sendMessageToClient(actionMessage.toJson());
    }


}
