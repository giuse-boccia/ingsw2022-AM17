package it.polimi.ingsw.server.game_state;

import it.polimi.ingsw.constants.Constants;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.game_objects.*;
import it.polimi.ingsw.model.game_objects.dashboard_objects.Dashboard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

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
    private final List<Color> ownedProfessors;


    private PlayerState(String name, TowerColor towerColor, Wizard wizard, int remainingTowers, int numCoins, int[] assistants, List<Student> entrance, List<Student> dining, List<Color> ownedProfessors) {
        this.name = name;
        this.towerColor = towerColor;
        this.wizard = wizard;
        this.remainingTowers = remainingTowers;
        this.numCoins = numCoins;
        this.assistants = assistants;
        this.entrance = entrance;
        this.dining = dining;
        this.ownedProfessors = ownedProfessors;
    }

    public PlayerState(Player player) {
        this(
                player.getName(),
                player.getTowerColor(),
                player.getWizard(),
                player.getRemainingTowers(),
                player.getNumCoins(),
                Arrays.stream(player.getHand()).filter(Objects::nonNull).mapToInt(Assistant::getValue).toArray(),
                player.getDashboard().getEntrance().getStudents(),
                player.getDashboard().getDiningRoom().getStudents(),
                player.getColorsOfOwnedProfessors()
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

    public List<Color> getOwnedProfessors() {
        return ownedProfessors;
    }

    /**
     * Loads the players from the given saved game state
     *
     * @param gs the saved game state
     * @return a list of players
     */
    public static List<Player> loadPlayers(SavedGameState gs) {
        List<Player> res = new ArrayList<>();
        int initialTowers = gs.getPlayers().size() % 2 == 0 ? Constants.TOWERS_IN_TWO_OR_FOUR_PLAYER_GAME : Constants.TOWERS_IN_THREE_PLAYER_GAME;

        gs.getPlayers().forEach(playerState -> res.add(playerState.loadPlayer(initialTowers)));

        return res;
    }

    /**
     * Loads a player from this PlayerState
     *
     * @param initialTowers the initial amount of towers that the player should have
     * @return the loaded Player
     */
    public Player loadPlayer(int initialTowers) {
        return new Player(
                assistants,
                new Dashboard(entrance, dining),
                name,
                numCoins,
                wizard,
                towerColor,
                initialTowers
        );
    }
}
