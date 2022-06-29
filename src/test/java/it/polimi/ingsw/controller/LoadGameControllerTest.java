package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.GameEndedException;
import it.polimi.ingsw.languages.MessageResourceBundle;
import it.polimi.ingsw.messages.login.ServerLoginMessage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LoadGameControllerTest {

    Controller controller = new Controller();
    ClientHandlerStub rickCh = new ClientHandlerStub();
    ClientHandlerStub clodCh = new ClientHandlerStub();

    @BeforeAll
    static void initializeMessagesResourceBundle() {
        MessageResourceBundle.initializeBundle("en");
    }

    /**
     * Tests what happens when a player who is not logged in tries to load a game
     */
    @Test
    public void testNotLoggedInLoader() throws GameEndedException {
        // Clod logs in
        String clodLogin = "{status:LOGIN,username:clod,action:SET_USERNAME,error:0}";
        controller.handleMessage(clodLogin, clodCh);
        // Rick hasn't logged in but wants to load an existing game
        String loadGameString = "{status:LOGIN,action:LOAD_GAME,error:0}";
        controller.handleMessage(loadGameString, rickCh);

        // Controller sends an error message to Rick
        ServerLoginMessage rickResponse = ServerLoginMessage.fromJson(rickCh.getJson());
        assertEquals(3, rickResponse.getError());
        assertEquals(MessageResourceBundle.getMessage("error_tag") + MessageResourceBundle.getMessage("invalid_player_creating_game"),
                rickResponse.getDisplayText());
    }

    /**
     * Tests what happens when a user whose username is not present among the ones of the saved game
     */
    @Test
    void testInvalidUsernameForLoader() throws GameEndedException {
        // NewUser logs in
        String newUserLogin = "{status:LOGIN,username:NewUser,action:SET_USERNAME,error:0}";
        controller.handleMessage(newUserLogin, rickCh);
        // NewUser tries to load a saved game
        String loadGameString = "{status:LOGIN,action:LOAD_GAME,error:0}";
        controller.handleMessage(loadGameString, rickCh);

        // Server has to refuse this request
        ServerLoginMessage rickResponse = ServerLoginMessage.fromJson(rickCh.getJson());
        assertEquals(5, rickResponse.getError());
        assertEquals(MessageResourceBundle.getMessage("error_tag") + MessageResourceBundle.getMessage("username_not_in_loaded_game"),
                rickResponse.getDisplayText());
        assertNull(controller.getGame());
    }
}
