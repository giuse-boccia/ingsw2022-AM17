package it.polimi.ingsw.controller;

import it.polimi.ingsw.utils.constants.Messages;
import it.polimi.ingsw.constants.Constants;
import it.polimi.ingsw.exceptions.GameEndedException;
import it.polimi.ingsw.languages.MessageResourceBundle;
import it.polimi.ingsw.messages.action.ServerActionMessage;
import it.polimi.ingsw.messages.login.ServerLoginMessage;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.characters.*;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.model.game_objects.Student;
import it.polimi.ingsw.model.game_objects.gameboard_objects.Island;
import it.polimi.ingsw.model.utils.Students;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ActionPhaseControllerTest {

    Controller controller = new Controller();
    ClientHandlerStub rickCh = new ClientHandlerStub();
    ClientHandlerStub giuseCh = new ClientHandlerStub();
    Player rick, giuse;

    @BeforeAll
    static void initializeMessagesResourceBundle() {
        MessageResourceBundle.initializeBundle("en");
    }

    /**
     * In this test a player tries to play when it's not his turn
     */
    @Test
    void testInvalidTurn() throws GameEndedException {
        startActionPhase(false);

        String rickMoveStudent = "{status:ACTION,player:rick,action:{name:MOVE_STUDENT_TO_DINING,args:{color:GREEN}}}";
        controller.handleMessage(rickMoveStudent, rickCh);

        ServerActionMessage response = ServerActionMessage.fromJson(rickCh.getJson());
        assertEquals(1, response.getError());
        assertEquals("[ERROR] " + MessageResourceBundle.getMessage("not_your_turn"), response.getDisplayText());
    }

    /**
     * In this test a user who is logged in - but is waiting for his turn -
     * sends an action message pretending to be the player who has to play
     */
    @Test
    void testFakeAlias() throws GameEndedException {
        startActionPhase(false);

        // Field "player" has value "giuse" but the message comes from Rick's Communicable
        String json = "{status:ACTION,player:giuse,action:{name:MOVE_STUDENT_TO_DINING,args:{color:GREEN}}}";
        controller.handleMessage(json, rickCh);

        ServerActionMessage response = ServerActionMessage.fromJson(rickCh.getJson());
        assertEquals(3, response.getError());
        assertEquals("[ERROR] " + MessageResourceBundle.getMessage("invalid_identity"), response.getDisplayText());
    }

    /**
     * Tests what happens when the correct player tries to move a student he does or doesn't own to his dining room
     */
    @Test
    void testMoveStudentToDining() throws GameEndedException {
        startActionPhase(false);
        fillEntrancesAndEmptyIslandsAndSetCharacters();

        // Giuse does not own a green student
        String invalidJson = "{status:ACTION,player:giuse,action:{name:MOVE_STUDENT_TO_DINING,args:{color:GREEN}}}";
        controller.handleMessage(invalidJson, giuseCh);

        ServerActionMessage response = ServerActionMessage.fromJson(giuseCh.getJson());
        assertEquals(1, response.getError());
        assertEquals("[ERROR] " + MessageResourceBundle.getMessage("entrance_doesnt_contain_student"), response.getDisplayText());

        for (int i = 1; i <= 3; i++) {
            String moveRedStudent = "{status:ACTION,player:giuse,action:{name:MOVE_STUDENT_TO_DINING,args:{color:RED}}}";
            controller.handleMessage(moveRedStudent, giuseCh);

            ServerActionMessage res = ServerActionMessage.fromJson(giuseCh.getJson());
            assertEquals(0, res.getError());
            assertEquals(i, Students.countColor(giuse.getDashboard().getDiningRoom().getStudents(), Color.RED));
            assertEquals(7 - i, Students.countColor(giuse.getDashboard().getEntrance().getStudents(), Color.RED));
        }

        assertEquals(2, giuse.getNumCoins());
        assertArrayEquals(new Color[]{Color.RED}, giuse.getColorsOfOwnedProfessors().toArray(new Color[0]));

    }

    /**
     * Tests what happens when the correct player tries to move a student to his dining room
     */
    @Test
    void testMoveStudentToIsland() throws GameEndedException {
        startActionPhase(false);
        fillEntrancesAndEmptyIslandsAndSetCharacters();

        String invalidJson = "{status:ACTION,player:giuse,action:{name:MOVE_STUDENT_TO_ISLAND,args:{color:RED,island:-1}}}";
        controller.handleMessage(invalidJson, giuseCh);

        ServerActionMessage invalidRes = ServerActionMessage.fromJson(giuseCh.getJson());
        assertEquals(2, invalidRes.getError());
        assertEquals("[ERROR] " + MessageResourceBundle.getMessage("invalid_island"), invalidRes.getDisplayText());

        // Giuse wants to move a red student to the third island
        String json = "{status:ACTION,player:giuse,action:{name:MOVE_STUDENT_TO_ISLAND,args:{color:RED,island:2}}}";
        controller.handleMessage(json, giuseCh);

        ServerActionMessage res = ServerActionMessage.fromJson(giuseCh.getJson());
        assertEquals(0, res.getError());

        assertEquals(1,
                Students.countColor(controller.getGame().getGameBoard().getIslands().get(2).getStudents(), Color.RED));
        assertEquals(6, Students.countColor(giuse.getDashboard().getEntrance().getStudents(), Color.RED));
    }

    /**
     * Tests json messages with invalid format - missing parentheses at the end and a word written instead
     * of a number
     */
    @Test
    void testInvalidJsonFormat() throws GameEndedException {
        startActionPhase(false);
        fillEntrancesAndEmptyIslandsAndSetCharacters();

        // Json message with invalid parentheses (missing two at the end)
        String missingParentheses = "{status:ACTION,player:giuse,action:{name:MOVE_STUDENT_TO_ISLAND,args:{color:RED,island:-1}";
        controller.handleMessage(missingParentheses, giuseCh);

        ServerLoginMessage invalidRes = ServerLoginMessage.fromJson(giuseCh.getJson());
        assertEquals(3, invalidRes.getError());
        assertEquals("[ERROR] " + MessageResourceBundle.getMessage("unrecognised_type"), invalidRes.getDisplayText());

        // Invalid island index: there's a word instead of a number
        String json = "{status:ACTION,player:giuse,action:{name:MOVE_STUDENT_TO_ISLAND,args:{color:RED,island:word}}}";
        controller.handleMessage(json, giuseCh);

        ServerActionMessage invalidSyntaxRes = ServerActionMessage.fromJson(giuseCh.getJson());
        assertEquals(3, invalidSyntaxRes.getError());
        assertEquals("[ERROR] " + MessageResourceBundle.getMessage("bad_request_syntax"), invalidSyntaxRes.getDisplayText());
    }

    /**
     * Tests what happens when the correct player tries to move mother nature of an invalid number of steps
     * and then a valid one
     */
    @Test
    void testMotherNatureMove() throws GameEndedException {
        playUntilMotherNatureMove();

        String invalidMoveMnJson = "{status:ACTION,player:giuse,action:{name:MOVE_MN,args:{num_steps:-1}}}";
        controller.handleMessage(invalidMoveMnJson, giuseCh);

        ServerActionMessage errorMessage = ServerActionMessage.fromJson(giuseCh.getJson());
        assertEquals(1, errorMessage.getError());
        assertEquals("[ERROR] " + MessageResourceBundle.getMessage("invalid_mn_move"), errorMessage.getDisplayText());

        String validMnMoveJson = "{status:ACTION,player:giuse,action:{name:MOVE_MN,args:{num_steps:1}}}";
        controller.handleMessage(validMnMoveJson, giuseCh);

        assertEquals(1, controller.getGame().getGameBoard().getMotherNatureIndex());
    }

    /**
     * Tests what happens when the correct player tries to select an invalid cloud and then a valid one
     */
    @Test
    void testChooseCloud() throws GameEndedException {
        playUntilMotherNatureMove();

        String invalidChooseCloudJson = "{status:ACTION,player:giuse,action:{name:FILL_FROM_CLOUD,args:{cloud:0}}}";
        controller.handleMessage(invalidChooseCloudJson, giuseCh);

        ServerActionMessage errorMessage = ServerActionMessage.fromJson(giuseCh.getJson());
        assertEquals(1, errorMessage.getError());
        assertEquals("[ERROR] " + MessageResourceBundle.getMessage("move_mn_first"), errorMessage.getDisplayText());
        assertFalse(controller.getGame().getGameBoard().getClouds().get(1).isEmpty());

        String validMnMoveJson = "{status:ACTION,player:giuse,action:{name:MOVE_MN,args:{num_steps:1}}}";
        controller.handleMessage(validMnMoveJson, giuseCh);

        String invalidCloudJson = "{status:ACTION,player:giuse,action:{name:FILL_FROM_CLOUD,args:{cloud:2}}}";
        controller.handleMessage(invalidCloudJson, giuseCh);

        ServerActionMessage invalidCloudMessage = ServerActionMessage.fromJson(giuseCh.getJson());
        assertEquals(1, invalidCloudMessage.getError());
        assertEquals("[ERROR] " + MessageResourceBundle.getMessage("invalid_cloud"), invalidCloudMessage.getDisplayText());
        assertFalse(controller.getGame().getGameBoard().getClouds().get(1).isEmpty());

        String validCloudJson = "{status:ACTION,player:giuse,action:{name:FILL_FROM_CLOUD,args:{cloud:1}}}";
        controller.handleMessage(validCloudJson, giuseCh);

        assertTrue(controller.getGame().getGameBoard().getClouds().get(1).isEmpty());
        assertFalse(controller.getGame().getGameBoard().getClouds().get(0).isEmpty());

        // It's Rick's turn
        assertEquals(rick, controller.getGame().getCurrentRound().getCurrentPlayerActionPhase().getCurrentPlayer());
    }

    /**
     * Tests a full round played in a 2-players match, especially the transition between the played round and the following one
     */
    @Test
    void testFullRound() throws GameEndedException {
        // Giuse moves 3 red students to his dining room
        playUntilMotherNatureMove();
        // Giuse moves mother nature of 1 position and chooses first cloud
        String giuseMovesMn = "{status:ACTION,player:giuse,action:{name:MOVE_MN,args:{num_steps:1}}}";
        String giuseChoosesCloud = "{status:ACTION,player:giuse,action:{name:FILL_FROM_CLOUD,args:{cloud:0}}}";
        controller.handleMessage(giuseMovesMn, giuseCh);
        controller.handleMessage(giuseChoosesCloud, giuseCh);

        // Rick moves three pink students to his dining room, moves mother nature of one position
        // and chooses the second cloud
        String rickMovesPinkToDining = "{status:ACTION,player:rick,action:{name:MOVE_STUDENT_TO_DINING,args:{color:PINK}}}";
        String rickMovesMn = "{status:ACTION,player:rick,action:{name:MOVE_MN,args:{num_steps:1}}}";
        String rickChoosesCloud = "{status:ACTION,player:rick,action:{name:FILL_FROM_CLOUD,args:{cloud:1}}}";
        for (int i = 0; i < 3; i++) {
            controller.handleMessage(rickMovesPinkToDining, rickCh);
        }
        controller.handleMessage(rickMovesMn, rickCh);
        controller.handleMessage(rickChoosesCloud, rickCh);

        // Now Giuse has to play assistant
        assertEquals(giuse, controller.getGame().getCurrentRound().getPlanningPhase().getNextPlayer());
        ServerActionMessage giuseMessage = ServerActionMessage.fromJson(giuseCh.getJson());
        assertArrayEquals(new String[]{Constants.ACTION_PLAY_ASSISTANT}, giuseMessage.getActions().toArray(new String[0]));
    }

    /**
     * Tests messages received at the end of the game
     */
    @Test
    void testLastRound() throws GameEndedException {
        startActionPhase(true);
        fillEntrancesAndEmptyIslandsAndSetCharacters();

        // Giuse moves 3 red students to his dining room and moves mother nature of 1 position
        for (int i = 0; i < 3; i++) {
            String giuseMovesStudentJson = "{status:ACTION,player:giuse,action:{name:MOVE_STUDENT_TO_DINING,args:{color:RED}}}";
            controller.handleMessage(giuseMovesStudentJson, giuseCh);
        }
        String giuseMovesMn = "{status:ACTION,player:giuse,action:{name:MOVE_MN,args:{num_steps:1}}}";
        controller.handleMessage(giuseMovesMn, giuseCh);

        // Rick moves two pink students to his dining room, one pink to the third island, then moves mother nature of one
        // position - so that he can build a tower
        String rickMovesPinkToDining = "{status:ACTION,player:rick,action:{name:MOVE_STUDENT_TO_DINING,args:{color:PINK}}}";
        String rickMovesPinkToIsland = "{status:ACTION,player:rick,action:{name:MOVE_STUDENT_TO_ISLAND,args:{color:PINK,island:2}}}";
        String rickMovesMn = "{status:ACTION,player:rick,action:{name:MOVE_MN,args:{num_steps:1}}}";
        controller.handleMessage(rickMovesPinkToDining, rickCh);
        controller.handleMessage(rickMovesPinkToDining, rickCh);
        controller.handleMessage(rickMovesPinkToIsland, rickCh);
        assertThrows(GameEndedException.class, () -> controller.handleMessage(rickMovesMn, rickCh));

        assertTrue(controller.getGame().isEnded());
        ServerActionMessage rickEndMessage = ServerActionMessage.fromJson(rickCh.getJson());
        ServerActionMessage giuseEndMessage = ServerActionMessage.fromJson(giuseCh.getJson());
        assertEquals(Constants.STATUS_END, rickEndMessage.getStatus(), giuseEndMessage.getStatus());
        assertEquals(MessageResourceBundle.getMessage("game_lost") + "rick" + MessageResourceBundle.getMessage("broadcast_game_won"), giuseEndMessage.getDisplayText());
        assertEquals(MessageResourceBundle.getMessage("game_won"), rickEndMessage.getDisplayText());
    }

    /**
     * Tests what happens when the correct player tries to play a character which is not in the game or a valid character with
     * invalid or missing arguments
     */
    @Test
    void testInvalidPlayMovingCharacter() throws GameEndedException {
        playUntilMotherNatureMove();

        String playInvalidCharacter =
                "{status:ACTION,player:giuse,action:{name:PLAY_CHARACTER,args:{characterName:ignoreTowers}}}";
        controller.handleMessage(playInvalidCharacter, giuseCh);

        ServerActionMessage invalidCharacterResponse = ServerActionMessage.fromJson(giuseCh.getJson());
        assertEquals(1, invalidCharacterResponse.getError());
        assertEquals("[ERROR] " + MessageResourceBundle.getMessage("character_not_in_game"), invalidCharacterResponse.getDisplayText());

        String playInvalidArgumentsCharacter =
                "{status:ACTION,player:giuse,action:{name:PLAY_CHARACTER,args:" +
                        "{characterName:move1FromCardToIsland,sourceStudents:[GREEN, GREEN],island:2}}}";
        controller.handleMessage(playInvalidArgumentsCharacter, giuseCh);

        ServerActionMessage invalidArgumentCharacterResponse = ServerActionMessage.fromJson(giuseCh.getJson());
        assertEquals(1, invalidArgumentCharacterResponse.getError());
        assertEquals("[ERROR] " + MessageResourceBundle.getMessage("move_just_one"), invalidArgumentCharacterResponse.getDisplayText());

        String missingArgumentsCharacter =
                "{status:ACTION,player:giuse,action:{name:PLAY_CHARACTER,args:" +
                        "{characterName:move1FromCardToIsland,sourceStudents:[GREEN]}}}";
        controller.handleMessage(missingArgumentsCharacter, giuseCh);

        ServerActionMessage missingArgumentsCharacterResponse = ServerActionMessage.fromJson(giuseCh.getJson());
        assertEquals(1, missingArgumentsCharacterResponse.getError());
        assertEquals("[ERROR] " + MessageResourceBundle.getMessage("invalid_argument"), missingArgumentsCharacterResponse.getDisplayText());

        String studentNotOnCardCharacter =
                "{status:ACTION,player:giuse,action:{name:PLAY_CHARACTER,args:" +
                        "{characterName:move1FromCardToIsland,sourceStudents:[BLUE],island:0}}}";
        controller.handleMessage(studentNotOnCardCharacter, giuseCh);

        ServerActionMessage studentNotOnCardResponse = ServerActionMessage.fromJson(giuseCh.getJson());
        assertEquals(2, studentNotOnCardResponse.getError());
        assertEquals("[ERROR] " + MessageResourceBundle.getMessage("student_not_found"), studentNotOnCardResponse.getDisplayText());


        String invalidIslandIndexCharacter =
                "{status:ACTION,player:giuse,action:{name:PLAY_CHARACTER,args:" +
                        "{characterName:move1FromCardToIsland,sourceStudents:[GREEN],island:14}}}";
        controller.handleMessage(invalidIslandIndexCharacter, giuseCh);

        ServerActionMessage invalidIslandIndexResponse = ServerActionMessage.fromJson(giuseCh.getJson());
        assertEquals(1, invalidIslandIndexResponse.getError());
        assertEquals("[ERROR] " + MessageResourceBundle.getMessage("invalid_argument"), invalidIslandIndexResponse.getDisplayText());
    }

    /**
     * Tests what happens when a player tries to play a passive character without providing the necessary arguments - e.g. color to ignore
     * during the influence calculus for ignoreColor character
     */
    @Test
    void testInvalidPassiveCharacter() throws GameEndedException {
        playUntilMotherNatureMove();
        giuse.addCoin();
        giuse.addCoin();

        String playInvalidCharacter =
                "{status:ACTION,player:giuse,action:{name:PLAY_CHARACTER,args:{characterName:ignoreColor}}}";
        controller.handleMessage(playInvalidCharacter, giuseCh);

        ServerActionMessage invalidArgumentResponse = ServerActionMessage.fromJson(giuseCh.getJson());
        assertEquals(1, invalidArgumentResponse.getError());
        assertEquals("[ERROR] " + MessageResourceBundle.getMessage("invalid_argument"), invalidArgumentResponse.getDisplayText());
    }

    /**
     * Tests what happens when a player correctly plays a character (with the correct arguments) and when, in the same turn, he tries to
     * play again the character
     */
    @Test
    void testValidCharacter() throws GameEndedException {
        startActionPhase(false);
        fillEntrancesAndEmptyIslandsAndSetCharacters();
        for (int i = 0; i < 100; i++) {
            giuse.addCoin();
        }

        String playValidCharacter =
                "{status:ACTION,player:giuse,action:{name:PLAY_CHARACTER,args:{characterName:everyOneMove3FromDiningRoomToBag,color:RED}}}";
        controller.handleMessage(playValidCharacter, giuseCh);

        ServerActionMessage response = ServerActionMessage.fromJson(giuseCh.getJson());
        assertEquals(0, response.getError());


        String playAgainCharacter =
                "{status:ACTION,player:giuse,action:{name:PLAY_CHARACTER,args:{characterName:ignoreColor,color:RED}}}";
        controller.handleMessage(playAgainCharacter, giuseCh);

        ServerActionMessage invalidArgumentResponse = ServerActionMessage.fromJson(giuseCh.getJson());
        assertEquals(2, invalidArgumentResponse.getError());
        assertEquals("[ERROR] " + MessageResourceBundle.getMessage("already_played_character"), invalidArgumentResponse.getDisplayText());
    }

    @Test
    void testAlertLastRound() throws GameEndedException {
        String rickLoginJson = "{status:LOGIN,username:rick,action:SET_USERNAME,error:0}";
        String giuseLoginJson = "{status:LOGIN,username:giuse,action:SET_USERNAME,error:0}";

        controller.handleMessage(rickLoginJson, rickCh);
        controller.handleMessage(giuseLoginJson, giuseCh);

        String createGameJson = "{status:LOGIN,username:rick,action:CREATE_GAME,expert:true,numPlayers:2}";
        controller.handleMessage(createGameJson, rickCh);
        controller.getGame().getCurrentRound().setLastRound();

        String rickPlayAssistantJson = "{status:ACTION,player:rick,action:{name:PLAY_ASSISTANT,args:{value:5}}}";
        String giusePlayAssistantJson = "{status:ACTION,player:giuse,action:{name:PLAY_ASSISTANT,args:{value:4}}}";

        controller.handleMessage(rickPlayAssistantJson, rickCh);
        controller.handleMessage(giusePlayAssistantJson, giuseCh);

        assertTrue(controller.getGame().getCurrentRound().isLastRound());
    }

    // TODO check why alertLastRound is not covered and test alertGameEnded

    /**
     * Handles the login and the planning phase of a new expert game for two, during which
     * Rick plays assistant number 5 and Giuse assistant number 4.
     * That means Giuse has to play his PlayerActionPhase
     *
     * @param lastRound a {@code boolean} set to true if this is the last round of the game
     */
    private void startActionPhase(boolean lastRound) throws GameEndedException {
        String rickLoginJson = "{status:LOGIN,username:rick,action:SET_USERNAME,error:0}";
        String giuseLoginJson = "{status:LOGIN,username:giuse,action:SET_USERNAME,error:0}";

        controller.handleMessage(rickLoginJson, rickCh);
        controller.handleMessage(giuseLoginJson, giuseCh);

        String createGameJson = "{status:LOGIN,username:rick,action:CREATE_GAME,expert:true,numPlayers:2}";
        controller.handleMessage(createGameJson, rickCh);

        if (lastRound) {
            controller.getGame().getCurrentRound().setLastRound();
        }

        rick = controller.getLoggedUsers().get(0).getPlayer();
        giuse = controller.getLoggedUsers().get(1).getPlayer();
        // Rick has to play before Giuse
        controller.getGame().getCurrentRound().getPlanningPhase().setPlayersInOrder(new ArrayList<>(List.of(rick, giuse)));

        String rickPlayAssistantJson = "{status:ACTION,player:rick,action:{name:PLAY_ASSISTANT,args:{value:5}}}";
        String giusePlayAssistantJson = "{status:ACTION,player:giuse,action:{name:PLAY_ASSISTANT,args:{value:4}}}";

        controller.handleMessage(rickPlayAssistantJson, rickCh);
        controller.handleMessage(giusePlayAssistantJson, giuseCh);

        rick.getDashboard().getEntrance().setStudents(new ArrayList<>());
    }

    /**
     * Rick has 7 PINK students in his entrance
     * Giuse has 7 RED students in his entrance
     * All islands are empty and mother nature is on the first island
     * The 3 characters are:
     * - move1FromCardToIsland (4 GREEN students on the card)
     * - everyOneMove3FromDiningRoomToBag
     * - ignoreColor
     */
    private void fillEntrancesAndEmptyIslandsAndSetCharacters() {
        ArrayList<Student> rickStudents = new ArrayList<>();
        ArrayList<Student> giuseStudents = new ArrayList<>();
        for (int i = 0; i < 7; i++) {
            rickStudents.add(new Student(Color.PINK));
            giuseStudents.add(new Student(Color.RED));
        }
        rick.getDashboard().getEntrance().setStudents(rickStudents);
        giuse.getDashboard().getEntrance().setStudents(giuseStudents);

        for (Island island : controller.getGame().getGameBoard().getIslands()) {
            island.setStudents(new ArrayList<>());
        }

        controller.getGame().getGameBoard().setMotherNatureIndex(0);

        Character[] characters = {
                new MovingCharacter(CharacterName.move1FromCardToIsland, controller.getGame().getGameBoard(), 4, 1),
                new EveryOneMovesCharacter(CharacterName.everyOneMove3FromDiningRoomToBag, controller.getGame().getGameBoard()),
                new PassiveCharacter(CharacterName.ignoreColor)
        };
        MovingCharacter firstCharacter = (MovingCharacter) characters[0];
        firstCharacter.setStudents(new ArrayList<>(List.of(
                new Student(Color.GREEN), new Student(Color.GREEN), new Student(Color.GREEN), new Student(Color.GREEN)
        )));

        controller.getGame().getGameBoard().setCharacters(characters);
    }

    /**
     * Calls the two methods startActionPhase() and fillEntrancesAndEmptyIslandsAndSetCharacters() and moves three red
     * students from Giuse's entrance to his dining room
     */
    private void playUntilMotherNatureMove() throws GameEndedException {
        startActionPhase(false);
        fillEntrancesAndEmptyIslandsAndSetCharacters();
        for (int i = 0; i < 3; i++) {
            String moveRedStudent = "{status:ACTION,player:giuse,action:{name:MOVE_STUDENT_TO_DINING,args:{color:RED}}}";
            controller.handleMessage(moveRedStudent, giuseCh);
        }

    }

}