package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.InvalidActionException;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.TestGameFactory;
import it.polimi.ingsw.model.game_actions.action_phase.PlayerActionPhase;
import it.polimi.ingsw.model.game_objects.Assistant;
import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.model.game_objects.Student;
import it.polimi.ingsw.model.game_objects.gameboard_objects.GameBoard;
import it.polimi.ingsw.model.game_objects.gameboard_objects.Island;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NoEntryCharacterTest {

    Game game = TestGameFactory.getNewGame();
    GameBoard gb = game.getGameBoard();

    /**
     * Tests the effect of the {@code Character} called "noEntry"
     */
    @Test
    void testNoEntryCharacter() {
        NoEntryCharacter character = new NoEntryCharacter(CharacterName.noEntry, gb);
        // Island selected by the View
        Island selectedIsland = game.getGameBoard().getIslands().get(0);
        Player rick = game.getPlayers().get(0);

        for (int noEntriesOnIsland = 0; noEntriesOnIsland < 4; noEntriesOnIsland++) {
            PlayerActionPhase pap = new PlayerActionPhase(
                    new Assistant(4, 8, rick), gb
            );

            assertEquals(noEntriesOnIsland, selectedIsland.getNoEntryNum());

            assertDoesNotThrow(() -> pap.playCharacter(character, selectedIsland, null, null, null));
            assertEquals(noEntriesOnIsland + 1, selectedIsland.getNoEntryNum());
        }

        PlayerActionPhase pap = new PlayerActionPhase(
                new Assistant(4, 8, rick), gb
        );
        assertThrows(
                InvalidActionException.class,
                () -> pap.playCharacter(character, selectedIsland, null, null, null)
        );

    }

    /**
     * Checks that the owner of an {@code Island} with at least one NoEntry tile does not get changed by
     * resolveIsland() method
     */
    @Test
    void testNoEntryCharacter2() {

        Character noEntryCharacter = null;
        do {
            game = TestGameFactory.getNewGame();
            for (Character c : game.getGameBoard().getCharacters()) {
                if (c.getCardName() == CharacterName.noEntry) {
                    noEntryCharacter = c;
                }
            }
        } while (noEntryCharacter == null);

        Island selectedIsland = game.getGameBoard().getIslands().get(0);
        Player rick = game.getPlayers().get(0);
        PlayerActionPhase pap = new PlayerActionPhase(
                new Assistant(4, 8, rick), game.getGameBoard()
        );

        game.getGameBoard().getIslands().get(1).setOwner(rick);
        game.getGameBoard().getIslands().get(11).setOwner(rick);

        selectedIsland.receiveStudent(new Student(Color.BLUE));
        rick.getDashboard().getEntrance().receiveStudent(new Student(Color.BLUE));
        // Rick now owns blue professor
        assertDoesNotThrow(() -> pap.moveStudent(Color.BLUE, rick.getDashboard().getDiningRoom()));

        NoEntryCharacter finalNoEntryCharacter = (NoEntryCharacter) noEntryCharacter;
        assertDoesNotThrow(() -> pap.playCharacter(finalNoEntryCharacter, selectedIsland, null, null, null));
        assertEquals(1, selectedIsland.getNoEntryNum());

        pap.resolveIsland(selectedIsland);

        assertNull(selectedIsland.getOwner());
        assertEquals(12, game.getGameBoard().getIslands().size());
        assertEquals(1, rick.getDashboard().getDiningRoom().getNumberOfStudentsOfColor(Color.BLUE));
        assertEquals(0, selectedIsland.getNoEntryNum());
    }
}
