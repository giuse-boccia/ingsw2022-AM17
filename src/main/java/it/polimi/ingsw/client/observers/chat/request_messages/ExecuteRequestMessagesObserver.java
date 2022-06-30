package it.polimi.ingsw.client.observers.chat.request_messages;

import it.polimi.ingsw.client.MessageHandler;
import it.polimi.ingsw.messages.chat.SimpleChatMessage;
import it.polimi.ingsw.utils.constants.Constants;

public class ExecuteRequestMessagesObserver implements RequestMessagesObserver {

    private final MessageHandler mh;

    public ExecuteRequestMessagesObserver(MessageHandler mh) {
        this.mh = mh;
        mh.attachRequestMessagesObserver(this);
    }

    @Override
    public void requestAllMessages() {
        SimpleChatMessage chatMessage = new SimpleChatMessage(null);
        chatMessage.setAction(Constants.ACTION_GET_ALL_MESSAGES);
        mh.getNetworkClient().sendMessageToServer(chatMessage.toJson());
    }
}
