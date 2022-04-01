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

    /**
     * Uses the correct effect of a {@code PassiveCharacter}
     *
     * @param currentPlayerActionPhase the {@code PlayerActionPhase} which the effect is used in
     * @param island                   the {@code Island} which the {@code Character} affects
     * @param color                    the {@code Color} which the {@code Character} affects
     * @param srcStudents              the students to be moved to the destination
     * @param dstStudents              the students to be moved to the source (only if the effect is a "swap" effect)
     * @throws InvalidCharacterException       if the {@code Character} is not a {@code PassiveCharacter}
     * @throws CharacterAlreadyPlayedException if the {@code Player} has already played a {@code Character} this turn
     */
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
