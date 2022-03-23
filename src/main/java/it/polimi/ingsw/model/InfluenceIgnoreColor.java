package it.polimi.ingsw.model;

public class InfluenceIgnoreColor implements InfluenceStrategy {

    private final Color colorToIgnore;

    public InfluenceIgnoreColor(Color colorToIgnore) {
        this.colorToIgnore = colorToIgnore;
    }

    @Override
    public int computeInfluence(Island island, Player player) {
        int difference = 0;

        for (Professor professor : player.getDashboard().getProfessors()) {
            if (professor.getColor() == colorToIgnore) {
                difference = Students.countColor(island.getStudents(), professor.getColor());
            }
        }

        return InfluenceDefault.computeDefaultInfluence(island, player) - difference;
    }

}
