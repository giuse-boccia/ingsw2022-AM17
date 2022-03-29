package it.polimi.ingsw.model.character;

import it.polimi.ingsw.model.game_objects.GameBoard;

public abstract class GameboardCharacter extends Character {

    private final GameBoard gb;

    public GameboardCharacter(CharacterName characterName, GameBoard gb) {
        super(characterName);
        this.gb = gb;
    }

    public GameBoard getGameBoard() {
        return gb;
    }
}
