package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.CharacterAlreadyPlayedException;
import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.exceptions.InvalidCharacterException;
import it.polimi.ingsw.exceptions.ProfessorAlreadyPresentException;
import it.polimi.ingsw.model.character.CharacterName;
import it.polimi.ingsw.model.character.EveryOneMovesCharacter;
import it.polimi.ingsw.model.character.NoEntryCharacter;
import it.polimi.ingsw.model.character.ResolveIslandCharacter;
import it.polimi.ingsw.model.game_actions.action_phase.PlayerActionPhase;
import it.polimi.ingsw.model.game_actions.action_phase.PlayerActionPhaseFactory;
import it.polimi.ingsw.model.game_objects.*;
import it.polimi.ingsw.model.game_objects.dashboard_objects.Entrance;
import it.polimi.ingsw.model.utils.Students;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;

import static org.junit.jupiter.api.Assertions.*;


public class CharactersTest {

    Game game = TestGameFactory.getNewGame();
    GameBoard gb = game.getGameBoard();

    private void fillEntrance(Entrance entrance) {
        for (int i = 0; i < 7; i++) {
            try {
                game.getGameBoard().getBag().giveStudent(entrance, game.getGameBoard().getBag().getRandStudent());
            } catch (EmptyBagException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    void testInitializeOfCharacters() {

    }

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
        assertThrows(InvalidCharacterException.class,
                () -> pap.playCharacter(character, selectedIsland, null, null, null),
                "There are no NoEntry pawns left on this card");

    }

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
