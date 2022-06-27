package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.InvalidActionException;
import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.TestGameFactory;
import it.polimi.ingsw.model.game_actions.PlayerActionPhase;
import it.polimi.ingsw.model.game_objects.Assistant;
import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.model.game_objects.Student;
import it.polimi.ingsw.model.game_objects.gameboard_objects.GameBoard;
import it.polimi.ingsw.model.utils.Students;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class EveryOneMovesCharacterTest {

    Game game = TestGameFactory.getNewGame();
    GameBoard gb = game.getGameBoard();
    Character[] c = {new EveryOneMovesCharacter(CharacterName.everyOneMove3FromDiningRoomToBag, gb)};
    Player rick = game.getPlayers().get(0);
    Player clod = game.getPlayers().get(1);
    Player giuse = game.getPlayers().get(2);

    /**
     * Fills the dining rooms of the three players in the following way:
     * - Rick has one green student
     * - Clod has three green students
     * - Giuse has seven green students
     */
    private void fillDiningRooms() throws InvalidActionException {
        rick.getDashboard().getDiningRoom().receiveStudent(new Student(Color.GREEN));
        for (int i = 0; i < 3; i++) {
            clod.getDashboard().getDiningRoom().receiveStudent(new Student(Color.GREEN));
        }
        for (int i = 0; i < 7; i++) {
            giuse.getDashboard().getDiningRoom().receiveStudent(new Student(Color.GREEN));
        }
    }

    /**
     * Tests the effect of the {@code Character} called "everyOneMove3FromDiningRoomToBag"
     */
    @Test
    void testEveryOneMovesCharacter() throws InvalidActionException {

        gb.setCharacters(c);
        fillDiningRooms();
        for (int i = 0; i < 5; i++) {
            rick.addCoin();
        }

        EveryOneMovesCharacter character = (EveryOneMovesCharacter) gb.getCharacters()[0];
        // Rick plays this character choosing green color
        PlayerActionPhase rickPap = new PlayerActionPhase(new Assistant(1, 2, rick), gb);
        assertDoesNotThrow(() -> rickPap.playCharacter(character, null, Color.GREEN, null, null));

        // Clod and Giuse should have three students less, while Rick should have lost his only green student
        assertEquals(0, Students.countColor(clod.getDashboard().getDiningRoom().getStudents(), Color.GREEN));
        assertEquals(0, Students.countColor(rick.getDashboard().getDiningRoom().getStudents(), Color.GREEN));
        assertEquals(4, Students.countColor(giuse.getDashboard().getDiningRoom().getStudents(), Color.GREEN));
        // Character should now cost 4 coins
        assertEquals(4, character.getCost());
    }

}
