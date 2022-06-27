package it.polimi.ingsw.client.observers.login.username;

public interface UsernameObserver {

    /**
     * This method is triggered when the player chooses their username
     *
     * @param username the username selected by the player
     */
    void onUsernameEntered(String username);

}
