package it.polimi.ingsw.model.characters_tests;

import it.polimi.ingsw.model.Game;
import it.polimi.ingsw.model.TestGameFactory;
import it.polimi.ingsw.model.game_objects.*;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class CharactersTest {

    Game game = TestGameFactory.getNewGame();
    GameBoard gb = game.getGameBoard();

    @Test
    void testInitializeOfCharacters() {

    }

}
