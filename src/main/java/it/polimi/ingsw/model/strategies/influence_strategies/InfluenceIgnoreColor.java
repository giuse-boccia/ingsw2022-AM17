package it.polimi.ingsw.model.strategies.influence_strategies;

import it.polimi.ingsw.model.*;
import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.model.game_objects.Island;
import it.polimi.ingsw.model.game_objects.Professor;
import it.polimi.ingsw.model.utils.Students;

public class InfluenceIgnoreColor implements InfluenceStrategy {

    private final Color colorToIgnore;

    public InfluenceIgnoreColor(Color colorToIgnore) {
        this.colorToIgnore = colorToIgnore;
    }

    @Override
    public int computeInfluence(Island island, Player player) {
        int difference = 0;

        if (player.getDashboard().getProfessorRoom().hasProfessorOfColor(colorToIgnore)) {
            difference = Students.countColor(island.getStudents(), colorToIgnore);
        }

        return InfluenceDefault.computeDefaultInfluence(island, player) - difference;
    }

}
