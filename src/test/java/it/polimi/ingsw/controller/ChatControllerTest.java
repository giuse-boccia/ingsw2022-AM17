package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.GameEndedException;
import it.polimi.ingsw.languages.Messages;
import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.messages.chat.ServerChatMessage;
import it.polimi.ingsw.messages.chat.SimpleChatMessage;
import it.polimi.ingsw.messages.login.ServerLoginMessage;
import it.polimi.ingsw.utils.constants.Constants;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ChatControllerTest {

    Controller controller = new Controller();
    ClientHandlerStub rickCh = new ClientHandlerStub();
    ClientHandlerStub giuseCh = new ClientHandlerStub();
    ClientHandlerStub clodCh = new ClientHandlerStub();

    @BeforeAll
    static void initializeMessagesResourceBundle() {
        Messages.initializeBundle("en");
    }

    /**
     * Tests what happens if a player who's not logged-in asks the messages to the server
     */
    @Test
    void testUnauthorizedMessagesRequest() throws GameEndedException {
        // Clod is not logged-in but asks messages to the server
        String askMessages = "{status:CHAT,action:GET_ALL_MESSAGES,error:0}";
        controller.handleMessage(askMessages, clodCh);

        // The server has to refuse this request
        ServerLoginMessage response = ServerLoginMessage.fromJson(clodCh.getJson());
        assertEquals(3, response.getError());
        assertEquals(Constants.STATUS_LOGIN, response.getStatus());
        assertEquals(Messages.getMessage("error_tag") + Messages.getMessage("not_logged_in"),
                response.getDisplayText());
    }

    /**
     * Tests what happens when a player asks the server for all the messages sent
     * in the game until now
     */
    @BeforeEach
    void testGetAllMessages() throws GameEndedException {
        // Rick logs in
        String rickLogin = "{status:LOGIN,username:rick,action:SET_USERNAME,error:0}";
        controller.handleMessage(rickLogin, rickCh);

        // Rick asks the server all the messages
        String askMessages = "{status:CHAT,action:GET_ALL_MESSAGES,error:0}";
        controller.handleMessage(askMessages, rickCh);

        // Server should have returned an empty list - no messages have been sent yet!
        ServerChatMessage chatMessage = ServerChatMessage.fromJson(rickCh.getJson());
        assertEquals(0, chatMessage.getMessages().size());
        assertNull(chatMessage.getChatMessage());
    }

    /**
     * Tests the situation in which a logged user sends a chat message: this should be visible
     * only to logged-in players
     */
    @Test
    void testSendMessage() throws GameEndedException {
        // Rick and Giuse logs in
        String rickLogin = "{status:LOGIN,username:rick,action:SET_USERNAME,error:0}";
        String giuseLogin = "{status:LOGIN,username:giuse,action:SET_USERNAME,error:0}";
        controller.handleMessage(rickLogin, rickCh);
        controller.handleMessage(giuseLogin, giuseCh);

        // Rick sends a chat message; this should be visible only to giuse
        String chatMessage = "{status:CHAT,action:SEND_MESSAGE,error:0,chatMessage:{message:test,sender:rick}}";
        controller.handleMessage(chatMessage, rickCh);

        // Giuse should have received the message from rick, while Clod and Rick not
        Message clodResponse = Message.fromJson(clodCh.getJson());
        assertNull(clodResponse);
        Message rickResponse = Message.fromJson(rickCh.getJson());
        assertNotEquals(Constants.STATUS_CHAT, rickResponse.getStatus());

        SimpleChatMessage messageFromRick = SimpleChatMessage.fromJson(giuseCh.getJson());
        assertEquals(Constants.STATUS_CHAT, messageFromRick.getStatus());
        assertEquals("test", messageFromRick.getChatMessage().getMessage());
        assertEquals("rick", messageFromRick.getChatMessage().getSender());

        // If now Giuse asks the server for all the messages he receives Rick's one
        String askMessages = "{status:CHAT,action:GET_ALL_MESSAGES,error:0}";
        controller.handleMessage(askMessages, giuseCh);

        ServerChatMessage allMessages = ServerChatMessage.fromJson(giuseCh.getJson());
        assertEquals(1, allMessages.getMessages().size());
        assertEquals("test", allMessages.getMessages().get(0).getMessage());
        assertEquals("rick", allMessages.getMessages().get(0).getSender());
    }

}
