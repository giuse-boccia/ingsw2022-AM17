package it.polimi.ingsw.client.observers.login.game_parameters;

public interface GameParametersObserver {

    /**
     * This method is triggered when the first player chooses
     * the number of player of the match and whether to play in expert mode
     *
     * @param numPlayers the number of players of the match
     * @param isExpert   true if the game has to be in expert mode
     */
    void onGameParametersSet(int numPlayers, boolean isExpert);

}
