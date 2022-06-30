package it.polimi.ingsw.server.game_state;


import com.google.gson.Gson;
import it.polimi.ingsw.languages.Messages;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.model.game_objects.Student;
import it.polimi.ingsw.model.game_objects.gameboard_objects.Bag;
import it.polimi.ingsw.model.game_objects.gameboard_objects.GameBoard;
import it.polimi.ingsw.utils.constants.Constants;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        System.out.println(Messages.getMessage("saving_to_file"));

        try {
            SavedGameState gs = new SavedGameState(game);
            Gson gson = new Gson();
            Writer writer = Files.newBufferedWriter(Paths.get(Constants.SAVED_GAME_PATH));
            gson.toJson(gs, writer);
            writer.close();

            System.out.println(Messages.getMessage("save_ok"));
        } catch (Exception e) {
            System.out.println(Messages.getMessage("save_err"));
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

        return loadGame(savedGame);
    }

    /**
     * Creates and returns a {@code Game} (complete model object) from the given savedGame
     *
     * @param savedGame a saved game
     * @return the loaded game
     */
    public static Game loadGame(SavedGameState savedGame) {
        List<Player> players = PlayerState.loadPlayers(savedGame);
        Game game = new Game(players, savedGame.isExpert());
        game.setRoundsPlayed(savedGame.roundsPlayed);
        game.setGameBoard(loadGameBoard(savedGame, game));
        game.setCurrentRound(RoundState.loadRound(savedGame, game));
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
        gameBoard.setMotherNatureIndex(savedGame.getMNIndex());
        gameBoard.setProfessors(loadProfessors(savedGame.getPlayers(), game.getPlayers()));
        gameBoard.setCharacters(CharacterState.loadCharacters(savedGame, gameBoard));

        return gameBoard;
    }

    /**
     * Infers a professor map from a list of Player states.
     * Assumes the list is correct: a professor is not owned by more than one player
     *
     * @param playerStates a list of player states from a loaded game
     * @param players      a list of players from the game
     * @return an object which maps each color with the player who owns the professor of that color
     */
    private static Map<Color, Player> loadProfessors(List<PlayerState> playerStates, ArrayList<Player> players) {
        Map<Color, Player> res = new HashMap<>();
        for (Color color : Color.values()) {
            res.put(color, null);
        }

        for (PlayerState playerState : playerStates) {
            // Get corresponding Player (game model object)
            Player player = players.stream()
                    .filter(p -> p.getName().equals(playerState.getName()))
                    .findFirst()
                    .orElseThrow();
            for (Color ownedProf : playerState.getOwnedProfessors()) {
                res.put(ownedProf, player);
            }
        }

        return res;
    }

    public int getRoundsPlayed() {
        return roundsPlayed;
    }

    public List<Student> getBag() {
        return bag;
    }

    public RoundState getRoundState() {
        return roundState;
    }
}
