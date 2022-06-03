package it.polimi.ingsw.client.observers.login.game_parameters;

public interface GameParametersObserver {

    void onGameParametersSet(int numPlayers, boolean isExpert);

}
