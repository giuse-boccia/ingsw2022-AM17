package it.polimi.ingsw.client.observers.choices.action;

public interface ActionChoiceObserver {

    /**
     * This method is triggered when a player - in Cli - chooses the given action from
     * the available actions' list
     *
     * @param action {@code String} with the name of the selected action
     */
    void onActionChosen(String action);

}
