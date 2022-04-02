package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.ProfessorAlreadyPresentException;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.TestGameFactory;
import it.polimi.ingsw.model.game_actions.action_phase.PlayerActionPhase;
import it.polimi.ingsw.model.game_actions.action_phase.PlayerActionPhaseFactory;
import it.polimi.ingsw.model.game_objects.*;
import it.polimi.ingsw.model.utils.Students;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ResolveIslandCharacterTest {

    Game game = TestGameFactory.getNewGame();
    GameBoard gb = game.getGameBoard();

    /**
     * Tests the effect of the {@code Character} called "resolveIsland"
     *
     * @throws ProfessorAlreadyPresentException if the {@code Professor} is already present in the {@code ProfessorRoom}
     *                                          of the {@code Player} who is trying to get it
     */
    @Test
    void testResolveIslandCharacter1() throws ProfessorAlreadyPresentException {
        ResolveIslandCharacter character = new ResolveIslandCharacter(CharacterName.noEntry, gb);
        PlayerActionPhase pap = PlayerActionPhaseFactory.createPlayerActionPhase(
                new Assistant(4, 8, game.getPlayers().get(0)), gb, true
        );
        ArrayList<Island> islands = game.getGameBoard().getIslands();

        while (gb.getMotherNatureIndex() != 3)
            gb.moveMotherNature(1);

        // Initially the selected island is owned by Player 2
        islands.get(0).setOwner(game.getPlayers().get(2));

        for (int i = 0; i < 3; i++) {
            islands.get(0).receiveStudent(new Student(Color.GREEN));
        }
        islands.get(0).receiveStudent(new Student(Color.BLUE));
        game.getPlayers().get(0).getDashboard().getProfessorRoom().takeProfessor(new Professor(Color.GREEN));
        game.getPlayers().get(0).getDashboard().getProfessorRoom().takeProfessor(new Professor(Color.RED));
        game.getPlayers().get(1).getDashboard().getProfessorRoom().takeProfessor(new Professor(Color.BLUE));

        // After playing the character, the island is resolved (and is now owned bt Player 0), while mother nature hasn't moved
        assertDoesNotThrow(() -> pap.playCharacter(character, islands.get(0), null, null, null));
        assertEquals(game.getPlayers().get(0), islands.get(0).getOwner());
        assertEquals(3, gb.getMotherNatureIndex());

    }

    /**
     * Tests the effect of the {@code Character} called "resolveIsland"
     *
     * @throws ProfessorAlreadyPresentException if the {@code Professor} is already present in the {@code ProfessorRoom}
     *                                          of the {@code Player} who is trying to get it
     */
    @Test
    void testResolveIslandCharacter2() throws ProfessorAlreadyPresentException {
        ResolveIslandCharacter character = new ResolveIslandCharacter(CharacterName.resolveIsland, gb);
        PlayerActionPhase pap = PlayerActionPhaseFactory.createPlayerActionPhase(
                new Assistant(4, 8, game.getPlayers().get(0)), gb, true
        );
        ArrayList<Island> islands = game.getGameBoard().getIslands();

        while (gb.getMotherNatureIndex() != 11)
            gb.moveMotherNature(1);

        // Initially the selected island is owned by Player 2, while Player 0 owns the two island on the left and on the right
        islands.get(0).setOwner(game.getPlayers().get(2));
        islands.get(1).setOwner(game.getPlayers().get(0));
        islands.get(11).setOwner(game.getPlayers().get(0));

        for (int i = 0; i < 3; i++) {
            islands.get(0).receiveStudent(new Student(Color.GREEN));
        }
        islands.get(0).receiveStudent(new Student(Color.BLUE));
        game.getPlayers().get(0).getDashboard().getProfessorRoom().takeProfessor(new Professor(Color.GREEN));
        game.getPlayers().get(0).getDashboard().getProfessorRoom().takeProfessor(new Professor(Color.BLUE));
        game.getPlayers().get(1).getDashboard().getProfessorRoom().takeProfessor(new Professor(Color.RED));


        assertDoesNotThrow(() -> pap.playCharacter(character, islands.get(0), null, null, null));
        assertEquals(game.getPlayers().get(0), islands.get(0).getOwner());
        assertEquals(10, game.getGameBoard().getIslands().size());
        assertEquals(islands.get(0).getOwner(), game.getGameBoard().getIslands().get(9).getOwner());
        assertTrue(3 <= Students.countColor(game.getGameBoard().getIslands().get(9).getStudents(), Color.GREEN));
        assertTrue(1 <= Students.countColor(game.getGameBoard().getIslands().get(9).getStudents(), Color.BLUE));
        assertEquals(9, gb.getMotherNatureIndex());

    }


}
