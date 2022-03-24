package it.polimi.ingsw.model;

public class MNBonus implements MNStrategy {

    private final int bonus;

    public MNBonus() {
        this.bonus = 2;
    }

    public MNBonus(int bonus) {
        this.bonus = bonus;
    }

    @Override
    public int getMNMaxSteps(Assistant card) {
        return card.getNumSteps() + bonus;
    }
}
