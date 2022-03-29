package it.polimi.ingsw.model.game_actions;

import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.game_actions.action_phase.PlayerActionPhase;
import it.polimi.ingsw.model.game_actions.action_phase.PlayerActionPhaseFactory;
import it.polimi.ingsw.model.game_objects.Assistant;
import it.polimi.ingsw.model.game_objects.Cloud;

import java.util.ArrayList;
import java.util.Comparator;

public class Round {
    private final int firstPlayerIndex;
    private final Game game;
    private PlanningPhase planningPhase;
    private PlayerActionPhase currentPlayerActionPhase;
    private boolean isLastRound;

    public Round(int firstPlayerIndex, Game game) {
        this.firstPlayerIndex = firstPlayerIndex;
        this.game = game;
        this.isLastRound = false;
    }

    public Round(int firstPlayerIndex, Game game, boolean isLastRound) {
        this.firstPlayerIndex = firstPlayerIndex;
        this.game = game;
        this.isLastRound = isLastRound;
    }

    public int play() {

        ArrayList<Assistant> assistants;

        try {
            fillClouds();
        } catch (EmptyBagException e) {
            isLastRound = true;
        }

        planningPhase = new PlanningPhase(createPlayersArray());
        assistants = planningPhase.playAssistants();

        // Sort assistants based on the "value" attribute of each card
        assistants = (ArrayList<Assistant>) assistants.stream()
                .sorted(Comparator.comparingInt(Assistant::getValue))
                .toList();

        for (Assistant assistant : assistants) {
            currentPlayerActionPhase = PlayerActionPhaseFactory.createPlayerActionPhase(assistant, game.getGameBoard(), game.isExpert());
            currentPlayerActionPhase.play();
        }

        if (isLastRound) {
            return -1;
        }
        return game.getPlayers().indexOf(assistants.get(0).getPlayer());
    }

    private void fillClouds() throws EmptyBagException {
        for (Cloud cloud : game.getGameBoard().getClouds()) {
            cloud.fillFromBag(game.getGameBoard().getBag());
        }
    }

    private ArrayList<Player> createPlayersArray() {
        ArrayList<Player> playersInOrder = new ArrayList<>();
        int numPlayers = game.getPlayers().size();
        for (int i = 0; i < numPlayers; i++) {
            playersInOrder.add(game.getPlayers().get((i + firstPlayerIndex) % numPlayers));
        }
        return playersInOrder;
    }

    public PlanningPhase getPlanningPhase() {
        return planningPhase;
    }

    public PlayerActionPhase getCurrentPlayerActionPhase() {
        return currentPlayerActionPhase;
    }

    public int getFirstPlayerIndex() {
        return firstPlayerIndex;
    }
}
