package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.InvalidActionException;
import it.polimi.ingsw.exceptions.InvalidCharacterException;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.TestGameFactory;
import it.polimi.ingsw.model.game_actions.action_phase.PlayerActionPhase;
import it.polimi.ingsw.model.game_actions.action_phase.PlayerActionPhaseFactory;
import it.polimi.ingsw.model.game_objects.Assistant;
import it.polimi.ingsw.model.game_objects.GameBoard;
import it.polimi.ingsw.model.game_objects.Island;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NoEntryCharacterTest {

    Game game = TestGameFactory.getNewGame();
    GameBoard gb = game.getGameBoard();

    @Test
    void testNoEntryCharacter() {
        NoEntryCharacter character = new NoEntryCharacter(CharacterName.noEntry, gb);
        // Island selected by the View
        Island selectedIsland = game.getGameBoard().getIslands().get(0);

        for (int noEntriesOnIsland = 0; noEntriesOnIsland < 4; noEntriesOnIsland++) {
            PlayerActionPhase pap = PlayerActionPhaseFactory.createPlayerActionPhase(
                    new Assistant(4, 8, game.getPlayers().get(0)), gb, true
            );

            assertEquals(noEntriesOnIsland, selectedIsland.getNoEntryNum());

            assertDoesNotThrow(() -> pap.playCharacter(character, selectedIsland, null, null, null));
            assertEquals(noEntriesOnIsland + 1, selectedIsland.getNoEntryNum());
        }

        PlayerActionPhase pap = PlayerActionPhaseFactory.createPlayerActionPhase(
                new Assistant(4, 8, game.getPlayers().get(0)), gb, true
        );
        assertThrows(InvalidActionException.class,
                () -> pap.playCharacter(character, selectedIsland, null, null, null),
                "There are no NoEntry pawns left on this card");

    }
}
