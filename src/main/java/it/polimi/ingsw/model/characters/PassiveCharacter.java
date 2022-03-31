package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.CharacterAlreadyPlayedException;
import it.polimi.ingsw.exceptions.InvalidCharacterException;
import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.model.game_actions.action_phase.PlayerActionPhase;
import it.polimi.ingsw.model.game_objects.Island;
import it.polimi.ingsw.model.game_objects.Student;

import java.util.ArrayList;

public class PassiveCharacter extends Character {

    public PassiveCharacter(CharacterName characterName) {
        super(characterName);
    }

    @Override
    public void useEffect(PlayerActionPhase currentPlayerActionPhase, Island island, Color color, ArrayList<Student> srcStudents, ArrayList<Student> dstStudents) throws InvalidCharacterException, CharacterAlreadyPlayedException {

        switch (this.getCardName()) {
            case plus2MNMoves, takeProfWithEqualStudents, plus2Influence, ignoreTowers -> {
                currentPlayerActionPhase.playPassiveCharacter(this);
            }
            case ignoreColor -> {
                currentPlayerActionPhase.playPassiveCharacterWithColor(color);
            }
            default -> {
                throw new InvalidCharacterException("This is not a passive character");
            }
        }

        super.addCoinAfterFirstUse();
    }
}
