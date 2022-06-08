package it.polimi.ingsw.model.game_objects.dashboard_objects;

import it.polimi.ingsw.model.game_objects.Student;

import java.util.List;

public class Dashboard {
    private final Entrance entrance;
    private final DiningRoom diningRoom;

    public Dashboard() {
        this.entrance = new Entrance();
        this.diningRoom = new DiningRoom();
    }

    public Dashboard(List<Student> entranceStudents, List<Student> diningStudents) {
        this.entrance = new Entrance(entranceStudents);
        this.diningRoom = new DiningRoom(entranceStudents);
    }

    public Entrance getEntrance() {
        return entrance;
    }

    public DiningRoom getDiningRoom() {
        return diningRoom;
    }
}
