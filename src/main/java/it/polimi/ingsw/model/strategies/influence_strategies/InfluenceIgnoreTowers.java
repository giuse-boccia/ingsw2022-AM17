package it.polimi.ingsw.model.strategies.influence_strategies;

import it.polimi.ingsw.model.game_objects.Island;
import it.polimi.ingsw.model.Player;

public class InfluenceIgnoreTowers implements InfluenceStrategy {
    /**
     * The method to calculate the influence of the selected {@code Island} when the effect of the
     * {@code Character} called "ignoreTowers" is active
     *
     * @param island the {@code Island} to be considered
     * @param player the {@code Player} to calculate the influence of
     * @return an int representing the influence of the player on the island
     */
    @Override
    public int computeInfluence(Island island, Player player) {
        int difference = 0;

        if (island.getOwner() == player) {
            difference = island.getNumOfTowers();
        }

        return InfluenceDefault.computeDefaultInfluence(island, player) - difference;
    }
}
