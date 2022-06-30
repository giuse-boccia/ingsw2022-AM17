package it.polimi.ingsw.messages.chat;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.utils.constants.Constants;

import java.util.ArrayList;
import java.util.List;

public class ServerChatMessage extends SimpleChatMessage {

    private static ServerChatMessage serverChatMessage;
    private final List<ChatMessage> messages;

    private ServerChatMessage() {
        super(null);
        messages = new ArrayList<>();
        setStatus(Constants.STATUS_CHAT);
        setAction(Constants.ACTION_SEND_ALL_MESSAGES);
    }

    public static ServerChatMessage getInstance() {
        if (serverChatMessage == null) {
            serverChatMessage = new ServerChatMessage();
        }
        return serverChatMessage;
    }

    public static ServerChatMessage fromJson(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, new TypeToken<ServerChatMessage>() {
        }.getType());
    }

    public void addMessage(ChatMessage message) {
        messages.add(message);
    }

    public List<ChatMessage> getMessages() {
        return messages;
    }

}
