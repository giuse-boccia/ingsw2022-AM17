package it.polimi.ingsw.model;

import java.util.ArrayList;

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

    public ArrayList<Professor> getProfessors() {
        return professors.getProfessors();
    }
}
