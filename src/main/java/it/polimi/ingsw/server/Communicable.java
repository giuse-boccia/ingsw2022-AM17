package it.polimi.ingsw.server;

public interface Communicable {

    /**
     * Sends a response to the client
     *
     * @param json the response to send to the client
     */
    void sendMessageToClient(String json);

}
