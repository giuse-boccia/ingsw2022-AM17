package it.polimi.ingsw.server.game_state;


import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.game_objects.Assistant;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

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

    /**
     * Loads a list of assistants from a list of assistant states
     *
     * @param savedAssistants a list of saved assistants
     * @param players         a list of real players (model objects)
     * @return a list of assistants (model objects) ordered by value
     */
    public static List<Assistant> loadAssistants(List<AssistantState> savedAssistants, ArrayList<Player> players) {
        return savedAssistants.stream()
                .map(sa -> sa.loadAssistant(players))
                .sorted(Comparator.comparingInt(Assistant::getValue))
                .toList();
    }

    /**
     * Loads an assistant from this assistant state
     *
     * @param players a list of players containing the player who owns this assistant
     * @return the loaded assistant
     * @throws java.util.NoSuchElementException if the given list of players doesn't contain the player who owns the loaded assistant
     */
    private Assistant loadAssistant(ArrayList<Player> players) {
        Player myPlayer = players.stream()
                .filter(p -> p.getName().equals(this.playerName))
                .findFirst()
                .orElseThrow();
        return new Assistant(this.numSteps, this.value, myPlayer);
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
