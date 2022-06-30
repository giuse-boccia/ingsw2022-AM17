package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.GameEndedException;
import it.polimi.ingsw.languages.Messages;
import it.polimi.ingsw.messages.login.ServerLoginMessage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class LoadGameControllerTest {

    Controller controller = new Controller();
    ClientHandlerStub rickCh = new ClientHandlerStub();
    ClientHandlerStub clodCh = new ClientHandlerStub();
    ClientHandlerStub giuseCh = new ClientHandlerStub();

    @BeforeAll
    static void initializeMessagesResourceBundle() {
        Messages.initializeBundle("en");
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
        assertEquals(Messages.getMessage("error_tag") + Messages.getMessage("invalid_player_creating_game"),
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
        assertEquals(Messages.getMessage("error_tag") + Messages.getMessage("username_not_in_loaded_game"),
                rickResponse.getDisplayText());
        assertNull(controller.getGame());
    }

    /**
     * Tests what happens when one of the players of the saved game attempts to load it
     */
    @Test
    void testValidLoadGame() throws GameEndedException {
        // Rick logs in: he now has the right of loading a saved game
        String rickLogin = "{status:LOGIN,username:rick,action:SET_USERNAME,error:0}";
        controller.handleMessage(rickLogin, rickCh);

        // Rick attempts to load a saved game
        String loadSavedGame = "{status:LOGIN,action:LOAD_GAME,error:0}";
        controller.handleMessage(loadSavedGame, rickCh);

        // Server should assert that a loaded game has been restored
        ServerLoginMessage gameLoadedResponse = ServerLoginMessage.fromJson(rickCh.getJson());
        assertEquals(Messages.getMessage("game_loaded"), gameLoadedResponse.getDisplayText());
        assertEquals(0, gameLoadedResponse.getError());

        // Clod attempts to log in, but he wasn't part of the saved game: he has to be kicked
        String clodLogin = "{status:LOGIN,username:clod,action:SET_USERNAME,error:0}";
        controller.handleMessage(clodLogin, clodCh);

        ServerLoginMessage clodResponse = ServerLoginMessage.fromJson(clodCh.getJson());
        assertEquals(5, clodResponse.getError());
        assertEquals(Messages.getMessage("error_tag") + Messages.getMessage("player_not_in_loaded_game"),
                clodResponse.getDisplayText());

        // Giuse, which was part of the saved game, logs in
        String giuseLogin = "{status:LOGIN,username:giuse,action:SET_USERNAME,error:0}";
        controller.handleMessage(giuseLogin, giuseCh);
        assertNotNull(controller.getGame());
        assertEquals(controller.getLoggedUsers().get(0).getPlayer(), controller.getGame().getPlayers().get(0));
        assertEquals(controller.getLoggedUsers().get(1).getPlayer(), controller.getGame().getPlayers().get(1));
    }

    /**
     * Tests what happens when a saved game is reloaded but other players were waiting in the lobby
     */
    @Test
    void testTooManyPlayersInLoadedGame() throws GameEndedException {
        // Rick logs in: he now has the right of loading a saved game
        String rickLogin = "{status:LOGIN,username:rick,action:SET_USERNAME,error:0}";
        controller.handleMessage(rickLogin, rickCh);
        // Meanwhile, Clod and Giuse enter the lobby: the second one is present in the saved game, the first one no
        String clodLogin = "{status:LOGIN,username:clod,action:SET_USERNAME,error:0}";
        String giuseLogin = "{status:LOGIN,username:giuse,action:SET_USERNAME,error:0}";
        controller.handleMessage(clodLogin, clodCh);
        controller.handleMessage(giuseLogin, giuseCh);

        // Rick loads a saved game
        String loadSavedGame = "{status:LOGIN,action:LOAD_GAME,error:0}";
        controller.handleMessage(loadSavedGame, rickCh);

        // Clod should have been kicked
        ServerLoginMessage clodResponse = ServerLoginMessage.fromJson(clodCh.getJson());
        assertEquals(1, clodResponse.getError());
        assertEquals("[ERROR] A new game for 2 players is starting. Your connection will be closed.", clodResponse.getDisplayText());
    }
}
