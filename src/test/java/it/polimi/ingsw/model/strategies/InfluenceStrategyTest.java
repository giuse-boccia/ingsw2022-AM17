package it.polimi.ingsw.model.strategies;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.TestGameFactory;
import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.model.characters.PassiveCharacter;
import it.polimi.ingsw.model.game_actions.PlayerActionPhase;
import it.polimi.ingsw.model.game_objects.*;
import it.polimi.ingsw.model.game_objects.gameboard_objects.GameBoard;
import it.polimi.ingsw.model.game_objects.gameboard_objects.Island;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class InfluenceStrategyTest {
    Game g = TestGameFactory.getNewGame();
    Game g2 = TestGameFactory.getNewFourPlayersGame();
    GameBoard gb = g.getGameBoard();

    /**
     * Tests the default strategy to calculate the influence on an {@code Island} when a {@code Player} has an advantage
     * against another one
     */
    @Test
    public void influenceDefaultTest1() {
        Player rick = g.getPlayers().get(0);        // Rick is player 0
        Player clod = g.getPlayers().get(1);        // Clod is player 1

        // Rick has green professor
        gb.setOwnerOfProfessor(Color.GREEN, rick);

        // Clod has red professor
        gb.setOwnerOfProfessor(Color.RED, clod);

        // island with 1 green student
        Island island = new Island();
        island.receiveStudent(new Student(Color.GREEN));

        //-----------------------Rick's turn--------------------------------------------
        Assistant a1 = new Assistant(1, 1, rick);
        PlayerActionPhase pap = new PlayerActionPhase(a1, gb);

        // Island is resolved, rick should own the island
        pap.resolveIsland(island);
        assertSame(island.getTowerColor(), rick.getTowerColor());

        //-----------------------Clod's turn--------------------------------------------
        Assistant a2 = new Assistant(1, 2, clod);
        pap = new PlayerActionPhase(a2, gb);

        // add 2 red to island and solve -> rick should still own the island
        island.receiveStudent(new Student(Color.RED));
        island.receiveStudent(new Student(Color.RED));
        pap.resolveIsland(island);
        assertSame(island.getTowerColor(), rick.getTowerColor());

        // add a third red student and solve -> clod now owns the island
        island.receiveStudent(new Student(Color.RED));
        pap.resolveIsland(island);
        assertSame(island.getTowerColor(), clod.getTowerColor());
    }

    /**
     * Tests the default strategy to calculate the influence on an {@code Island} when no {@code Player} has an advantage
     * against another one
     */
    @Test
    public void influenceDefaultTest2() {
        Player rick = g.getPlayers().get(0);        // Rick is player 0
        Player clod = g.getPlayers().get(1);        // Clod is player 1

        // Rick has green professor
        gb.setOwnerOfProfessor(Color.GREEN, rick);

        // Clod has red professor
        gb.setOwnerOfProfessor(Color.RED, clod);

        // island with 1 red student and 1 green student
        Island island = new Island();
        island.receiveStudent(new Student(Color.RED));
        island.receiveStudent(new Student(Color.GREEN));

        // Rick's turn
        Assistant lion = new Assistant(1, 1, rick);
        PlayerActionPhase pap = new PlayerActionPhase(lion, gb);

        // Island is resolved, nobody should own the island
        pap.resolveIsland(island);
        assertNull(island.getTowerColor());
    }

    /**
     * Tests the strategy to calculate the influence on an {@code Island} when the effect of the {@code Character}
     * called "plus2Influence" is active
     */
    @Test
    public void influenceBonusTest() {
        Player rick = g.getPlayers().get(0);        // Rick is player 0
        Player clod = g.getPlayers().get(1);        // Clod is player 1

        // Rick has green professor
        gb.setOwnerOfProfessor(Color.GREEN, rick);

        // Clod has red professor
        gb.setOwnerOfProfessor(Color.RED, clod);

        // island with 1 green student and 2 red students
        Island island = new Island();
        island.receiveStudent(new Student(Color.GREEN));
        island.receiveStudent(new Student(Color.RED));
        island.receiveStudent(new Student(Color.RED));

        //-----------------------Rick's turn--------------------------------------------
        Assistant a1 = new Assistant(1, 1, rick);
        PlayerActionPhase pap = new PlayerActionPhase(a1, gb);

        // Rick plays plus2Influence character
        PassiveCharacter pc = new PassiveCharacter(CharacterName.plus2Influence);
        pap.playPassiveCharacter(pc);

        // Island is resolved, rick should own the island
        pap.resolveIsland(island);
        assertSame(island.getTowerColor(), rick.getTowerColor());
    }

    /**
     * Tests the strategy to calculate the influence on an {@code Island} when the effect of the {@code Character}
     * called "ignoreColor" is active
     */
    @Test
    public void influenceIgnoreColorTest() {
        Player rick = g.getPlayers().get(0);        // Rick is player 0
        Player clod = g.getPlayers().get(1);        // Clod is player 1

        // Rick has green professor
        gb.setOwnerOfProfessor(Color.GREEN, rick);

        // Clod has red professor
        gb.setOwnerOfProfessor(Color.RED, clod);

        // island with 3 green student and 2 red students
        Island island = new Island();
        island.receiveStudent(new Student(Color.GREEN));
        island.receiveStudent(new Student(Color.GREEN));
        island.receiveStudent(new Student(Color.GREEN));
        island.receiveStudent(new Student(Color.RED));
        island.receiveStudent(new Student(Color.RED));

        //-----------------------Rick's turn--------------------------------------------
        Assistant a1 = new Assistant(1, 1, rick);
        PlayerActionPhase pap = new PlayerActionPhase(a1, gb);

        // Island is resolved, rick should own the island
        pap.resolveIsland(island);
        assertSame(island.getTowerColor(), rick.getTowerColor());

        //-----------------------Clod's turn--------------------------------------------
        Assistant a2 = new Assistant(1, 2, clod);
        pap = new PlayerActionPhase(a2, gb);

        // Clod plays the ignoreColor character
        pap.playPassiveCharacterWithColor(Color.GREEN);

        // solve island again -> clod will now own the island
        pap.resolveIsland(island);
        assertSame(island.getTowerColor(), clod.getTowerColor());
    }

    /**
     * Tests the strategy to calculate the influence on an {@code Island} when the effect of the {@code Character}
     * called "ignoreTowers" is active
     */
    @Test
    public void influenceIgnoreTowersTest1() {
        Player rick = g.getPlayers().get(0);        // Rick is player 0
        Player clod = g.getPlayers().get(1);        // Clod is player 1

        // Rick has green professor
        gb.setOwnerOfProfessor(Color.GREEN, rick);

        // Clod has red professor
        gb.setOwnerOfProfessor(Color.RED, clod);

        // island with 1 green student
        Island island = new Island();
        island.receiveStudent(new Student(Color.GREEN));

        //-----------------------Rick's turn--------------------------------------------
        Assistant a1 = new Assistant(1, 1, rick);
        PlayerActionPhase pap = new PlayerActionPhase(a1, gb);

        // Island is resolved, rick should own the island
        pap.resolveIsland(island);
        assertSame(island.getTowerColor(), rick.getTowerColor());

        //-----------------------Clod's turn--------------------------------------------
        Assistant a2 = new Assistant(1, 2, clod);
        pap = new PlayerActionPhase(a2, gb);

        // Clod plays the ignoreTowers character
        PassiveCharacter pc = new PassiveCharacter(CharacterName.ignoreTowers);
        pap.playPassiveCharacter(pc);

        // add 2 red to island and solve -> clod will now own the island
        island.receiveStudent(new Student(Color.RED));
        island.receiveStudent(new Student(Color.RED));
        pap.resolveIsland(island);
        assertSame(island.getTowerColor(), clod.getTowerColor());
    }

    /**
     * Tests the default strategy to calculate the influence on an {@code Island} when on a 4 player game (two teams)
     */
    @Test
    public void fourPlayerGameDefault() {
        Player rick = g2.getPlayers().get(0);        // Rick is player 0
        Player clod = g2.getPlayers().get(1);        // Clod is player 1
        Player giuse = g2.getPlayers().get(2);        // Clod is player 1
        Player fabio = g2.getPlayers().get(3);        // Clod is player 1

        gb = g2.getGameBoard();

        assertSame(rick.getTowerColor(), TowerColor.WHITE);
        assertSame(giuse.getTowerColor(), TowerColor.WHITE);
        assertSame(clod.getTowerColor(), TowerColor.BLACK);
        assertSame(fabio.getTowerColor(), TowerColor.BLACK);

        // Rick has green professor
        gb.setOwnerOfProfessor(Color.GREEN, rick);

        // Clod has red and blue professor
        gb.setOwnerOfProfessor(Color.RED, clod);
        gb.setOwnerOfProfessor(Color.BLUE, fabio);

        // Giuse has pink professor
        gb.setOwnerOfProfessor(Color.PINK, giuse);

        // Fabio has yellow professor
        gb.setOwnerOfProfessor(Color.YELLOW, fabio);

        // island with 2 green student, 2 pink and 3 reds
        Island island = new Island();
        island.receiveStudent(new Student(Color.GREEN));
        island.receiveStudent(new Student(Color.GREEN));
        island.receiveStudent(new Student(Color.PINK));
        island.receiveStudent(new Student(Color.PINK));
        island.receiveStudent(new Student(Color.RED));
        island.receiveStudent(new Student(Color.RED));
        island.receiveStudent(new Student(Color.RED));

        //-----------------------Rick's turn--------------------------------------------
        Assistant a1 = new Assistant(1, 1, rick);
        PlayerActionPhase pap = new PlayerActionPhase(a1, gb);

        // Island is resolved, island tower should be white
        pap.resolveIsland(island);
        assertSame(island.getTowerColor(), TowerColor.WHITE);

        //-----------------------Clod's turn--------------------------------------------
        Assistant a2 = new Assistant(1, 2, clod);
        pap = new PlayerActionPhase(a2, gb);

        // add 2 yellow to island and solve -> island tower should remain white
        island.receiveStudent(new Student(Color.YELLOW));
        island.receiveStudent(new Student(Color.YELLOW));
        pap.resolveIsland(island);
        assertSame(island.getTowerColor(), TowerColor.WHITE);

        //-----------------------Fabio's turn--------------------------------------------
        Assistant a3 = new Assistant(1, 3, fabio);
        pap = new PlayerActionPhase(a3, gb);
        // add a blue and solve -> island tower is now black
        island.receiveStudent(new Student(Color.BLUE));
        pap.resolveIsland(island);
        assertSame(island.getTowerColor(), TowerColor.BLACK);
    }

}