package it.polimi.ingsw.model.strategies.influence_strategies;

import it.polimi.ingsw.model.game_objects.Island;
import it.polimi.ingsw.model.Player;

public interface InfluenceStrategy {

    /**
     * Returns the influence value of a certain {@code Player} on an {@code Island}
     *
     * @param island the {@code Island} to be considered
     * @param player the {@code Player} to calculate the influence of
     * @return an int representing the influence of the player on the island
     */
    int computeInfluence(Island island, Player player);
}
