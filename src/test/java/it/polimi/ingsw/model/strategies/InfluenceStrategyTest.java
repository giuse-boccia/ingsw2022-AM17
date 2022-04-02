package it.polimi.ingsw.model.strategies;

import it.polimi.ingsw.exceptions.ProfessorAlreadyPresentException;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.TestGameFactory;
import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.model.characters.PassiveCharacter;
import it.polimi.ingsw.model.game_actions.action_phase.PlayerActionPhase;
import it.polimi.ingsw.model.game_actions.action_phase.PlayerActionPhaseFactory;
import it.polimi.ingsw.model.game_objects.*;
import it.polimi.ingsw.model.game_objects.gameboard_objects.GameBoard;
import it.polimi.ingsw.model.game_objects.gameboard_objects.Island;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InfluenceStrategyTest {
    Game g = TestGameFactory.getNewGame();
    GameBoard gb = g.getGameBoard();

    /**
     * Tests the default strategy to calculate the influence on an {@code Island} when a {@code Player} has an advantage
     * against another one
     */
    @Test
    public void influenceDefaultTest1() throws ProfessorAlreadyPresentException {
        Player rick = g.getPlayers().get(0);        // Rick is player 0
        Player clod = g.getPlayers().get(1);        // Clod is player 1

        // Rick has green professor
        rick.getDashboard().getProfessorRoom().takeProfessor(new Professor(Color.GREEN));

        // Clod has red professor
        clod.getDashboard().getProfessorRoom().takeProfessor(new Professor(Color.RED));

        // island with 1 green student
        Island island = new Island();
        island.receiveStudent(new Student(Color.GREEN));

        //-----------------------Rick's turn--------------------------------------------
        Assistant a1 = new Assistant(1, 1, rick);
        PlayerActionPhase pap = PlayerActionPhaseFactory.createPlayerActionPhase(a1, gb, true);

        // Island is resolved, rick should own the island
        pap.resolveIsland(island);
        assertSame(island.getOwner(), rick);

        //-----------------------Clod's turn--------------------------------------------
        Assistant a2 = new Assistant(1, 2, clod);
        pap = PlayerActionPhaseFactory.createPlayerActionPhase(a2, gb, true);

        // add 2 red to island and solve -> rick should still own the island
        island.receiveStudent(new Student(Color.RED));
        island.receiveStudent(new Student(Color.RED));
        pap.resolveIsland(island);
        assertSame(island.getOwner(), rick);

        // add a third red student and solve -> clod now owns the island
        island.receiveStudent(new Student(Color.RED));
        pap.resolveIsland(island);
        assertSame(island.getOwner(), clod);
    }

    /**
     * Tests the default strategy to calculate the influence on an {@code Island} when no {@code Player} has an advantage
     * against another one
     */
    @Test
    public void influenceDefaultTest2() throws ProfessorAlreadyPresentException {
        Player rick = g.getPlayers().get(0);        // Rick is player 0
        Player clod = g.getPlayers().get(1);        // Clod is player 1

        // Rick has green professor
        rick.getDashboard().getProfessorRoom().takeProfessor(new Professor(Color.GREEN));

        // Clod has red professor
        clod.getDashboard().getProfessorRoom().takeProfessor(new Professor(Color.RED));

        // island with 1 red student and 1 green student
        Island island = new Island();
        island.receiveStudent(new Student(Color.RED));
        island.receiveStudent(new Student(Color.GREEN));

        // Rick's turn
        Assistant lion = new Assistant(1, 1, rick);
        PlayerActionPhase pap = PlayerActionPhaseFactory.createPlayerActionPhase(lion, gb, true);

        // Island is resolved, nobody should own the island
        pap.resolveIsland(island);
        assertNull(island.getOwner());
    }

    /**
     * Tests the strategy to calculate the influence on an {@code Island} when the effect of the {@code Character}
     * called "plus2Influence" is active
     */
    @Test
    public void influenceBonusTest() throws ProfessorAlreadyPresentException {
        Player rick = g.getPlayers().get(0);        // Rick is player 0
        Player clod = g.getPlayers().get(1);        // Clod is player 1

        // Rick has green professor
        rick.getDashboard().getProfessorRoom().takeProfessor(new Professor(Color.GREEN));

        // Clod has red professor
        clod.getDashboard().getProfessorRoom().takeProfessor(new Professor(Color.RED));

        // island with 1 green student and 2 red students
        Island island = new Island();
        island.receiveStudent(new Student(Color.GREEN));
        island.receiveStudent(new Student(Color.RED));
        island.receiveStudent(new Student(Color.RED));

        //-----------------------Rick's turn--------------------------------------------
        Assistant a1 = new Assistant(1, 1, rick);
        PlayerActionPhase pap = PlayerActionPhaseFactory.createPlayerActionPhase(a1, gb, true);

        // Rick plays plus2Influence character
        PassiveCharacter pc = new PassiveCharacter(CharacterName.plus2Influence);
        pap.playPassiveCharacter(pc);

        // Island is resolved, rick should own the island
        pap.resolveIsland(island);
        assertSame(island.getOwner(), rick);
    }

    /**
     * Tests the strategy to calculate the influence on an {@code Island} when the effect of the {@code Character}
     * called "ignoreColor" is active
     */
    @Test
    public void influenceIgnoreColorTest() throws ProfessorAlreadyPresentException {
        Player rick = g.getPlayers().get(0);        // Rick is player 0
        Player clod = g.getPlayers().get(1);        // Clod is player 1

        // Rick has green professor
        rick.getDashboard().getProfessorRoom().takeProfessor(new Professor(Color.GREEN));

        // Clod has red professor
        clod.getDashboard().getProfessorRoom().takeProfessor(new Professor(Color.RED));

        // island with 3 green student and 2 red students
        Island island = new Island();
        island.receiveStudent(new Student(Color.GREEN));
        island.receiveStudent(new Student(Color.GREEN));
        island.receiveStudent(new Student(Color.GREEN));
        island.receiveStudent(new Student(Color.RED));
        island.receiveStudent(new Student(Color.RED));

        //-----------------------Rick's turn--------------------------------------------
        Assistant a1 = new Assistant(1, 1, rick);
        PlayerActionPhase pap = PlayerActionPhaseFactory.createPlayerActionPhase(a1, gb, true);

        // Island is resolved, rick should own the island
        pap.resolveIsland(island);
        assertSame(island.getOwner(), rick);

        //-----------------------Clod's turn--------------------------------------------
        Assistant a2 = new Assistant(1, 2, clod);
        pap = PlayerActionPhaseFactory.createPlayerActionPhase(a2, gb, true);

        // Clod plays the ignoreColor character
        pap.playPassiveCharacterWithColor(Color.GREEN);

        // solve island again -> clod will now own the island
        pap.resolveIsland(island);
        assertSame(island.getOwner(), clod);
    }

    /**
     * Tests the strategy to calculate the influence on an {@code Island} when the effect of the {@code Character}
     * called "ignoreTowers" is active
     */
    @Test
    public void influenceIgnoreTowersTest1() throws ProfessorAlreadyPresentException {
        Player rick = g.getPlayers().get(0);        // Rick is player 0
        Player clod = g.getPlayers().get(1);        // Clod is player 1

        // Rick has green professor
        rick.getDashboard().getProfessorRoom().takeProfessor(new Professor(Color.GREEN));

        // Clod has red professor
        clod.getDashboard().getProfessorRoom().takeProfessor(new Professor(Color.RED));

        // island with 1 green student
        Island island = new Island();
        island.receiveStudent(new Student(Color.GREEN));

        //-----------------------Rick's turn--------------------------------------------
        Assistant a1 = new Assistant(1, 1, rick);
        PlayerActionPhase pap = PlayerActionPhaseFactory.createPlayerActionPhase(a1, gb, true);

        // Island is resolved, rick should own the island
        pap.resolveIsland(island);
        assertSame(island.getOwner(), rick);

        //-----------------------Clod's turn--------------------------------------------
        Assistant a2 = new Assistant(1, 2, clod);
        pap = PlayerActionPhaseFactory.createPlayerActionPhase(a2, gb, true);

        // Clod plays the ignoreTowers character
        PassiveCharacter pc = new PassiveCharacter(CharacterName.ignoreTowers);
        pap.playPassiveCharacter(pc);

        // add 2 red to island and solve -> clod will now own the island
        island.receiveStudent(new Student(Color.RED));
        island.receiveStudent(new Student(Color.RED));
        pap.resolveIsland(island);
        assertSame(island.getOwner(), clod);

    }
}