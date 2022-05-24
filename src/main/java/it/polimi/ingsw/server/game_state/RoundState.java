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
     * a {@code List} of all the assistants played during the preparation phase. Can be null if the prep phase hasn't been played
     */
    private List<Assistant> playedAssistants;
    private int firstPlayerIndex;
    private boolean isLastRound;

    public RoundState(List<Assistant> playedAssistants, int firstPlayerIndex, boolean isLastRound) {
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
                round.getPlayedAssistants(),
                round.getFirstPlayerIndex(),
                round.isLastRound()
        );
    }

    public List<Assistant> getPlayedAssistants() {
        return playedAssistants;
    }

    public void setPlayedAssistants(List<Assistant> playedAssistants) {
        this.playedAssistants = playedAssistants;
    }

    public int getFirstPlayerIndex() {
        return firstPlayerIndex;
    }

    public void setFirstPlayerIndex(int firstPlayerIndex) {
        this.firstPlayerIndex = firstPlayerIndex;
    }

    public boolean isLastRound() {
        return isLastRound;
    }

    public void setLastRound(boolean lastRound) {
        isLastRound = lastRound;
    }
}
