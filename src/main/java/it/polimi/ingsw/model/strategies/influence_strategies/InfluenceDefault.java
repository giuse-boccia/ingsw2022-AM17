package it.polimi.ingsw.model.strategies.influence_strategies;

import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.model.game_objects.Island;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.game_objects.Professor;
import it.polimi.ingsw.model.utils.Students;

public class InfluenceDefault implements InfluenceStrategy {

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

    @Override
    public int computeInfluence(Island island, Player player) {
        return computeDefaultInfluence(island, player);
    }
}
