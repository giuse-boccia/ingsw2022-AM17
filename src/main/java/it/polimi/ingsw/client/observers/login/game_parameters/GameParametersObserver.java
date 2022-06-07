package it.polimi.ingsw.client.observers.login.game_parameters;

public interface GameParametersObserver {

    /**
     * This method is called to trigger an update of the model when the first player - in Cli or Gui - chooses
     * the number of player of the match and whether to play in expert mode
     *
     * @param numPlayers integer representing the number of players of the match
     * @param isExpert   boolean set to true if the created match has to be in expert mode and set to false otherwise
     */
    void onGameParametersSet(int numPlayers, boolean isExpert);

}
