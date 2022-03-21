package it.polimi.ingsw.model;

public class Assistant {
    private final int numSteps;
    private final int value;
    private final Player player;

    public Assistant(int numSteps, int value, Player player) {
        this.numSteps = numSteps;
        this.value = value;
        this.player = player;
    }

    public int getNumSteps() {
        return numSteps;
    }

    public int getValue() {
        return value;
    }

    public Player getPlayer() {
        return player;
    }
}
