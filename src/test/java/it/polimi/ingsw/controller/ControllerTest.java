package it.polimi.ingsw.controller;

import it.polimi.ingsw.exceptions.GameEndedException;
import it.polimi.ingsw.languages.MessageResourceBundle;
import it.polimi.ingsw.messages.Message;
import it.polimi.ingsw.messages.login.ClientLoginMessage;
import it.polimi.ingsw.messages.login.ServerLoginMessage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ControllerTest {

    Controller controller = new Controller();
    ClientHandlerStub ch = new ClientHandlerStub();
    ClientHandlerStub secondCh = new ClientHandlerStub();

    @BeforeAll
    static void initializeMessagesResourceBundle() {
        MessageResourceBundle.initializeBundle("en");
    }

    /**
     * Tests the effect of a pong message on the controller - that is incrementing by 1 the variable pongCount
     */
    @Test
    void testPong() {
        String json = "{status:PONG,error:0}";
        assertDoesNotThrow(() -> controller.handleMessage(json, ch));
        assertEquals(1, controller.getPongCount());
        for (int i = 0; i < 3; i++) {
            assertDoesNotThrow(() -> controller.handleMessage(json, ch));
        }
        assertEquals(4, controller.getPongCount());
    }

    /**
     * Checks the answer to a message with invalid status
     */
    @Test
    void testInvalidStatus() {
        String json = "{status:invalid,error:0}";
        assertDoesNotThrow(() -> controller.handleMessage(json, ch));
        Message msg = Message.fromJson(ch.getJson());
        assertEquals(3, msg.getError());
    }

    /**
     * Checks the answer to a message with invalid action
     */
    @Test
    void testInvalidAction1() {
        String json = "{status:LOGIN,username:clod,action:INVALID,numPlayers:3}";
        assertDoesNotThrow(() -> controller.handleMessage(json, ch));
        Message msg = Message.fromJson(ch.getJson());
        assertEquals("LOGIN", msg.getStatus());
        assertEquals(3, msg.getError());
    }

    /**
     * Checks the answer to a message with missing action
     */
    @Test
    void testInvalidAction2() {
        String json = "{status:LOGIN,username:clod,numPlayers:3}";
        assertDoesNotThrow(() -> controller.handleMessage(json, ch));
        Message msg = Message.fromJson(ch.getJson());
        assertEquals("LOGIN", msg.getStatus());
        assertEquals(3, msg.getError());
    }

    /**
     * Checks the answer to a message with a username field made only of spaces
     */
    @Test
    void testEmptyUsername() {
        ClientLoginMessage clientLoginMessage = new ClientLoginMessage();
        clientLoginMessage.setUsername("  ");
        clientLoginMessage.setAction("SET_USERNAME");
        String jsonEmptyUsername = clientLoginMessage.toJson();
        assertDoesNotThrow(() -> controller.handleMessage(jsonEmptyUsername, ch));
        ServerLoginMessage msg = ServerLoginMessage.fromJson(ch.getJson());
        assertEquals(3, msg.getError());
        assertEquals("[ERROR] " + MessageResourceBundle.getMessage("invalid_username"), msg.getDisplayText());
    }

    /**
     * Checks the answer to a message with a username longer than 32 characters
     */
    @Test
    void testTooLongUsername() {
        String json = "{status:LOGIN,username:" + "a".repeat(33) + ",action:SET_USERNAME,error:0}";
        assertDoesNotThrow(() -> controller.handleMessage(json, ch));
        ServerLoginMessage msg = ServerLoginMessage.fromJson(ch.getJson());
        assertEquals(3, msg.getError());
        assertEquals("[ERROR] " + MessageResourceBundle.getMessage("username_too_long"), msg.getDisplayText());
    }

    /**
     * Tests what happens when a player attempts to log in with a username already taken
     */
    @Test
    void testAlreadyTakenUsername() {
        String firstLoginJson = "{status:LOGIN,username:clod,action:SET_USERNAME,error:0}";
        assertDoesNotThrow(() -> controller.handleMessage(firstLoginJson, ch));

        String secondLoginJson = "{status:LOGIN,username:clod,action:SET_USERNAME,error:0}";
        assertDoesNotThrow(() -> controller.handleMessage(secondLoginJson, secondCh));

        ServerLoginMessage errorResponse = ServerLoginMessage.fromJson(secondCh.getJson());
        assertEquals(2, errorResponse.getError());
        assertEquals("[ERROR] " + MessageResourceBundle.getMessage("username_already_taken"), errorResponse.getDisplayText());

        assertEquals(1, controller.getLoggedUsers().size());
    }

    /**
     * Tests what happens when two player with a valid username join the lobby, even though yhe first one hasn't
     * chosen game parameters yet
     */
    @Test
    void testTwoValidLogins() {
        String firstLoginJson = "{status:LOGIN,username:clod,action:SET_USERNAME,error:0}";
        assertDoesNotThrow(() -> controller.handleMessage(firstLoginJson, ch));
        assertTrue(controller.getLoggedUsers().stream().anyMatch(p -> p.getCommunicable().equals(ch)));

        String firstResponseJson = ch.getJson();
        ServerLoginMessage firstMsg = ServerLoginMessage.fromJson(ch.getJson());
        assertEquals("CREATE_GAME", firstMsg.getAction());

        String secondLoginJson = "{status:LOGIN,username:rick,action:SET_USERNAME,error:0}";
        assertDoesNotThrow(() -> controller.handleMessage(secondLoginJson, secondCh));
        assertEquals(2, controller.getLoggedUsers().size());
        assertTrue(controller.getLoggedUsers().stream().anyMatch(p -> p.getCommunicable().equals(secondCh)));

        // The player who is creating the game hasn't received the broadcast message
        assertEquals(firstResponseJson, ch.getJson());

        // The second player should have received a broadcast message telling him he's in the lobby
        ServerLoginMessage secondMsg = ServerLoginMessage.fromJson(secondCh.getJson());
        assertEquals(MessageResourceBundle.getMessage("new_player_joined"), secondMsg.getDisplayText());
        assertEquals("clod", secondMsg.getGameLobby().getPlayers().get(0));
        assertEquals("rick", secondMsg.getGameLobby().getPlayers().get(1));
        assertEquals(-1, secondMsg.getGameLobby().getNumPlayers());
    }

    /**
     * Tests what happens when the first player logs in and sets game parameters
     */
    @Test
    void testGameCreation() {
        String login = "{status:LOGIN,username:clod,action:SET_USERNAME,error:0}";
        assertDoesNotThrow(() -> controller.handleMessage(login, ch));

        String createGame = "{status:LOGIN,username:clod,action:CREATE_GAME,expert:true,numPlayers:3}";
        assertDoesNotThrow(() -> controller.handleMessage(createGame, ch));

        ServerLoginMessage gameCreatedMessage = ServerLoginMessage.fromJson(ch.getJson());
        assertEquals(MessageResourceBundle.getMessage("game_created"), gameCreatedMessage.getDisplayText());
    }

    /**
     * Tests the rightness of the messages sent to every player who joins an existing game
     */
    @Test
    void testJoiningGame() {

        testGameCreation();

        String loginJson = "{status:LOGIN,username:giuse,action:SET_USERNAME,error:0}";
        assertDoesNotThrow(() -> controller.handleMessage(loginJson, secondCh));

        ServerLoginMessage clodMessage = ServerLoginMessage.fromJson(ch.getJson());
        ServerLoginMessage giuseMessage = ServerLoginMessage.fromJson(secondCh.getJson());

        assertEquals(MessageResourceBundle.getMessage("new_player_joined"), clodMessage.getDisplayText(), giuseMessage.getDisplayText());
        assertEquals(new ArrayList<String>(List.of(new String[]{"clod", "giuse"})), clodMessage.getGameLobby().getPlayers());
    }

    /**
     * Tests two cases: when the correct player tries to set an invalid number of players or when the wrong player
     * attempts to set up a new game
     */
    @Test
    void testInvalidGameParameters() {
        testTwoValidLogins();

        // Num players is 5 -> invalid
        String invalidNumPlayersJson = "{status:LOGIN,username:clod,action:CREATE_GAME,expert:true,numPlayers:5}";
        assertDoesNotThrow(() -> controller.handleMessage(invalidNumPlayersJson, ch));

        ServerLoginMessage response = ServerLoginMessage.fromJson(ch.getJson());
        assertEquals(3, response.getError());
        assertEquals("[ERROR] " + MessageResourceBundle.getMessage("invalid_num_players"), response.getDisplayText());
        assertNull(response.getGameLobby());
        assertNull(controller.getGame());

        // Num player is "test" -> invalid
        String wrongFormatJson = "{status:LOGIN,username:clod,action:CREATE_GAME,expert:true,numPlayers:test}";
        assertDoesNotThrow(() -> controller.handleMessage(wrongFormatJson, ch));

        ServerLoginMessage secondResponse = ServerLoginMessage.fromJson(ch.getJson());
        assertEquals(3, secondResponse.getError());
        assertEquals("[ERROR] " + MessageResourceBundle.getMessage("invalid_num_players"), response.getDisplayText());

        // Second player is trying to set game parameters -> invalid
        String invalidPlayerJson = "{status:LOGIN,username:rick,action:CREATE_GAME,expert:true,numPlayers:2}";
        assertDoesNotThrow(() -> controller.handleMessage(invalidPlayerJson, secondCh));
        ServerLoginMessage thirdResponse = ServerLoginMessage.fromJson(secondCh.getJson());
        assertEquals(3, thirdResponse.getError());
        assertEquals("[ERROR] " + MessageResourceBundle.getMessage("invalid_player_creating_game"), thirdResponse.getDisplayText());
        assertNull(thirdResponse.getGameLobby());
        assertNull(controller.getGame());
    }

    /**
     * Creates a new 2 players expert game while 3 players are waiting in the lobby: last one is kicked
     */
    @Test
    void testValidTwoGameParameters() {
        testTwoValidLogins();

        ClientHandlerStub thirdCh = new ClientHandlerStub();
        String loginThirdUser = "{status:LOGIN,username:giuse,action:SET_USERNAME,error:0}";
        assertDoesNotThrow(() -> controller.handleMessage(loginThirdUser, thirdCh));

        assertEquals(3, controller.getLoggedUsers().size());

        String validNumPlayersJson = "{status:LOGIN,username:clod,action:CREATE_GAME,expert:true,numPlayers:2}";
        assertDoesNotThrow(() -> controller.handleMessage(validNumPlayersJson, ch));

        // A new expert game for 2 players should be started: clod and rick should be in, while giuse has to be kicked
        assertEquals(2, controller.getGame().getPlayers().size());
        assertTrue(controller.getGame().isExpert());
        assertNotNull(controller.getLoggedUsers().get(0).getPlayer());
        assertNotNull(controller.getLoggedUsers().get(1).getPlayer());

        assertEquals("clod", controller.getLoggedUsers().get(0).getUsername(), controller.getLoggedUsers().get(1).getPlayer().getName());
        assertEquals("rick", controller.getLoggedUsers().get(1).getUsername(), controller.getLoggedUsers().get(1).getPlayer().getName());


        ServerLoginMessage giuseMessage = ServerLoginMessage.fromJson(thirdCh.getJson());
        assertEquals(1, giuseMessage.getError());
        assertEquals("[ERROR] A new game for 2 players is starting. Your connection will be closed", giuseMessage.getDisplayText());
    }

    /**
     * Tests the behaviour of the controller when a new user attempts to join the lobby but a game is already in progress
     */
    @Test
    void testJoiningAfterGameStarted() {
        testValidTwoGameParameters();

        ClientHandlerStub fourthCh = new ClientHandlerStub();
        String loginFourthUser = "{status:LOGIN,username:stub,action:SET_USERNAME,error:0}";
        assertDoesNotThrow(() -> controller.handleMessage(loginFourthUser, fourthCh));

        ServerLoginMessage errorResponse = ServerLoginMessage.fromJson(fourthCh.getJson());
        assertEquals(1, errorResponse.getError());
        assertEquals("[ERROR] " + MessageResourceBundle.getMessage("lobby_full"), errorResponse.getDisplayText());
    }

    /**
     * Tests the creation of a new four players game
     */
    @Test
    void testFourPlayersGame() {
        testTwoValidLogins();

        ClientHandlerStub thirdCh = new ClientHandlerStub();
        String loginThirdUser = "{status:LOGIN,username:giuse,action:SET_USERNAME,error:0}";
        assertDoesNotThrow(() -> controller.handleMessage(loginThirdUser, thirdCh));

        ClientHandlerStub fourthCh = new ClientHandlerStub();
        String loginFourthUser = "{status:LOGIN,username:stub,action:SET_USERNAME,error:0}";
        assertDoesNotThrow(() -> controller.handleMessage(loginFourthUser, fourthCh));

        // Start a new expert game for four players
        String startGameJson = "{status:LOGIN,username:clod,action:CREATE_GAME,expert:true,numPlayers:4}";
        assertDoesNotThrow(() -> controller.handleMessage(startGameJson, ch));

        assertEquals(4, controller.getGame().getPlayers().size());
        assertTrue(controller.getGame().isExpert());
    }

    /**
     * Tests what happens when an already logged-in user attempts to rename himself
     */
    @Test
    void testRenameUser() throws GameEndedException {
        // Rick logs in with a valid username
        String rickLogin = "{status:LOGIN,username:rick,action:SET_USERNAME,error:0}";
        controller.handleMessage(rickLogin, ch);

        // Rick attempts to change his username with a too long string
        String tooLongUsernameJson = "{status:LOGIN,username:" + "a".repeat(33) + ",action:SET_USERNAME,error:0}";
        testInvalidRenameUserAssertions(tooLongUsernameJson, "username_too_long");

        // Rick attempts to change his username with a blank string
        ClientLoginMessage clientLoginMessage = new ClientLoginMessage();
        clientLoginMessage.setUsername("  ");
        clientLoginMessage.setAction("SET_USERNAME");
        String emptyUsernameJson = clientLoginMessage.toJson();
        testInvalidRenameUserAssertions(emptyUsernameJson, "invalid_username");

        // Giuse logs in and Rick attempts to take his username
        String giuseLogin = "{status:LOGIN,username:giuse,action:SET_USERNAME,error:0}";
        controller.handleMessage(giuseLogin, secondCh);
        String alreadyTakenUsernameJson = "{status:LOGIN,username:giuse,action:SET_USERNAME,error:0}";
        testInvalidRenameUserAssertions(alreadyTakenUsernameJson, "username_already_taken");

        // Rick attempts to change his username with a valid one
        String changeUsername = "{status:LOGIN,username:username,action:SET_USERNAME,error:0}";
        controller.handleMessage(changeUsername, ch);

        assertEquals("username", controller.getLoggedUsers().get(0).getUsername());
    }

    /**
     * Asserts that the provided json message, handled by the controller, gives an error message with 5 as error code
     * and with the given key in the "key" field
     *
     * @param json        the input json {@code String}
     * @param expectedKey the expected key for the error message
     */
    private void testInvalidRenameUserAssertions(String json, String expectedKey) throws GameEndedException {
        controller.handleMessage(json, ch);
        ServerLoginMessage response = ServerLoginMessage.fromJson(ch.getJson());
        assertEquals(5, response.getError());
        assertEquals(MessageResourceBundle.getMessage("error_tag") + MessageResourceBundle.getMessage(expectedKey),
                response.getDisplayText());
    }

    /**
     * Tests what happens when
     */

}