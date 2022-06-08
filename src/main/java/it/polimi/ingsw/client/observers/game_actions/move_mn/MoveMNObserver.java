package it.polimi.ingsw.client.observers.game_actions.move_mn;

public interface MoveMNObserver {

    /**
     * This triggered when a player wants to move mother nature of the selected number of steps
     *
     * @param numSteps the number of steps to move mother nature of
     */
    void onMotherNatureMoved(int numSteps);

}
