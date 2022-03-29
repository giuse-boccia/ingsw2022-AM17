package it.polimi.ingsw.model.character;

import it.polimi.ingsw.exceptions.InvalidCharacterException;
import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.model.game_objects.GameBoard;
import it.polimi.ingsw.model.game_objects.Island;
import it.polimi.ingsw.model.game_actions.action_phase.PlayerActionPhase;
import it.polimi.ingsw.model.game_objects.Student;

import java.util.ArrayList;

public class NoEntryCharacter extends GameboardCharacter {
    private int noEntryNum;

    public NoEntryCharacter(CharacterName characterName, GameBoard gb) {
        super(characterName, gb);
        noEntryNum = 4;
    }

    public NoEntryCharacter(CharacterName characterName, GameBoard gb, int noEntryNum) {
        super(characterName, gb);
        this.noEntryNum = noEntryNum;
    }

    public void addNoEntry() {
        noEntryNum++;
    }

    @Override
    public void useEffect(PlayerActionPhase currentPlayerActionPhase, Island island, Color color, ArrayList<Student> srcStudents, ArrayList<Student> dstStudents) throws InvalidCharacterException {
        removeNoEntry();
        island.increaseNoEntryNum();
        super.addCoinAfterFirstUse();
    }

    private void removeNoEntry() throws InvalidCharacterException {
        if (noEntryNum == 0) throw new InvalidCharacterException("There are no NoEntry pawns left on this card");
        noEntryNum--;
    }
}
