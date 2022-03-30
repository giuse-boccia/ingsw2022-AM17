package it.polimi.ingsw.model.characters_tests;

import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.exceptions.InvalidActionException;
import it.polimi.ingsw.exceptions.StudentNotOnTheCardException;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.TestGameFactory;
import it.polimi.ingsw.model.character.Character;
import it.polimi.ingsw.model.character.CharacterName;
import it.polimi.ingsw.model.character.MovingCharacter;
import it.polimi.ingsw.model.game_actions.action_phase.PlayerActionPhase;
import it.polimi.ingsw.model.game_actions.action_phase.PlayerActionPhaseFactory;
import it.polimi.ingsw.model.game_objects.*;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class Move1FromCardToIslandTest {

    Game game = TestGameFactory.getNewGame();
    GameBoard gb = game.getGameBoard();

    private int containsCard(Character[] characters) {
        for (int i = 0; i < characters.length; i++) {
            if (characters[i].getCardName() == CharacterName.move1FromCardToIsland)
                return i;
        }
        return -1;
    }

    @Test
    void testMove1FromCardToIsland1() throws EmptyBagException {
        MovingCharacter character = new MovingCharacter(CharacterName.move1FromCardToIsland, gb, 4, 1);
        PlayerActionPhase pap = PlayerActionPhaseFactory.createPlayerActionPhase(
                new Assistant(4, 8, game.getPlayers().get(0)), gb, true
        );
        Island island = gb.getIslands().get(4);
        int initialStudentsOnIsland = island.getStudents().size();
        island.receiveStudent(new Student(Color.PINK));
        island.receiveStudent(new Student(Color.BLUE));
        character.fillCardFromBag();

        assertEquals(1, character.getNumStudents());
        assertTrue(2 == island.getStudents().size() || 3 == island.getStudents().size());
        assertEquals(4, character.getStudents().size());

        ArrayList<Student> studentInCard = new ArrayList<>();
        studentInCard.add(character.getStudents().get(2));

        assertDoesNotThrow(() -> pap.playCharacter(character, island, null, studentInCard, null));

        assertEquals(3, character.getStudents().size());
        assertEquals(3 + initialStudentsOnIsland, island.getStudents().size());
        assertTrue(island.getStudents().contains(studentInCard.get(0)));
    }

    @Test
    void testMove1FromCardToIsland2() throws EmptyBagException {
        int index = containsCard(gb.getCharacters());
        while (index == -1) {
            gb = new GameBoard(game);
            index = containsCard(gb.getCharacters());
        }

        MovingCharacter character = (MovingCharacter) gb.getCharacters()[index];
        character.fillCardFromBag();
        assertEquals(4, character.getInitialStudents());
        assertEquals(4, character.getStudents().size());

        Island island = gb.getIslands().get(3);
        int initialStudents = island.getStudents().size();
        island.receiveStudent(new Student(Color.GREEN));
        PlayerActionPhase pap = PlayerActionPhaseFactory.createPlayerActionPhase(
                new Assistant(4, 8, game.getPlayers().get(0)), gb, true
        );

        Student studentToGive = character.getStudents().get(new Random().nextInt(4));
        assertDoesNotThrow(() -> pap.playCharacter(
                character, island, null, new ArrayList<>(List.of(studentToGive)), null
        ));

        assertEquals(initialStudents + 2, island.getStudents().size());
        assertEquals(3, character.getStudents().size());
        assertTrue(island.getStudents().contains(studentToGive));
        assertFalse(character.getStudents().contains(studentToGive));
    }

    @Test
    void testMove1FromCardToIsland3() throws EmptyBagException {
        MovingCharacter character = new MovingCharacter(CharacterName.move1FromCardToIsland, gb, 4, 1);
        PlayerActionPhase pap = PlayerActionPhaseFactory.createPlayerActionPhase(
                new Assistant(4, 8, game.getPlayers().get(0)), gb, true
        );
        Island island = gb.getIslands().get(6);

        character.fillCardFromBag();

        ArrayList<Student> invalidStudents = new ArrayList<>(List.of(new Student(Color.GREEN)));

        assertThrows(
                StudentNotOnTheCardException.class,
                () -> pap.playCharacter(character, island, null, invalidStudents, null),
                "The student is not on the card"
        );
    }

    @Test
    void testMove1FromCardToIsland4() throws EmptyBagException {
        MovingCharacter character = new MovingCharacter(CharacterName.move1FromCardToIsland, gb, 4, 1);
        PlayerActionPhase pap = PlayerActionPhaseFactory.createPlayerActionPhase(
                new Assistant(4, 8, game.getPlayers().get(0)), gb, true
        );
        Island island = gb.getIslands().get(6);

        character.fillCardFromBag();

        ArrayList<Student> invalidStudents = new ArrayList<>(character.getStudents().subList(0, 3));

        assertThrows(
                InvalidActionException.class,
                () -> pap.playCharacter(character, island, null, invalidStudents, null),
                "You can move up to two students"
        );
    }

}
