package it.polimi.ingsw.model.strategies.mn_strategies;

import it.polimi.ingsw.model.game_objects.Assistant;

public interface MNStrategy {
    /**
     * Returns the maximum number of steps that mother nature can do this turn
     *
     * @param card the {@code Assistant} card played this turn
     * @return the maximum number of steps that mother nature can do this turn
     */
    int getMNMaxSteps(Assistant card);
}
