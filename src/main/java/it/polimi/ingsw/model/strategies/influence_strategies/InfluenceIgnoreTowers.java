package it.polimi.ingsw.model.strategies.influence_strategies;

import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.game_objects.gameboard_objects.Island;

import java.util.ArrayList;

public class InfluenceIgnoreTowers implements InfluenceStrategy {
    /**
     * The method to calculate the influence of the selected {@code Island} when the effect of the
     * {@code Character} called "ignoreTowers" is active
     *
     * @param island the {@code Island} to be considered
     * @param team   the {@code Player} to calculate the influence of
     * @return an int representing the influence of the player on the island
     */
    @Override
    public int computeInfluence(Island island, ArrayList<Player> team) {
        int difference = 0;

        if (team.size() > 0 && island.getTowerColor() == team.get(0).getTowerColor()) {
            difference = island.getNumOfTowers();
        }

        return InfluenceDefault.computeDefaultInfluence(island, team) - difference;
    }
}
