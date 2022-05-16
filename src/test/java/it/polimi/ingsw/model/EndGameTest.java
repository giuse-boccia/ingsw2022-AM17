package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.model.game_actions.PlayerActionPhase;
import it.polimi.ingsw.model.game_objects.Assistant;
import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.model.game_objects.gameboard_objects.Bag;
import it.polimi.ingsw.model.game_objects.gameboard_objects.Island;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class EndGameTest {

    Game game = TestGameFactory.getNewGame();

    @Test
    void testEmptyBagEnd() throws InvalidActionException, InvalidStudentException, InvalidStepsForMotherNatureException, InvalidCloudException {
        Bag bag = game.getGameBoard().getBag();
        Player rick = game.getPlayers().get(0);
        Player clod = game.getPlayers().get(1);
        Player giuse = game.getPlayers().get(2);

        while (game.getGameBoard().getMotherNatureIndex() != 0)
            game.getGameBoard().moveMotherNature(1);

        int studentsInBag = 108;
        for (Character character : game.getGameBoard().getCharacters()) {
            CharacterName name = character.getCardName();
            if (name == CharacterName.move1FromCardToIsland || name == CharacterName.move1FromCardToDining) {
                studentsInBag -= 4;
            } else if (name == CharacterName.swapUpTo3FromEntranceToCard) {
                studentsInBag -= 6;
            }
        }

        for (int i = 0; i < studentsInBag; i++) {
            assertDoesNotThrow(() ->
                    bag.giveStudent(rick.getDashboard().getEntrance(), bag.getRandStudent())
            );
        }

        // Bag is now empty
        assertTrue(bag.isEmpty());
        assertFalse(game.getCurrentRound().isLastRound());

        // Clod has 5 Green & 4 Blue, Rick 5 Pink & 4 Green, G 5 Blue & 4 Pink
        TestGameFactory.fillThreeEntrances(rick, clod, giuse);


        game.getCurrentRound().endPlanningPhase(
                new ArrayList<>(List.of(
                        new Assistant(4, 8, rick),
                        new Assistant(2, 4, clod),
                        new Assistant(3, 6, giuse))
                )
        );

        // Quick simulation of the last Round
        // Clod's turn
        PlayerActionPhase pap = game.getCurrentRound().getCurrentPlayerActionPhase();
        pap.moveStudent(Color.BLUE, clod.getDashboard().getDiningRoom());
        pap.moveStudent(Color.BLUE, clod.getDashboard().getDiningRoom());
        pap.moveStudent(Color.BLUE, game.getGameBoard().getIslands().get(1));
        pap.moveStudent(Color.BLUE, game.getGameBoard().getIslands().get(1));
        pap.moveMotherNature(1);
        pap.chooseCloud(0);
        assertEquals(5, clod.getNumberOfTowers());

        // Mother nature is in island 1 where there are 2 blue students, whose professor belongs to Clod

        // Giuse's turn
        pap = game.getCurrentRound().getCurrentPlayerActionPhase();
        pap.moveStudent(Color.PINK, giuse.getDashboard().getDiningRoom());
        pap.moveStudent(Color.PINK, giuse.getDashboard().getDiningRoom());
        pap.moveStudent(Color.PINK, game.getGameBoard().getIslands().get(2));
        pap.moveStudent(Color.PINK, game.getGameBoard().getIslands().get(2));
        pap.moveMotherNature(1);
        pap.chooseCloud(1);
        assertEquals(5, giuse.getNumberOfTowers());

        // Mother nature is in island 2 where there are 2 pink students, whose professor belongs to Giuse

        // Rick's turn
        pap = game.getCurrentRound().getCurrentPlayerActionPhase();
        pap.moveStudent(Color.PINK, rick.getDashboard().getDiningRoom());
        pap.moveStudent(Color.PINK, rick.getDashboard().getDiningRoom());
        pap.moveStudent(Color.PINK, rick.getDashboard().getDiningRoom());
        pap.moveStudent(Color.PINK, rick.getDashboard().getDiningRoom());
        Island nextIsland = game.getGameBoard().getIslands().get(3);
        if (nextIsland.getStudents().size() == 1) {
            nextIsland.giveStudent(rick.getDashboard().getEntrance(), nextIsland.getStudents().get(0));
        }
        pap.moveMotherNature(1);
        pap.chooseCloud(2);
        assertEquals(6, rick.getNumberOfTowers());

        assertTrue(game.isEnded());
        assertEquals(1, game.getWinners().size());
        assertSame(clod, game.getWinners().get(0));
    }

    private void playCurrentRound(Game game) {

    }

}
