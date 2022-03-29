package it.polimi.ingsw.model.game_actions.action_phase;

import it.polimi.ingsw.model.game_objects.Assistant;
import it.polimi.ingsw.model.game_objects.GameBoard;

public class PlayerActionPhaseFactory {
    /**
     * Creates the {@code PlayerActionPhase} accordingly to the isExpert {@code Boolean}
     *
     * @param assistant The {@code Assistant} played this turn
     * @param gb        The {@code GameBoard} relative to the {@code Game}
     * @param isExpert  The {@code Boolean} which says if the mode of the {@code Game} is or is not Expert
     * @return the correct {@code PlayerActionPhase} accordingly to the isExpert {@code Boolean}
     */
    public static PlayerActionPhase createPlayerActionPhase(Assistant assistant, GameBoard gb, boolean isExpert) {
        if (isExpert)
            return new ExpertPlayerActionPhase(assistant, gb);
        return new NonExpertPlayerActionPhase(assistant, gb);
    }

}
