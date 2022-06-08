package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.game_objects.gameboard_objects.GameBoard;

/**
 * A subclass of {@code Character} which has a reference to the {@code GameBoard}
 */
public abstract class GameboardCharacter extends Character {

    private final GameBoard gb;

    public GameboardCharacter(CharacterName characterName, GameBoard gb) {
        super(characterName);
        this.gb = gb;
    }

    public GameboardCharacter(CharacterName characterName, GameBoard gb, boolean hasCoin) {
        super(characterName, hasCoin);
        this.gb = gb;
    }

    public GameBoard getGameBoard() {
        return gb;
    }
}
