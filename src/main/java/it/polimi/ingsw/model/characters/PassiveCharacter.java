package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.InvalidActionException;
import it.polimi.ingsw.exceptions.InvalidCharacterException;
import it.polimi.ingsw.languages.Messages;
import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.model.game_actions.PlayerActionPhase;
import it.polimi.ingsw.model.game_objects.gameboard_objects.Island;

import java.util.List;

public class PassiveCharacter extends Character {

    public PassiveCharacter(CharacterName characterName) {
        super(characterName);
    }

    public PassiveCharacter(CharacterName characterName, boolean hasCoin) {
        super(characterName, hasCoin);
    }

    /**
     * Uses the correct effect of a {@code PassiveCharacter}
     *
     * @param currentPlayerActionPhase the {@code PlayerActionPhase} which the effect is used in
     * @param island                   the {@code Island} which the {@code Character} affects
     * @param color                    the {@code Color} which the {@code Character} affects
     * @param srcColors                the students to be moved to the destination
     * @param dstColors                the students to be moved to the source (only if the effect is a "swap" effect)
     * @throws InvalidCharacterException if the {@code Character} is not a {@code PassiveCharacter}
     * @throws InvalidActionException    if the selected {@code Color} is null
     */
    @Override
    public void useEffect(PlayerActionPhase currentPlayerActionPhase, Island island, Color color, List<Color> srcColors, List<Color> dstColors) throws InvalidCharacterException, InvalidActionException {

        switch (this.getCardName()) {
            case plus2MNMoves, takeProfWithEqualStudents, plus2Influence, ignoreTowers -> currentPlayerActionPhase.playPassiveCharacter(this);
            case ignoreColor -> {
                if (color == null) {
                    throw new InvalidActionException("invalid_argument");
                }
                currentPlayerActionPhase.playPassiveCharacterWithColor(color);
            }
            default -> throw new InvalidCharacterException("not_passive");

        }

    }
}
