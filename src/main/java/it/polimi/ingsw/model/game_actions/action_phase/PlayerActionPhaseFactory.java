package it.polimi.ingsw.model.game_actions.action_phase;

import it.polimi.ingsw.model.game_objects.Assistant;
import it.polimi.ingsw.model.game_objects.GameBoard;

public class PlayerActionPhaseFactory {

    public static PlayerActionPhase createPlayerActionPhase(Assistant assistant, GameBoard gb, boolean isExpert) {
        if (isExpert)
            return new ExpertPlayerActionPhase(assistant, gb);
        return new NonExpertPlayerActionPhase(assistant, gb);
    }

}
