package it.polimi.ingsw.model.game_state;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.game_objects.Color;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Immutable class that represent the game as viewed by the clients
 */
public class GameState {
    private final boolean isExpert;
    private final int MNIndex;
    private final String[] professors;  // array of professors owner, if professors[i] is null then nobody owns the professor
    private final List<PlayerState> players;
    private final List<IslandState> islands;
    private final List<CharacterState> characters;
    private final List<CloudState> clouds;

    public GameState(boolean isExpert, int MNIndex, String[] professors, List<PlayerState> players, List<IslandState> islands, List<CharacterState> characters, List<CloudState> clouds) {
        this.isExpert = isExpert;
        this.MNIndex = MNIndex;
        this.professors = professors;
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
        this.isExpert = game.isExpert();
        this.MNIndex = game.getGameBoard().getMotherNatureIndex();

        this.professors = new String[5];
        for (int i = 0; i < Color.values().length; i++) {
            Player owner = game.getGameBoard().getOwnerOfProfessor(Color.values()[i]);
            professors[i] = (owner != null ? owner.getName() : null);
        }

        this.players = game.getPlayers().stream()
                .map(PlayerState::new)
                .toList();

        this.islands = game.getGameBoard().getIslands().stream()
                .map(IslandState::new)
                .toList();

        this.characters = Arrays.stream(game.getGameBoard().getCharacters())
                .map(CharacterState::new)
                .toList();

        this.clouds = game.getGameBoard().getClouds().stream()
                .map(CloudState::new)
                .toList();
    }

    public boolean isExpert() {
        return isExpert;
    }

    public int getMNIndex() {
        return MNIndex;
    }

    public String[] getProfessors() {
        return professors;
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
