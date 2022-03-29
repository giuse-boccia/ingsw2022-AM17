package it.polimi.ingsw.model.game_actions;

import it.polimi.ingsw.model.game_objects.Assistant;
import it.polimi.ingsw.model.Player;

import java.util.ArrayList;

public class PlanningPhase {

    private final ArrayList<Player> playersinOrder;
    private final ArrayList<Assistant> playedAssistant;

    public PlanningPhase(ArrayList<Player> playersinOrder) {
        this.playersinOrder = playersinOrder;
        this.playedAssistant = new ArrayList<>();
    }

    public void addAssistant(Assistant assistant) {
        playedAssistant.add(assistant);
    }

    public ArrayList<Assistant> playAssistants() {

        for (Player player : playersinOrder) {
            // TODO call player.getController().chooseAssistant(), which calls addAssistant()
            addAssistant(new Assistant(1, 1, player)); //
        }

        return playedAssistant;
    }


}
