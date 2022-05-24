package it.polimi.ingsw.server.game_state;


import it.polimi.ingsw.model.game_objects.Assistant;

public class AssistantState {
    private final int numSteps;
    private final int value;
    private final String playerName;

    public AssistantState(int numSteps, int value, String playerName) {
        this.numSteps = numSteps;
        this.value = value;
        this.playerName = playerName;
    }

    public AssistantState(Assistant assistant) {
        this(
                assistant.getNumSteps(),
                assistant.getValue(),
                assistant.getPlayer().getName()
        );
    }

    public int getNumSteps() {
        return numSteps;
    }

    public int getValue() {
        return value;
    }

    public String getPlayerName() {
        return playerName;
    }
}
