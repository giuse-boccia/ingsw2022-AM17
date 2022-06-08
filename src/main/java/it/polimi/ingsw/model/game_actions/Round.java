package it.polimi.ingsw.model.game_actions;

import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.game_objects.Assistant;
import it.polimi.ingsw.model.game_objects.gameboard_objects.Cloud;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class Round {
    private final int firstPlayerIndex;
    private final Game game;
    private int currentAssistantIndex = -1;
    private PlanningPhase planningPhase;
    private List<Assistant> playedAssistants;
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

    public int getCurrentAssistantIndex() {
        return currentAssistantIndex;
    }

    public void setCurrentAssistantIndex(int currentAssistantIndex) {
        this.currentAssistantIndex = currentAssistantIndex;
    }

    /**
     * Starts the {@code PlanningPhase} of the {@code Round}
     */
    public void startPlanningPhase() {
        try {
            fillClouds();
        } catch (EmptyBagException e) {
            setLastRound();
        }

        planningPhase = new PlanningPhase(createPlayersArray(), this);
    }

    /**
     * Sorts the playedAssistants by their value and starts the first {@code PlayerActionPhase}
     *
     * @param playedAssistants the assistants played in the {@code PlanningPhase}
     */
    public void endPlanningPhase(ArrayList<Assistant> playedAssistants) {
        this.playedAssistants = playedAssistants;

        // Sort assistants based on the "value" attribute of each card
        this.playedAssistants = new ArrayList<>(playedAssistants.stream()
                .sorted(Comparator.comparingInt(Assistant::getValue))
                .toList());

        nextPlayerActionPhase();
    }

    /**
     * If every {@code Player} has completed their {@code PlayerActionPhase} creates the next {@code Round}, else
     * creates the next {@code PlayerActionPhase}
     */
    public void nextPlayerActionPhase() {

        currentAssistantIndex++;

        if (currentAssistantIndex == game.getPlayers().size()) {
            Player nextFirstPlayer = playedAssistants.get(0).getPlayer();
            game.nextRound(game.getPlayers().indexOf(nextFirstPlayer));
        } else {
            Player currentPlayer = playedAssistants.get(currentAssistantIndex).getPlayer();
            currentPlayerActionPhase = new PlayerActionPhase(playedAssistants.get(currentAssistantIndex), game.getGameBoard());
        }
    }

    /**
     * Checks if it's the last {@code Round} of the {@code Game}
     *
     * @return true if it's the last {@code Round}
     */
    public boolean isLastRound() {
        return isLastRound;
    }

    public void setLastRound() {
        isLastRound = true;
    }

    /**
     * Fills the clouds from the {@code Bag}
     *
     * @throws EmptyBagException if the {@code Bag} is empty
     */
    private void fillClouds() throws EmptyBagException {
        for (Cloud cloud : game.getGameBoard().getClouds()) {
            cloud.fillFromBag(game.getGameBoard().getBag());
        }
    }

    /**
     * Sorts the players in the order they should play the {@code PlanningPhase} in
     *
     * @return the {@code ArrayList} containing the players in the order they should play the {@code PlanningPhase} in
     */
    public ArrayList<Player> createPlayersArray() {
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

    public void setPlanningPhase(PlanningPhase planningPhase) {
        this.planningPhase = planningPhase;
    }

    public PlayerActionPhase getCurrentPlayerActionPhase() {
        return currentPlayerActionPhase;
    }

    public void setCurrentPlayerActionPhase(PlayerActionPhase currentPlayerActionPhase) {
        this.currentPlayerActionPhase = currentPlayerActionPhase;
    }

    public int getFirstPlayerIndex() {
        return firstPlayerIndex;
    }

    public List<Assistant> getPlayedAssistants() {
        if (playedAssistants == null) {
            return null;
        }
        return new ArrayList<>(playedAssistants);
    }

    public void setPlayedAssistants(List<Assistant> playedAssistants) {
        this.playedAssistants = playedAssistants;
    }
}
