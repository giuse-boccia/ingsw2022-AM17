package it.polimi.ingsw.model;

public class ProfessorDefault implements ProfessorStrategy {

    @Override
    public boolean canStealProfessor(Color color, DiningRoom myDiningRoom, DiningRoom otherDiningRoom) {
        int myStudentsNum = myDiningRoom.getNumberOfStudentsOfColor(color);
        int otherStudentsNum = otherDiningRoom.getNumberOfStudentsOfColor(color);

        return myStudentsNum > otherStudentsNum;
    }
}
