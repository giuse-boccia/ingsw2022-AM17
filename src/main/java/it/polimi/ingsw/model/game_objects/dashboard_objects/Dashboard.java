package it.polimi.ingsw.model.game_objects.dashboard_objects;

public class Dashboard {
    private final Entrance entrance;
    private final DiningRoom diningRoom;

    public Dashboard() {
        entrance = new Entrance();
        diningRoom = new DiningRoom();
    }

    public Entrance getEntrance() {
        return entrance;
    }

    public DiningRoom getDiningRoom() {
        return diningRoom;
    }
}
