package it.polimi.ingsw.model;

public class Dashboard {
    private final Entrance entrance;
    private final DiningRoom diningRoom;
    private final ProfessorRoom professors;

    public Dashboard() {
        entrance = new Entrance();
        diningRoom = new DiningRoom();
        professors = new ProfessorRoom();
    }

    public Entrance getEntrance() {
        return entrance;
    }

    public DiningRoom getDiningRoom() {
        return diningRoom;
    }

    public ProfessorRoom getProfessors() {
        return professors;
    }
}
