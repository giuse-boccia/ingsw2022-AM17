package it.polimi.ingsw.model;

import com.google.gson.Gson;
import it.polimi.ingsw.languages.MessageResourceBundle;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.utils.constants.Constants;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.model.characters.MovingCharacter;
import it.polimi.ingsw.model.game_actions.Round;
import it.polimi.ingsw.model.game_objects.Assistant;
import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.model.game_objects.gameboard_objects.Cloud;
import it.polimi.ingsw.model.game_objects.gameboard_objects.Island;
import it.polimi.ingsw.server.game_state.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class PersistenceTest {

    @BeforeAll
    static void initializeMessageResourceBundle() {
        MessageResourceBundle.initializeBundle("en");
    }

    /**
     * Loads a game state from a file, then loads a game from that game state and tests that everything is correct
     * by making assertions between the gameState and the game
     */
    @Test
    void loadGameTest() throws IOException, ClassNotFoundException {
        // load gameState from file
        Gson gson = new Gson();
        Reader reader = Files.newBufferedReader(Paths.get("src/test/resources/exampleGames/game1.json"));
        // TODO: add more saved games in exampleGames/ and repeat the test with other games
        SavedGameState gameState = gson.fromJson(reader, SavedGameState.class);
        reader.close();

        // load game (model object) from gameState
        Game game = SavedGameState.loadGame(gameState);
        game.resume();      // bind game to players

        // assert game has been loaded correctly
        assertGameWasLoadedCorrectly(gameState, game);
    }

    /**
     * Loads a game (game1), then saves it, then loads it again (game2). Checks that nothing has changed
     */
    @Test
    void saveGameTest() throws IOException, ClassNotFoundException {
        Gson gson = new Gson();
        Reader reader = Files.newBufferedReader(Paths.get("src/test/resources/exampleGames/game1.json"));
        SavedGameState loadedGameState = gson.fromJson(reader, SavedGameState.class);
        reader.close();

        Game game1 = SavedGameState.loadGame(loadedGameState);
        game1.resume();
        assertGameWasLoadedCorrectly(loadedGameState, game1);

        SavedGameState tmp = new SavedGameState(game1);
        Game game2 = SavedGameState.loadGame(tmp);
        game2.resume();
        assertGameWasLoadedCorrectly(tmp, game2);

        assertEquals(game1, game2);
    }

    /**
     * Assert that the given game is a correct representation of the given gameState
     *
     * @param gameState a SavedGameState (representation of a JSON save file)
     * @param game      a Game (model object)
     */
    private void assertGameWasLoadedCorrectly(SavedGameState gameState, Game game) throws ClassNotFoundException {
        // general game parameters
        assertEquals(gameState.isExpert(), game.isExpert());
        assertEquals(gameState.getRoundsPlayed(), game.getRoundsPlayed());
        assertEquals(gameState.getMNIndex(), game.getGameBoard().getMotherNatureIndex());
        assertEquals(gameState.getBag(), game.getGameBoard().getBag().getStudents());

        // assert the current round has been loaded correctly
        RoundState roundState = gameState.getRoundState();
        Round round = game.getCurrentRound();
        assertRoundWasLoadedCorrectly(roundState, round);

        // assert professors have been loaded correctly
        for (Color professor : Color.values()) {
            Player owner = game.getGameBoard().getOwnerOfProfessor(professor);
            if (owner != null) {
                String ownerName = owner.getName();
                assertTrue(gameState.getPlayers().stream()
                        .filter(playerState -> playerState.getName().equals(ownerName))
                        .findFirst().orElseThrow()
                        .getOwnedProfessors()
                        .contains(professor));
            }
        }

        // assert players have been loaded correctly (and order is maintained)
        int numberOfPlayers = game.getPlayers().size();
        for (int i = 0; i < numberOfPlayers; i++) {
            PlayerState playerState = gameState.getPlayers().get(i);    // the player state from the saved JSON
            Player player = game.getPlayers().get(i);                   // the loaded player (model object)

            assertPlayerWasLoadedCorrectly(playerState, player);
        }

        // assert islands have been loaded correctly (and order is maintained)
        int numberOfIslands = game.getGameBoard().getIslands().size();
        for (int i = 0; i < numberOfIslands; i++) {
            IslandState islandState = gameState.getIslands().get(i);    // the island state from the saved JSON
            Island island = game.getGameBoard().getIslands().get(i);    // the loaded island (model object)

            assertIslandWasLoadedCorrectly(islandState, island);
        }

        // assert clouds have been loaded correctly
        int numberOfClouds = game.getGameBoard().getClouds().size();
        for (int i = 0; i < numberOfClouds; i++) {
            CloudState cloudState = gameState.getClouds().get(i);   // the cloud state from the saved JSON
            Cloud cloud = game.getGameBoard().getClouds().get(i);   // the loaded cloud (model object)

            assertCloudWasLoadedCorrectly(cloudState, cloud);
        }

        // assert characters have been loaded correctly
        for (int i = 0; i < Constants.NUM_CHARACTERS; i++) {
            CharacterState characterState = gameState.getCharacters().get(i);
            Character character = game.getGameBoard().getCharacters()[i];

            assertCharacterWasLoadedCorrectly(characterState, character);
        }
    }

    /**
     * Assert that the given round is a correct representation of the given roundState
     */
    private void assertRoundWasLoadedCorrectly(RoundState roundState, Round round) {
        assertEquals(roundState.isLastRound(), round.isLastRound());
        assertEquals(roundState.getFirstPlayerIndex(), round.getFirstPlayerIndex());
        assertEquals(roundState.getCurrentAssistantIndex(), round.getCurrentAssistantIndex());
        if (roundState.getPlayedAssistants() == null) {
            // game was saved at the beginning of the planning phase: assistantPlayed should be null
            assertNull(round.getPlayedAssistants());
        } else {
            // game was saved at the beginning of a player action phase: check assistant played
            int numberOfAssistantsPlayed = roundState.getPlayedAssistants().size();
            assertEquals(numberOfAssistantsPlayed, round.getPlayedAssistants().size());
            for (int i = 0; i < numberOfAssistantsPlayed; i++) {
                AssistantState assistantState = roundState.getPlayedAssistants().get(i);
                Assistant assistant = round.getPlayedAssistants().get(i);
                assertEquals(assistantState.getValue(), assistant.getValue());
                assertEquals(assistantState.getPlayerName(), assistant.getPlayer().getName());
            }
        }
    }

    /**
     * Assert that the given player is a correct representation of the given playerState
     */
    private void assertPlayerWasLoadedCorrectly(PlayerState playerState, Player player) {
        assertEquals(playerState.getName(), player.getName());
        assertEquals(playerState.getOwnedProfessors(), player.getColorsOfOwnedProfessors());
        assertEquals(playerState.getNumCoins(), player.getNumCoins());
        assertEquals(playerState.getEntrance(), player.getDashboard().getEntrance().getStudents());
        assertEquals(playerState.getDining(), player.getDashboard().getDiningRoom().getStudents());
        assertEquals(playerState.getTowerColor(), player.getTowerColor());
        assertEquals(playerState.getRemainingTowers(), player.getRemainingTowers());

        // assert player has each and only assistant from the player state
        int[] handState = playerState.getAssistants();
        Assistant[] hand = player.getHand();
        for (int j = 1; j < Constants.MAX_ASSISTANT_VALUE; j++) {
            int assistantValue = j;
            boolean playerStateHasAssistant = Arrays.stream(handState).anyMatch(a -> a == assistantValue);
            boolean playerHasAssistant = Arrays.stream(hand).anyMatch(a -> a != null && a.getValue() == assistantValue);
            assertEquals(playerStateHasAssistant, playerHasAssistant);
        }
    }

    /**
     * Assert that the given island is a correct representation of the given islandState
     */
    private void assertIslandWasLoadedCorrectly(IslandState islandState, Island island) {
        assertEquals(islandState.getStudents(), island.getStudents());
        assertEquals(islandState.getTowerColor(), island.getTowerColor());
        assertEquals(islandState.getNumOfTowers(), island.getNumOfTowers());
        assertEquals(islandState.getNoEntryNum(), island.getNoEntryNum());
    }

    /**
     * Assert that the given cloud is a correct representation of the given cloudState
     */
    private void assertCloudWasLoadedCorrectly(CloudState cloudState, Cloud cloud) {
        assertEquals(cloudState.getStudents(), cloud.getStudents());
        assertEquals(cloudState.getMaxStudents(), cloud.getMaxStudents());
    }

    /**
     * Assert that the given character is a correct representation of the characterState
     */
    private void assertCharacterWasLoadedCorrectly(CharacterState characterState, Character character) throws ClassNotFoundException {
        assertEquals(characterState.getCharacterName(), character.getCardName());
        assertEquals(characterState.getCost(), character.getCost());
        assertEquals(characterState.hasCoin(), character.hasCoin());
        // If the character is a GameBoardCharacter check students
        if (character.getClass().isAssignableFrom(Class.forName("it.polimi.ingsw.model.characters.MovingCharacter"))) {
            assertEquals(characterState.getStudents(), ((MovingCharacter) character).getStudents());
        }
    }
}
