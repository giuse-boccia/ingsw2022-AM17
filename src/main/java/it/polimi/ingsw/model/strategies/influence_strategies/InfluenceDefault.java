package it.polimi.ingsw.model.strategies.influence_strategies;

import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.model.game_objects.gameboard_objects.Island;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.utils.Students;

public class InfluenceDefault implements InfluenceStrategy {
    /**
     * A static method which calculates the influence of the selected {@code Player} on the selected {@code Island}
     *
     * @param island the {@code Island} to be considered
     * @param player the {@code Player} to calculate the influence of
     * @return an int representing the influence of the player on the island
     */
    public static int computeDefaultInfluence(Island island, Player player) {
        int influence = 0;

        for (Color color : Color.values()) {
            if (player.getDashboard().getProfessorRoom().hasProfessorOfColor(color)) {
                influence += Students.countColor(island.getStudents(), color);
            }
        }

        if (island.getOwner() == player) {
            influence += island.getNumOfTowers();
        }

        return influence;
    }

    /**
     * Returns an int representing the influence of the player on the island
     *
     * @param island the {@code Island} to be considered
     * @param player the {@code Player} to calculate the influence of
     * @return an int representing the influence of the player on the island
     */
    @Override
    public int computeInfluence(Island island, Player player) {
        return computeDefaultInfluence(island, player);
    }
}
