package it.polimi.ingsw.client.observers.chat;

import it.polimi.ingsw.messages.chat.ChatMessage;

public interface ChatObserver {

    void onMessageSent(ChatMessage message);

}
