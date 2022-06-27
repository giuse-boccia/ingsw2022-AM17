package it.polimi.ingsw.client.observers.game_actions.play_assistant;

public interface PlayAssistantObserver {

    /**
     * This method is triggered when a player plays an assistant of the selected values
     *
     * @param value the value of the assistant to be played
     */
    void onAssistantPlayed(int value);

}
