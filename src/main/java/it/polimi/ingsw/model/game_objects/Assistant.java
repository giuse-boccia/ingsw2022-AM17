package it.polimi.ingsw.model.game_objects;

import it.polimi.ingsw.model.Player;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Assistant assistant = (Assistant) o;

        if (numSteps != assistant.numSteps) return false;
        if (value != assistant.value) return false;
        // comparing only player name to avoid infinite recursion
        return player.getName().equals(assistant.getPlayer().getName());
    }
}
