package it.polimi.ingsw.client.observers.chat.request_messages;

public interface RequestMessagesObserver {

    /**
     * This function is called in order to retrieve all sent messages from server
     */
    void requestAllMessages();

}
