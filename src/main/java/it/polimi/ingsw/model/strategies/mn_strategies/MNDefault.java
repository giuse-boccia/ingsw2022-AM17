package it.polimi.ingsw.model.strategies.mn_strategies;

import it.polimi.ingsw.model.game_objects.Assistant;

public class MNDefault implements MNStrategy {
    /**
     * The default method to calculate the maximum number of steps that mother nature can do this turn when the effect
     * of the {@code Character} called "takeProfWithEqualStudents" is active
     *
     * @param card the {@code Assistant} card played this turn
     * @return the maximum number of steps that mother nature can do this turn
     */
    @Override
    public int getMNMaxSteps(Assistant card) {
        return card.getNumSteps();
    }

}
