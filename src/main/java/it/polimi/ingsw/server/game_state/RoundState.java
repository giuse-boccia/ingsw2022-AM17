package it.polimi.ingsw.server.game_state;

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
    private final boolean isLastRound;

    public RoundState(List<AssistantState> playedAssistants, int firstPlayerIndex, boolean isLastRound) {
        this.playedAssistants = playedAssistants;
        this.firstPlayerIndex = firstPlayerIndex;
        this.isLastRound = isLastRound;
    }

    /**
     * Creates a new RoundState starting from a given {@code Round}
     * @param round the {@code round} to create the new {@code RoundState} from
     */
    public RoundState(Round round) {
        this(
                round.getPlayedAssistants() != null ? round.getPlayedAssistants().stream()
                        .map(AssistantState::new)
                        .toList() : null,
                round.getFirstPlayerIndex(),
                round.isLastRound()
        );
    }

    public List<AssistantState> getPlayedAssistants() {
        return playedAssistants;
    }

    public int getFirstPlayerIndex() {
        return firstPlayerIndex;
    }

    public boolean isLastRound() {
        return isLastRound;
    }
}
