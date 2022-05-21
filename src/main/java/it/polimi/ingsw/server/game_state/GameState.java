package it.polimi.ingsw.server.game_state;

import it.polimi.ingsw.model.Game;

import java.util.Arrays;
import java.util.List;

/**
 * Immutable class that represent the game as viewed by the clients
 */
public class GameState {
    private final boolean isExpert;
    private final int MNIndex;
    private final List<PlayerState> players;
    private final List<IslandState> islands;
    private final List<CharacterState> characters;
    private final List<CloudState> clouds;

    public GameState(boolean isExpert, int MNIndex, List<PlayerState> players, List<IslandState> islands, List<CharacterState> characters, List<CloudState> clouds) {
        this.isExpert = isExpert;
        this.MNIndex = MNIndex;
        this.players = players;
        this.islands = islands;
        this.characters = characters;
        this.clouds = clouds;
    }

    /**
     * Creates a new GameState from a Game, including islands, clouds, players and characters
     *
     * @param game the game to create a GameState from
     */
    public GameState(Game game) {
        this(
                game.isExpert(),
                game.getGameBoard().getMotherNatureIndex(),
                game.getPlayers().stream()
                        .map(PlayerState::new)
                        .toList(),
                game.getGameBoard().getIslands().stream()
                        .map(IslandState::new)
                        .toList(),
                Arrays.stream(game.getGameBoard().getCharacters())
                        .map(CharacterState::new)
                        .toList(),
                game.getGameBoard().getClouds().stream()
                        .map(CloudState::new)
                        .toList()
        );

    }

    public boolean isExpert() {
        return isExpert;
    }

    public int getMNIndex() {
        return MNIndex;
    }

    public List<PlayerState> getPlayers() {
        return players;
    }

    public List<IslandState> getIslands() {
        return islands;
    }

    public List<CharacterState> getCharacters() {
        return characters;
    }

    public List<CloudState> getClouds() {
        return clouds;
    }
}
