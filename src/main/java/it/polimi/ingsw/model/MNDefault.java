package it.polimi.ingsw.model;

public class MNDefault implements MNStrategy {

    @Override
    public int getMNMaxSteps(Assistant card) {
        return card.getNumSteps();
    }

}
