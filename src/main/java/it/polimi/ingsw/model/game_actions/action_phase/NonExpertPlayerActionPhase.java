package it.polimi.ingsw.model.game_actions.action_phase;

import it.polimi.ingsw.model.game_objects.Assistant;
import it.polimi.ingsw.model.game_objects.gameboard_objects.GameBoard;

public class NonExpertPlayerActionPhase extends PlayerActionPhase {

    public NonExpertPlayerActionPhase(Assistant assistant, GameBoard gb) {
        super(assistant, gb);
    }

    @Override
    public void play() {

    }
}
