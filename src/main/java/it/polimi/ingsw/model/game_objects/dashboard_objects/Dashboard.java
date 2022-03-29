package it.polimi.ingsw.model.game_objects.dashboard_objects;

import it.polimi.ingsw.model.game_objects.Professor;

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

    public ProfessorRoom getProfessorRoom() {
        return professors;
    }

}
