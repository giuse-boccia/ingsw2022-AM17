package it.polimi.ingsw.model;

import com.google.gson.Gson;
import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.server.game_state.SavedGameState;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.*;

public class PersistenceTest {
    /**
     * Loads a game state from file, then asserts the game was loaded correctly
     */
    @Test
    void loadGameStateTest() throws IOException {
        Gson gson = new Gson();
        Reader reader = Files.newBufferedReader(Paths.get("src/test/resources/exampleGames/game1.json"));
        SavedGameState loadedGameState = gson.fromJson(reader, SavedGameState.class);
        reader.close();

        //----------- GENERAL VALUES AND INDEXES ------------------
        assertTrue(loadedGameState.isExpert());
        assertEquals(10, loadedGameState.getMNIndex());
        assertEquals(0, loadedGameState.getRoundsPlayed());

        //--------------------------- BAG -------------------------
        assertEquals(96, loadedGameState.getBag().size());
        // students in bag order is the same (check only first 5 students
        assertEquals(Color.PINK, loadedGameState.getBag().get(0).getColor());
        assertEquals(Color.BLUE, loadedGameState.getBag().get(1).getColor());
        assertEquals(Color.PINK, loadedGameState.getBag().get(2).getColor());
        assertEquals(Color.YELLOW, loadedGameState.getBag().get(3).getColor());
        assertEquals(Color.RED, loadedGameState.getBag().get(4).getColor());

        //---------------------- ROUND STATE -----------------------
        assertFalse(loadedGameState.getRoundState().isLastRound());

    }

    /**
     * Loads a game state from file, then loads a game from that game state and asserts everything is correct
     */
    @Test
    void loadGameTest() throws IOException {
        Gson gson = new Gson();
        Reader reader = Files.newBufferedReader(Paths.get("src/test/resources/exampleGames/game1.json"));
        SavedGameState loadedGameState = gson.fromJson(reader, SavedGameState.class);
        reader.close();

        Game game = SavedGameState.loadGame(loadedGameState);

        // TODO: a bunch of assertions on the game
        // fare assertion tra loadedGameState e Game, non tra Game e costanti del file (è la stessa cosa ma rende meglio l'idea
    }

    /**
     * Loads a game (game1), then saves it, then loads it again (game2). Checks that nothing has changed
     */
    @Test
    void saveGameTest() throws IOException {
        Gson gson = new Gson();
        Reader reader = Files.newBufferedReader(Paths.get("src/test/resources/exampleGames/game1.json"));
        SavedGameState loadedGameState = gson.fromJson(reader, SavedGameState.class);
        reader.close();

        Game game1 = SavedGameState.loadGame(loadedGameState);
        game1.resume();
        SavedGameState tmp = new SavedGameState(game1);
        Game game2 = SavedGameState.loadGame(tmp);
        game2.resume();

        assertEquals(game1, game2);
    }
}
