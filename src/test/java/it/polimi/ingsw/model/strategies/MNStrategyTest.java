package it.polimi.ingsw.model.strategies;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.TestGameFactory;
import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.model.characters.PassiveCharacter;
import it.polimi.ingsw.model.game_actions.action_phase.PlayerActionPhase;
import it.polimi.ingsw.model.game_actions.action_phase.PlayerActionPhaseFactory;
import it.polimi.ingsw.model.game_objects.Assistant;
import it.polimi.ingsw.model.game_objects.GameBoard;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MNStrategyTest {
    Game g = TestGameFactory.getNewGame();
    GameBoard gb = g.getGameBoard();

    /**
     * Tests the default strategy to calculate the number of max steps MotherNature can do
     */
    @Test
    public void mnDefaultTest() {
        Player rick = g.getPlayers().get(0);        // Rick is player 0
        Player clod = g.getPlayers().get(1);        // Clod is player 1

        // Rick's turn, he played the lion assistant
        Assistant lion = new Assistant(1, 1, rick);
        PlayerActionPhase pap = PlayerActionPhaseFactory.createPlayerActionPhase(lion, gb, true);

        assertEquals(1, pap.getMNMaxSteps());

        // Clod's turn, he played the turtle assistant
        Assistant turtle = new Assistant(5, 10, clod);
        pap = PlayerActionPhaseFactory.createPlayerActionPhase(turtle, gb, true);

        assertEquals(5, pap.getMNMaxSteps());
    }

    /**
     * Tests the strategy to calculate the number of max steps MotherNature can do when the effect of the {@code Character}
     * called "plus2MNMoves" is active
     */
    @Test
    public void mnBonusTest1() {
        Player rick = g.getPlayers().get(0);        // Rick is player 0
        Player clod = g.getPlayers().get(1);        // Clod is player 1

        // Rick's turn, he played the lion assistant
        Assistant lion = new Assistant(1, 1, rick);
        PlayerActionPhase pap = PlayerActionPhaseFactory.createPlayerActionPhase(lion, gb, true);

        assertEquals(1, pap.getMNMaxSteps());

        // Clod's turn, he played the turtle assistant
        Assistant turtle = new Assistant(5, 10, clod);
        pap = PlayerActionPhaseFactory.createPlayerActionPhase(turtle, gb, true);

        assertEquals(5, pap.getMNMaxSteps());

        // Now clod plays the plus2MNMoves character
        PassiveCharacter pc = new PassiveCharacter(CharacterName.plus2MNMoves);
        pap.playPassiveCharacter(pc);

        assertEquals(7, pap.getMNMaxSteps());       // 5+2 = 7 max moves
    }

    /**
     * Tests the strategy to calculate the number of max steps MotherNature can do when the effect of the {@code Character}
     * called "plus2MNMoves" is active
     */
    @Test
    public void MNBonusTest2() {
        Player rick = g.getPlayers().get(0);        // Rick is player 0
        Player clod = g.getPlayers().get(1);        // Clod is player 1

        // Rick's turn, he played the lion assistant
        Assistant lion = new Assistant(1, 1, rick);
        PlayerActionPhase pap = PlayerActionPhaseFactory.createPlayerActionPhase(lion, gb, true);

        assertEquals(1, pap.getMNMaxSteps());

        // Now Rick plays the plus2MNMoves character
        PassiveCharacter pc = new PassiveCharacter(CharacterName.plus2MNMoves);
        pap.playPassiveCharacter(pc);

        assertEquals(3, pap.getMNMaxSteps());   // 1+2 = 3 max moves

        // Clod's turn, he played the turtle assistant
        Assistant turtle = new Assistant(5, 10, clod);
        pap = PlayerActionPhaseFactory.createPlayerActionPhase(turtle, gb, true);

        assertEquals(5, pap.getMNMaxSteps());   // Rick's bonus doesn't apply to Clod
    }
}