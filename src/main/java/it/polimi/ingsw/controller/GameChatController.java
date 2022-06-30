package it.polimi.ingsw.controller;

import it.polimi.ingsw.messages.chat.ServerChatMessage;
import it.polimi.ingsw.messages.chat.SimpleChatMessage;
import it.polimi.ingsw.server.Communicable;
import it.polimi.ingsw.server.PlayerClient;

import java.util.List;

public class GameChatController {

    /**
     * Sends the given message to all the logged-in users
     *
     * @param chatMessage the {@link SimpleChatMessage} sent from the user
     * @param ch          the {@link Communicable} of the player who sent the message
     * @param users       list of all the logged-in users
     */
    public static void sendMessageToEveryone(SimpleChatMessage chatMessage, Communicable ch, List<PlayerClient> users) {
        ServerChatMessage.getInstance().addMessage(chatMessage.getChatMessage());
        for (PlayerClient user : users) {
            if (user.getCommunicable().equals(ch)) continue;
            user.getCommunicable().sendMessageToClient(chatMessage.toJson());
        }
    }

    /**
     * Sends all the messages received until now to the given user
     *
     * @param ch the {@link Communicable} of the user who requested the messages
     */
    public static void sendAllMessagesToClient(Communicable ch) {
        ServerChatMessage toSend = ServerChatMessage.getInstance();
        ch.sendMessageToClient(toSend.toJson());
    }

}
