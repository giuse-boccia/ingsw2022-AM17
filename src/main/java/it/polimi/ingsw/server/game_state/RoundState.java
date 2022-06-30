package it.polimi.ingsw.server.game_state;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.game_actions.PlanningPhase;
import it.polimi.ingsw.model.game_actions.PlayerActionPhase;
import it.polimi.ingsw.model.game_actions.Round;
import it.polimi.ingsw.model.game_objects.Assistant;

import java.util.List;

/**
 * Class used to encapsulate the state of a round after certain "checkpoints".
 * Can be used to save the state of the round after the preparation phase and
 * after every action phase, but NOT in the middle of an action phase
 */
public class RoundState {
    /**
     * a {@code List} of all the assistants played during the preparation phase.
     * Is null if the prep phase hasn't been played yet
     */
    private final List<AssistantState> playedAssistants;
    private final int firstPlayerIndex;
    private final int currentAssistantIndex;
    private final boolean isLastRound;

    public RoundState(List<AssistantState> playedAssistants, int firstPlayerIndex, int currentAssistantIndex, boolean isLastRound) {
        this.playedAssistants = playedAssistants;
        this.firstPlayerIndex = firstPlayerIndex;
        this.currentAssistantIndex = currentAssistantIndex;
        this.isLastRound = isLastRound;
    }

    /**
     * Creates a new RoundState starting from a given {@code Round}
     *
     * @param round the {@code round} to create the new {@code RoundState} from
     */
    public RoundState(Round round) {
        this(
                round.getPlayedAssistants() != null ? round.getPlayedAssistants().stream()
                        .map(AssistantState::new)
                        .toList() : null,
                round.getFirstPlayerIndex(),
                round.getCurrentAssistantIndex(),
                round.isLastRound()
        );
    }

    /**
     * Loads a round from a round state
     *
     * @param savedGame the saved game containing the round state
     * @param game      the game model
     * @return the loaded round
     */
    public static Round loadRound(SavedGameState savedGame, Game game) {
        RoundState savedRound = savedGame.getRoundState();
        Round res = new Round(savedRound.getFirstPlayerIndex(), game, savedRound.isLastRound());

        if (savedRound.currentAssistantIndex == -1) {
            // we are at the beginning of the planning phase
            res.setPlanningPhase(new PlanningPhase(res.createPlayersArray(), res));
        } else {
            // we are at the beginning of a player action phase
            res.setCurrentAssistantIndex(savedRound.getCurrentAssistantIndex());
            List<Assistant> playedAssistantsInOrder = AssistantState.loadAssistants(savedRound.getPlayedAssistants(), game.getPlayers());
            res.setPlayedAssistants(playedAssistantsInOrder);
            Assistant currentAssistant = playedAssistantsInOrder.get(savedRound.getCurrentAssistantIndex());
            res.setCurrentPlayerActionPhase(new PlayerActionPhase(currentAssistant, game.getGameBoard()));
        }

        return res;
    }

    public List<AssistantState> getPlayedAssistants() {
        return playedAssistants;
    }

    public int getFirstPlayerIndex() {
        return firstPlayerIndex;
    }

    public int getCurrentAssistantIndex() {
        return currentAssistantIndex;
    }

    public boolean isLastRound() {
        return isLastRound;
    }
}
