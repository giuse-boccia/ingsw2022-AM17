package it.polimi.ingsw.model.game_actions;

import it.polimi.ingsw.exceptions.InvalidActionException;
import it.polimi.ingsw.model.game_objects.Assistant;
import it.polimi.ingsw.model.Player;

import java.util.ArrayList;

public class PlanningPhase {

    private final ArrayList<Player> playersInOrder;
    private final ArrayList<Assistant> playedAssistants;
    private final Round round;

    public PlanningPhase(ArrayList<Player> playersInOrder, Round round) {
        this.playersInOrder = playersInOrder;
        this.playedAssistants = new ArrayList<>();
        this.round = round;
    }

    /**
     * Adds an {@code Assistant} to the playedAssistant {@code ArrayList}
     *
     * @param assistant the {@code Assistant} to add
     * @throws InvalidActionException if the player who is trying to play the {@code Assistant} is not the one actually
     *                                playing this turn
     */
    public void addAssistant(Assistant assistant) throws InvalidActionException {
        Player player = assistant.getPlayer();

        if (player != playersInOrder.get(playedAssistants.size())) {
            throw new InvalidActionException("You can't play an assistant now");
        }

        playedAssistants.add(assistant);

        // Ends planning phase if necessary
        if (playedAssistants.size() == playersInOrder.size()) {
            round.endPlanningPhase(playedAssistants);
        }
    }

    public ArrayList<Assistant> getPlayedAssistants() {
        return new ArrayList<>(playedAssistants);
    }


}
