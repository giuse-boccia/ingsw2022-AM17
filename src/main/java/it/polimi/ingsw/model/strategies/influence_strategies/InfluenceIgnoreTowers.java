package it.polimi.ingsw.model.strategies.influence_strategies;

import it.polimi.ingsw.model.game_objects.Island;
import it.polimi.ingsw.model.Player;

public class InfluenceIgnoreTowers implements InfluenceStrategy {

    @Override
    public int computeInfluence(Island island, Player player) {
        int difference = 0;

        if (island.getOwner() == player) {
            difference = island.getNumOfTowers();
        }

        return InfluenceDefault.computeDefaultInfluence(island, player) - difference;
    }
}
