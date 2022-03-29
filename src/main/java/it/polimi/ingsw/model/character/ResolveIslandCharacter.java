package it.polimi.ingsw.model.character;

import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.model.game_objects.GameBoard;
import it.polimi.ingsw.model.game_objects.Island;
import it.polimi.ingsw.model.game_actions.action_phase.PlayerActionPhase;
import it.polimi.ingsw.model.game_objects.Student;

import java.util.ArrayList;

public class ResolveIslandCharacter extends GameboardCharacter {

    public ResolveIslandCharacter(CharacterName characterName, GameBoard gb) {
        super(characterName, gb);
    }

    @Override
    public void useEffect(PlayerActionPhase currentPlayerActionPhase, Island island, Color color, ArrayList<Student> srcStudents, ArrayList<Student> dstStudents) {
        currentPlayerActionPhase.resolveIsland(island);
        super.addCoinAfterFirstUse();
    }
}
