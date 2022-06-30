package it.polimi.ingsw.client.observers.chat.send_message;

import it.polimi.ingsw.messages.chat.ChatMessage;

public interface ChatMessageObserver {

    /**
     * This method is triggered when a player clicks on Send button from chat screen
     *
     * @param message the sent {@link ChatMessage}
     */
    void onMessageSent(ChatMessage message);

}
