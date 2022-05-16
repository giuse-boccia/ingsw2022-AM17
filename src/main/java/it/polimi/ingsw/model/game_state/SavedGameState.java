package it.polimi.ingsw.model.game_state;


import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.game_objects.Student;

import java.util.List;

/**
 * Immutable class that represents the current status of the game.
 * This is used for persistence
 */
public class SavedGameState extends GameState {
    private final List<Student> bag;
    private final RoundState roundState;

    public SavedGameState(boolean isExpert, int MNIndex, String[] professors, List<PlayerState> players, List<IslandState> islands, List<CharacterState> characters, List<CloudState> clouds, List<Student> bag, RoundState roundState) {
        super(isExpert, MNIndex, players, islands, characters, clouds);
        this.bag = bag;
        this.roundState = roundState;
    }

    public SavedGameState(Game game) {
        super(game);
        this.bag = game.getGameBoard().getBag().getStudents();
        this.roundState = null;     //TODO fix
    }
}
