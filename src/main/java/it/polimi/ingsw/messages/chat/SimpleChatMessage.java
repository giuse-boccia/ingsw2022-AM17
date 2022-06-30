package it.polimi.ingsw.messages.chat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.utils.constants.Constants;

public class SimpleChatMessage extends Message {

    private final ChatMessage chatMessage;
    private String action;

    public SimpleChatMessage(ChatMessage chatMessage) {
        super();
        setStatus(Constants.STATUS_CHAT);
        action = Constants.ACTION_SEND_MESSAGE;
        this.chatMessage = chatMessage;
    }

    public static SimpleChatMessage fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, new TypeToken<SimpleChatMessage>() {
        }.getType());
    }

    public ChatMessage getChatMessage() {
        return chatMessage;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }
}
