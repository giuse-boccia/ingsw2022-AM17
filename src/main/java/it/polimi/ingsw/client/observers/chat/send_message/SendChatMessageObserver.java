package it.polimi.ingsw.client.observers.chat.send_message;

import it.polimi.ingsw.client.MessageHandler;
import it.polimi.ingsw.messages.chat.ChatMessage;
import it.polimi.ingsw.messages.chat.SimpleChatMessage;

public class SendChatMessageObserver implements ChatMessageObserver {

    private final MessageHandler mh;

    public SendChatMessageObserver(MessageHandler mh) {
        this.mh = mh;
        mh.attachChatMessageObserver(this);
    }

    @Override
    public void onMessageSent(ChatMessage message) {
        SimpleChatMessage chatMessage = new SimpleChatMessage(message);
        mh.getNetworkClient().sendMessageToServer(chatMessage.toJson());
    }
}
