package it.polimi.ingsw.server.game_state;


import com.google.gson.Gson;
import it.polimi.ingsw.constants.Constants;
import it.polimi.ingsw.constants.Messages;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.game_objects.Student;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/**
 * Immutable class that represents the current status of the game.
 * This is used for persistence
 */
public class SavedGameState extends GameState {
    private final List<Student> bag;
    private final RoundState roundState;

    public SavedGameState(boolean isExpert, int MNIndex, List<PlayerState> players, List<IslandState> islands, List<CharacterState> characters, List<CloudState> clouds, List<Student> bag, RoundState roundState) {
        super(isExpert, MNIndex, players, islands, characters, clouds);
        this.bag = bag;
        this.roundState = roundState;
    }

    public SavedGameState(Game game) {
        super(game);
        this.bag = game.getGameBoard().getBag().getStudents();
        this.roundState = new RoundState(game.getCurrentRound());
    }

    /**
     * Saves the game to disk
     */
    public static void saveToFile(Game game) {
        try {
            GameState gs = new SavedGameState(game);
            Gson gson = new Gson();
            Writer writer = Files.newBufferedWriter(Paths.get(Constants.SAVED_GAME_PATH));
            gson.toJson(gs, writer);
            writer.close();

            System.out.println(Messages.SAVE_OK);
        } catch (IOException e) {
            System.out.println(Messages.SAVE_ERR);
        }
    }


}
