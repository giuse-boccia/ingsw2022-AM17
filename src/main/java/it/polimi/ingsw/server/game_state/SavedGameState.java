package it.polimi.ingsw.server.game_state;


import com.google.gson.Gson;
import it.polimi.ingsw.constants.Constants;
import it.polimi.ingsw.constants.Messages;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.game_actions.Round;
import it.polimi.ingsw.model.game_objects.Student;
import it.polimi.ingsw.model.game_objects.gameboard_objects.Bag;
import it.polimi.ingsw.model.game_objects.gameboard_objects.GameBoard;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * Immutable class that represents the current status of the game.
 * This is used for persistence
 */
public class SavedGameState extends GameState {
    private final int roundsPlayed;
    private final List<Student> bag;
    private final RoundState roundState;

    public SavedGameState(boolean isExpert, int MNIndex, List<PlayerState> players, List<IslandState> islands, List<CharacterState> characters, List<CloudState> clouds, int roundsPlayed, List<Student> bag, RoundState roundState) {
        super(isExpert, MNIndex, players, islands, characters, clouds);
        this.roundsPlayed = roundsPlayed;
        this.bag = bag;
        this.roundState = roundState;
    }

    public SavedGameState(Game game) {
        super(game);
        this.roundsPlayed = game.getRoundsPlayed();
        this.bag = game.getGameBoard().getBag().getStudents();
        this.roundState = new RoundState(game.getCurrentRound());
    }

    /**
     * Saves the game to disk
     */
    public static void saveToFile(Game game) {

        System.out.println(Messages.SAVING_TO_FILE);

        try {
            SavedGameState gs = new SavedGameState(game);
            Gson gson = new Gson();
            Writer writer = Files.newBufferedWriter(Paths.get(Constants.SAVED_GAME_PATH));
            gson.toJson(gs, writer);
            writer.close();

            System.out.println(Messages.SAVE_OK);
        } catch (Exception e) {
            System.out.println(Messages.SAVE_ERR);
        }
    }

    /**
     * Loads a Game from disk
     *
     * @return the loaded game
     */
    public static Game loadFromFile() throws IOException {
        SavedGameState savedGame;
        Gson gson = new Gson();
        Reader reader = Files.newBufferedReader(Paths.get(Constants.SAVED_GAME_PATH));
        savedGame = gson.fromJson(reader, SavedGameState.class);
        reader.close();

        // Create Game from saved game state
        List<Player> players = PlayerState.loadPlayers(savedGame);
        Game game = new Game(players, savedGame.isExpert());
        game.setRoundsPlayed(savedGame.roundsPlayed);
        // game.setGameBoard(loadGameBoard(savedGame, game));
        // game.setCurrentRound(new Round());

        return game;
    }

    /**
     * Loads a GameBoard object form a saved game, and assigns it to a Game
     *
     * @param savedGame the SavedGame from which to load the GameBoard
     * @param game      the Game to assign the new GameBoard to
     * @return the loaded GameBoard
     */
    public static GameBoard loadGameBoard(SavedGameState savedGame, Game game) {
        GameBoard gameBoard = new GameBoard(game);
        gameBoard.setBag(new Bag(savedGame.bag));
        gameBoard.setIslands(IslandState.loadIslands(savedGame));
        gameBoard.setClouds(CloudState.loadClouds(savedGame));

        // also load characters, professors map and MN index

        return gameBoard;
    }
}
