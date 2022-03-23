package it.polimi.ingsw.model;

public class InfluenceDefault implements InfluenceStrategy {

    public static int computeDefaultInfluence(Island island, Player player) {
        int influence = 0;

        for (Professor professor : player.getDashboard().getProfessors()) {
            influence += Students.countColor(island.getStudents(), professor.getColor());
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
