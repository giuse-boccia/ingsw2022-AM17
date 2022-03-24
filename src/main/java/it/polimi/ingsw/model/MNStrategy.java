package it.polimi.ingsw.model;

public interface MNStrategy {
    /**
     * Returns the maximum number of steps that mother nature can do this turn
     *
     * @param card the {@code Assistant} card played this turn
     * @return the maximum number of steps that mother nature can do this turn
     */
    int getMNMaxSteps(Assistant card);
}
