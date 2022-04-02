package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.TestGameFactory;
import it.polimi.ingsw.model.game_objects.gameboard_objects.GameBoard;
import org.junit.jupiter.api.Test;


public class CharactersTest {

    Game game = TestGameFactory.getNewGame();
    GameBoard gb = game.getGameBoard();

    @Test
    void testInitializeOfCharacters() {

    }

}
