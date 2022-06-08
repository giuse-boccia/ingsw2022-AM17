package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.constants.Messages;
import it.polimi.ingsw.exceptions.InvalidActionException;
import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.model.game_objects.gameboard_objects.GameBoard;
import it.polimi.ingsw.model.game_objects.gameboard_objects.Island;
import it.polimi.ingsw.model.game_actions.PlayerActionPhase;

import java.util.List;

public class NoEntryCharacter extends GameboardCharacter {
    private int noEntryNum;

    public NoEntryCharacter(CharacterName characterName, GameBoard gb) {
        super(characterName, gb);
        this.noEntryNum = 4;
    }

    public NoEntryCharacter(CharacterName characterName, GameBoard gb, boolean hasCoin, int noEntryNum) {
        super(characterName, gb, hasCoin);
        this.noEntryNum = noEntryNum;
    }

    /**
     * Adds a noEntry tile to the {@code Character}
     */
    public void addNoEntry() {
        noEntryNum++;
    }

    /**
     * Puts a noEntry tile on the selected {@code Island}
     *
     * @param currentPlayerActionPhase the {@code PlayerActionPhase} which the effect is used in
     * @param island                   the {@code Island} which the {@code Character} affects
     * @param color                    the {@code Color} which the {@code Character} affects
     * @param srcColors                the students to be moved to the destination
     * @param dstColors                the students to be moved to the source (only if the effect is a "swap" effect)
     * @throws InvalidActionException if there are no noEntry tiles on the {@code Character}
     */
    @Override
    public void useEffect(PlayerActionPhase currentPlayerActionPhase, Island island, Color color, List<Color> srcColors, List<Color> dstColors) throws InvalidActionException {
        removeNoEntry();
        island.increaseNoEntryNum();
    }

    /**
     * removes a noEntry tile from the {@code Character}
     *
     * @throws InvalidActionException if there are no noEntry tiles on the {@code Character}
     */
    private void removeNoEntry() throws InvalidActionException {
        if (noEntryNum == 0) throw new InvalidActionException(Messages.NO_NOENTRY);
        noEntryNum--;
    }

    public int getNoEntryNum() {
        return noEntryNum;
    }
}
