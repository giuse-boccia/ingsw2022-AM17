package it.polimi.ingsw.model.game_state;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.game_objects.Assistant;
import it.polimi.ingsw.model.game_objects.Student;
import it.polimi.ingsw.model.game_objects.TowerColor;
import it.polimi.ingsw.model.game_objects.Wizard;

import java.util.Arrays;
import java.util.List;

/**
 * Immutable class to represent the current state of a player
 */
public class PlayerState {
    private final String name;
    private final TowerColor towerColor;      // this could be a plain String if Gson gets angry with enums...
    private final Wizard wizard;
    private final int remainingTowers;
    private final int numCoins;
    private final int[] assistants;
    private final List<Student> entrance;
    private final List<Student> dining;


    private PlayerState(String name, TowerColor towerColor, Wizard wizard, int remainingTowers, int numCoins, int[] assistants, List<Student> entrance, List<Student> dining) {
        this.name = name;
        this.towerColor = towerColor;
        this.wizard = wizard;
        this.remainingTowers = remainingTowers;
        this.numCoins = numCoins;
        this.assistants = assistants;
        this.entrance = entrance;
        this.dining = dining;
    }

    public PlayerState(Player player) {
        this(
                player.getName(),
                player.getTowerColor(),
                player.getWizard(),
                player.getNumberOfTowers(),
                player.getNumCoins(),
                Arrays.stream(player.getHand()).mapToInt(Assistant::getValue).toArray(),
                player.getDashboard().getEntrance().getStudents(),
                player.getDashboard().getDiningRoom().getStudents()
        );
    }

    public String getName() {
        return name;
    }

    public TowerColor getTowerColor() {
        return towerColor;
    }

    public Wizard getWizard() {
        return wizard;
    }

    public int getRemainingTowers() {
        return remainingTowers;
    }

    public int getNumCoins() {
        return numCoins;
    }

    public int[] getAssistants() {
        return assistants;
    }

    public List<Student> getEntrance() {
        return entrance;
    }

    public List<Student> getDining() {
        return dining;
    }
}
