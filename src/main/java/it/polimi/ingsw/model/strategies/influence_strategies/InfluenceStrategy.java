package it.polimi.ingsw.model.strategies.influence_strategies;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.game_objects.gameboard_objects.Island;

import java.util.ArrayList;

public interface InfluenceStrategy {

    /**
     * Returns the influence value of the selected team on an {@code Island}
     *
     * @param island the {@code Island} to be considered
     * @param team   an {@code ArrayList} of players who belong to the team considered
     * @return an int representing the influence of the team on the island
     */
    int computeInfluence(Island island, ArrayList<Player> team);
}
