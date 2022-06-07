package it.polimi.ingsw.client.observers.game_actions.move_mn;

public interface MoveMNObserver {

    /**
     * This method is called to trigger an update of the model when a player - in Cli or Gui - wants to
     * move mother nature of the given number of steps
     *
     * @param numSteps the number of steps to move mother nature of
     */
    void onMotherNatureMoved(int numSteps);

}
