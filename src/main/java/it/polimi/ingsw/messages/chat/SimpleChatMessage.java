package it.polimi.ingsw.messages.chat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.utils.constants.Constants;

public class SimpleChatMessage extends Message {

    private final ChatMessage chatMessage;

    public SimpleChatMessage(ChatMessage chatMessage) {
        super();
        setStatus(Constants.STATUS_CHAT);
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
}
