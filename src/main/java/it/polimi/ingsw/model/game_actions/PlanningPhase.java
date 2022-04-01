package it.polimi.ingsw.model.game_actions;

import it.polimi.ingsw.exceptions.AlreadyPlayedAssistantException;
import it.polimi.ingsw.exceptions.InvalidActionException;
import it.polimi.ingsw.exceptions.SameAssistantPlayedException;
import it.polimi.ingsw.model.game_objects.Assistant;
import it.polimi.ingsw.model.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
     * @throws InvalidActionException          if the player who is trying to play the {@code Assistant} is not the one actually
     *                                         playing this turn
     * @throws AlreadyPlayedAssistantException if the player who is trying to play the {@code Assistant} has already
     *                                         played it
     * @throws SameAssistantPlayedException    if the {@code Assistant} who is trying to be played has already been played
     *                                         by someone else this turn
     */
    public void addAssistant(Assistant assistant) throws InvalidActionException, AlreadyPlayedAssistantException, SameAssistantPlayedException {

        if (assistant == null) {
            throw new AlreadyPlayedAssistantException("You have already played this assistant");
        }

        Player player = assistant.getPlayer();

        if (isEnded()) {
            throw new InvalidActionException("Planning phase is ended");
        }

        if (player != playersInOrder.get(playedAssistants.size())) {
            throw new InvalidActionException("You can't play an assistant now");
        }

        if (!isAssistantPlayable(assistant)) {
            throw new SameAssistantPlayedException("Someone already played the same assistant this turn");
        }

        playedAssistants.add(assistant);
        player.playAssistant(assistant);

        // Ends planning phase if necessary
        if (playedAssistants.size() == playersInOrder.size()) {
            round.endPlanningPhase(playedAssistants);
        }
    }

    /**
     * Checks if the selected {@code Assistant} is playable
     *
     * @param assistant the {@code Assistant} to check
     * @return true if the {@code Assistan} has a different value from all the other values of the other assistans
     * played or if the current {@code Player} has no other playable assistant
     */
    private boolean isAssistantPlayable(Assistant assistant) {
        List<Integer> playedValues =
                playedAssistants.stream().map(Assistant::getValue).toList();
        if (playedValues.contains(assistant.getValue())) {
            int numberOfPlayableAssistants =
                    Arrays.stream(assistant.getPlayer().getHand()).filter(Objects::nonNull).map(Assistant::getValue)
                            .filter(value -> !playedValues.contains(value)).toList().size();
            return numberOfPlayableAssistants <= 0;
        }
        return true;
    }

    public ArrayList<Assistant> getPlayedAssistants() {
        return new ArrayList<>(playedAssistants);
    }

    public boolean isEnded() {
        return playedAssistants.size() == playersInOrder.size();
    }

}
