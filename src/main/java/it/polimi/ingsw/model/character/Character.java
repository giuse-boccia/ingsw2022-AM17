package it.polimi.ingsw.model.character;

import it.polimi.ingsw.exceptions.CharacterAlreadyPlayedException;
import it.polimi.ingsw.exceptions.InvalidCharacterException;
import it.polimi.ingsw.model.game_actions.action_phase.PlayerActionPhase;
import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.model.game_objects.Island;
import it.polimi.ingsw.model.game_objects.Student;

import java.util.ArrayList;

public abstract class Character {

    private final int cost;
    private final CharacterName characterName;
    private boolean hasCoin;

    public Character(CharacterName characterName) {
        this.characterName = characterName;
        this.cost = characterName.getInitialCost();
        this.hasCoin = false;
    }

    public int getCost() {
        return hasCoin ? cost + 1 : cost;
    }

    public CharacterName getCardName() {
        return characterName;
    }

    protected void addCoinAfterFirstUse() {
        hasCoin = true;
    }

    public abstract void useEffect(
            PlayerActionPhase currentPlayerActionPhase, Island island, Color color,
            ArrayList<Student> srcStudents, ArrayList<Student> dstStudents
    ) throws InvalidCharacterException, CharacterAlreadyPlayedException;
}
