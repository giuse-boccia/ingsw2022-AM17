package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.CharacterAlreadyPlayedException;
import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.TestGameFactory;
import it.polimi.ingsw.model.game_actions.action_phase.PlayerActionPhase;
import it.polimi.ingsw.model.game_actions.action_phase.PlayerActionPhaseFactory;
import it.polimi.ingsw.model.game_objects.Assistant;
import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.model.game_objects.gameboard_objects.GameBoard;
import it.polimi.ingsw.model.game_objects.dashboard_objects.Entrance;
import it.polimi.ingsw.model.utils.Students;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;

public class EveryOneMovesCharacterTest {

    Game game = TestGameFactory.getNewGame();
    GameBoard gb = game.getGameBoard();

    /**
     * Helper method which fills the selected {@code Entrance} with 7 students from the {@code Bag}
     *
     * @param entrance the {@code Entrance} to fill
     */
    private void fillEntrance(Entrance entrance) {
        for (int i = 0; i < 7; i++) {
            try {
                game.getGameBoard().getBag().giveStudent(entrance, game.getGameBoard().getBag().getRandStudent());
            } catch (EmptyBagException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Tests the effect of the {@code Character} called "everyOneMove3FromDiningRoomToBag"
     */
    @Test
    void testEveryOneMovesCharacter() {
        EveryOneMovesCharacter character = new EveryOneMovesCharacter(CharacterName.everyOneMove3FromDiningRoomToBag, gb);
        ArrayList<HashMap<Color, Integer>> initialPlayerMaps = new ArrayList<>();
        ArrayList<HashMap<Color, Integer>> finalPlayerMaps = new ArrayList<>();

        for (Player player : game.getPlayers()) {
            Entrance entrance = player.getDashboard().getEntrance();
            HashMap<Color, Integer> initialMap = new HashMap<>();
            fillEntrance(entrance);
            for (Color color : Color.values()) {
                initialMap.put(color, Students.countColor(entrance.getStudents(), color));
            }
            initialPlayerMaps.add(initialMap);
        }

        PlayerActionPhase pap = PlayerActionPhaseFactory.createPlayerActionPhase(
                new Assistant(4, 8, game.getPlayers().get(0)), gb, true
        );
        assertDoesNotThrow(() -> pap.playCharacter(character, null, Color.GREEN, null, null));

        for (Player player : game.getPlayers()) {
            HashMap<Color, Integer> finalMap = new HashMap<>();
            for (Color color : Color.values()) {
                finalMap.put(color, Students.countColor(player.getDashboard().getEntrance().getStudents(), color));
            }
            finalPlayerMaps.add(finalMap);
        }

        assertEquals(initialPlayerMaps.size(), finalPlayerMaps.size());
        for (int i = 0; i < initialPlayerMaps.size(); i++) {
            HashMap<Color, Integer> initialMap = initialPlayerMaps.get(i);
            HashMap<Color, Integer> finalMap = finalPlayerMaps.get(i);
            for (Color color : Color.values()) {
                assertEquals(initialMap.get(color), finalMap.get(color));
            }
        }

        assertThrows(CharacterAlreadyPlayedException.class, () -> pap.playCharacter(character, null, Color.GREEN, null, null));
    }

}
