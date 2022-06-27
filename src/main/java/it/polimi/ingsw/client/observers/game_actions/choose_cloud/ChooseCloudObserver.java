package it.polimi.ingsw.client.observers.game_actions.choose_cloud;

public interface ChooseCloudObserver {

    /**
     * This method is triggered when a player chooses a cloud
     *
     * @param index the index of the chosen cloud
     */
    void onCloudChosen(int index);

}
