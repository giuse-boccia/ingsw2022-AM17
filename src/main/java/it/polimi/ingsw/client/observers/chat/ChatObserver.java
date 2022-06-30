package it.polimi.ingsw.client.observers.chat;

import it.polimi.ingsw.messages.chat.ChatMessage;

public interface ChatObserver {

    /**
     * This method is triggered when a player clicks on Send button from chat screen
     *
     * @param message the sent {@link ChatMessage}
     */
    void onMessageSent(ChatMessage message);

}
