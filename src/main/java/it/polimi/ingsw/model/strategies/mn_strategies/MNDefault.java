package it.polimi.ingsw.model.strategies.mn_strategies;

import it.polimi.ingsw.model.game_objects.Assistant;

public class MNDefault implements MNStrategy {

    @Override
    public int getMNMaxSteps(Assistant card) {
        return card.getNumSteps();
    }

}
