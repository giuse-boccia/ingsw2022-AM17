package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.TestGameFactory;
import it.polimi.ingsw.model.game_actions.PlayerActionPhase;
import it.polimi.ingsw.model.game_objects.*;
import it.polimi.ingsw.model.game_objects.gameboard_objects.GameBoard;
import it.polimi.ingsw.model.game_objects.gameboard_objects.Island;
import it.polimi.ingsw.model.utils.Students;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ResolveIslandCharacterTest {

    Game game = TestGameFactory.getNewGame();
    GameBoard gb = game.getGameBoard();
    Character[] c = {new ResolveIslandCharacter(CharacterName.resolveIsland, gb)};

    /**
     * Tests the effect of the {@code Character} called "resolveIsland"
     */
    @Test
    void testResolveIslandCharacter1() {
        gb.setCharacters(c);

        ResolveIslandCharacter character = (ResolveIslandCharacter) gb.getCharacters()[0];
        PlayerActionPhase pap = new PlayerActionPhase(
                new Assistant(4, 8, game.getPlayers().get(0)), gb
        );
        for (int i = 0; i < 8; i++) {
            game.getPlayers().get(0).addCoin();
        }
        ArrayList<Island> islands = game.getGameBoard().getIslands();

        gb.setMotherNatureIndex(3);

        // Initially the selected island is owned by Player 2
        islands.get(0).setTowerColor(game.getPlayers().get(2).getTowerColor());

        for (int i = 0; i < 3; i++) {
            islands.get(0).receiveStudent(new Student(Color.GREEN));
        }
        islands.get(0).receiveStudent(new Student(Color.BLUE));
        gb.setOwnerOfProfessor(Color.GREEN, game.getPlayers().get(0));
        gb.setOwnerOfProfessor(Color.RED, game.getPlayers().get(0));
        gb.setOwnerOfProfessor(Color.BLUE, game.getPlayers().get(1));

        // After playing the character, the island is resolved (and is now owned bt Player 0), while mother nature hasn't moved
        assertDoesNotThrow(() -> pap.playCharacter(character, islands.get(0), null, null, null));
        assertEquals(game.getPlayers().get(0).getTowerColor(), islands.get(0).getTowerColor());
        assertEquals(3, gb.getMotherNatureIndex());

    }

    /**
     * Tests the effect of the {@code Character} called "resolveIsland"
     */
    @Test
    void testResolveIslandCharacter2() {
        gb.setCharacters(c);

        ResolveIslandCharacter character = (ResolveIslandCharacter) gb.getCharacters()[0];
        PlayerActionPhase pap = new PlayerActionPhase(
                new Assistant(4, 8, game.getPlayers().get(0)), gb
        );
        for (int i = 0; i < 8; i++) {
            game.getPlayers().get(0).addCoin();
        }
        ArrayList<Island> islands = game.getGameBoard().getIslands();

        gb.setMotherNatureIndex(11);

        // Initially the selected island is owned by Player 2, while Player 0 owns the two island on the left and on the right
        islands.get(0).setTowerColor(game.getPlayers().get(2).getTowerColor());
        islands.get(1).setTowerColor(game.getPlayers().get(0).getTowerColor());
        islands.get(11).setTowerColor(game.getPlayers().get(0).getTowerColor());

        for (int i = 0; i < 3; i++) {
            islands.get(0).receiveStudent(new Student(Color.GREEN));
        }
        islands.get(0).receiveStudent(new Student(Color.BLUE));

        gb.setOwnerOfProfessor(Color.GREEN, game.getPlayers().get(0));
        gb.setOwnerOfProfessor(Color.BLUE, game.getPlayers().get(0));
        gb.setOwnerOfProfessor(Color.RED, game.getPlayers().get(1));

        assertDoesNotThrow(() -> pap.playCharacter(character, islands.get(0), null, null, null));
        assertEquals(game.getPlayers().get(0).getTowerColor(), islands.get(0).getTowerColor());
        assertEquals(10, game.getGameBoard().getIslands().size());
        assertEquals(islands.get(0).getTowerColor(), game.getGameBoard().getIslands().get(9).getTowerColor());
        assertTrue(3 <= Students.countColor(game.getGameBoard().getIslands().get(9).getStudents(), Color.GREEN));
        assertTrue(1 <= Students.countColor(game.getGameBoard().getIslands().get(9).getStudents(), Color.BLUE));
        assertEquals(9, gb.getMotherNatureIndex());

    }


}
