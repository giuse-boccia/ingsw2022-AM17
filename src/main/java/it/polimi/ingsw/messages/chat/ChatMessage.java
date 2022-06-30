package it.polimi.ingsw.messages.chat;

public class ChatMessage {
    private final String message;
    private final String sender;

    public ChatMessage(String message, String sender) {
        this.message = message;
        this.sender = sender;
    }

    public String getMessage() {
        return message;
    }

    public String getSender() {
        return sender;
    }
}
