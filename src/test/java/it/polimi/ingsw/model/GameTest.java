package it.polimi.ingsw.model;

import it.polimi.ingsw.utils.constants.Constants;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.game_actions.Round;
import it.polimi.ingsw.model.game_actions.PlayerActionPhase;
import it.polimi.ingsw.model.game_objects.*;
import it.polimi.ingsw.model.game_objects.gameboard_objects.Bag;
import it.polimi.ingsw.model.game_objects.gameboard_objects.Island;
import it.polimi.ingsw.model.utils.Students;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class GameTest {

    Game game = TestGameFactory.getNewGame();
    Bag bag = game.getGameBoard().getBag();
    ArrayList<Player> players = game.getPlayers();
    Player rick = players.get(0);
    Player clod = players.get(1);
    Player giuse = players.get(2);

    /**
     * Tests the first round of a non-expert {@code Game}
     * Tries to play wrong moves and asserts proper exceptions are thrown
     */
    @Test
    void testFullNonExpertGame() throws InvalidStudentException, InvalidActionException {

        int originalMNIndex = game.getGameBoard().getMotherNatureIndex();
        prepareIslands(originalMNIndex);
        while (game.getGameBoard().getMotherNatureIndex() != 0) {
            game.getGameBoard().moveMotherNature(1);
        }

        setupGame();

        // I decide to play 5 Rounds
        playFirstRound();

    }

    /**
     * Plays the first round of a non-expert {@code Game}
     */
    private void playFirstRound() {
        Round firstRound = game.getCurrentRound();

        assertNull(firstRound.getPlayedAssistants());
        assertEquals(0, game.getRoundsPlayed());
        assertFalse(firstRound.isLastRound());
        // Now clouds are full and there are 93 students

        // We are in planning phase
        // Now every player plays an Assistant, starting from Player0
        // If Player1 wants to play first, an Exception is thrown
        assertThrows(InvalidActionException.class, () -> playAssistant(clod, 4));
        assertThrows(InvalidActionException.class, () -> playAssistant(giuse, 2));
        // Player0 has the right to play and plays 3rd assistant
        assertEquals("Rick", rick.getName());
        assertEquals(0, firstRound.getFirstPlayerIndex());
        assertDoesNotThrow(() -> playAssistant(rick, 8));
        // Player 0 has in hand 9 Assistants

        assertThrows(InvalidActionException.class, () -> playAssistant(giuse, 2));
        assertThrows(InvalidActionException.class, () -> playAssistant(rick, 3));
        assertDoesNotThrow(() -> playAssistant(clod, 5));

        assertThrows(SameAssistantPlayedException.class, () -> playAssistant(giuse, 5));
        assertDoesNotThrow(() -> playAssistant(giuse, 7));

        assertEquals(3, firstRound.getPlayedAssistants().size());
        //Now the PlanningPhase has ended and the PlayerActionPhase of clod has been created
        PlayerActionPhase pap = firstRound.getCurrentPlayerActionPhase();
        assertEquals(Constants.ACTION_MOVE_STUDENT, firstRound.getCurrentPlayerActionPhase().getExpectedAction());
        assertEquals(clod, pap.getCurrentPlayer());

        //Now several clients can call PlayerActionPhase
        assertDoesNotThrow(() -> pap.moveStudent(Color.GREEN, clod.getDashboard().getDiningRoom()));
        assertTrue(clod.hasProfessor(Color.GREEN));
        assertDoesNotThrow(() -> pap.moveStudent(Color.GREEN, clod.getDashboard().getDiningRoom()));
        assertThrows(InvalidActionException.class,
                () -> pap.moveMotherNature(1),
                "Move your students first"
        );
        assertThrows(InvalidStudentException.class,
                () -> pap.moveStudent(Color.PINK, clod.getDashboard().getDiningRoom())
        );
        assertThrows(InvalidActionException.class,
                () -> pap.chooseCloud(1),
                "Move your students first"
        );

        assertDoesNotThrow(() -> pap.moveStudent(Color.BLUE, clod.getDashboard().getDiningRoom()));
        assertTrue(clod.hasProfessor(Color.BLUE));
        assertDoesNotThrow(() -> pap.moveStudent(Color.BLUE, game.getGameBoard().getIslands().get(1)));

        assertEquals(2, Students.countColor(clod.getDashboard().getDiningRoom().getStudents(), Color.GREEN));
        assertEquals(3, Students.countColor(clod.getDashboard().getEntrance().getStudents(), Color.GREEN));
        assertEquals(2, Students.countColor(clod.getDashboard().getEntrance().getStudents(), Color.BLUE));
        assertEquals(1, Students.countColor(clod.getDashboard().getDiningRoom().getStudents(), Color.BLUE));

        assertThrows(InvalidActionException.class,
                () -> pap.moveStudent(Color.BLUE, clod.getDashboard().getDiningRoom()),
                "You have already moved 4 students"
        );
        assertThrows(InvalidStepsForMotherNatureException.class,
                () -> pap.moveMotherNature(4),
                "Invalid move for mother nature"
        );
        assertThrows(InvalidActionException.class,
                () -> pap.chooseCloud(1),
                "Move mother nature first"
        );
        assertThrows(InvalidStepsForMotherNatureException.class,
                () -> pap.moveMotherNature(-1),
                "Invalid move for mother nature"
        );
        assertDoesNotThrow(() -> pap.moveMotherNature(1));
        assertEquals(1, game.getGameBoard().getMotherNatureIndex());
        assertEquals(clod.getTowerColor(), game.getGameBoard().getIslands().get(1).getTowerColor());
        assertEquals(1, Students.countColor(game.getGameBoard().getIslands().get(1).getStudents(), Color.BLUE));

        assertThrows(InvalidCloudException.class,
                () -> pap.chooseCloud(4),
                "The selected cloud is not valid"
        );
        assertDoesNotThrow(() -> pap.chooseCloud(1));
        assertTrue(game.getGameBoard().getClouds().get(1).isEmpty());
        assertEquals(9, clod.getDashboard().getEntrance().getStudents().size());

        // It's Giuse's turn
        assertEquals(giuse, game.getCurrentRound().getCurrentPlayerActionPhase().getCurrentPlayer());
        PlayerActionPhase pap2 = game.getCurrentRound().getCurrentPlayerActionPhase();
        // Check merging of islands
        for (int i = 0; i < 2; i++) {
            assertDoesNotThrow(() -> pap2.moveStudent(Color.BLUE, giuse.getDashboard().getDiningRoom()));
            assertDoesNotThrow(() -> pap2.moveStudent(Color.PINK, giuse.getDashboard().getDiningRoom()));
        }
        assertTrue(giuse.hasProfessor(Color.BLUE));
        assertFalse(clod.hasProfessor(Color.BLUE));
        assertDoesNotThrow(() -> pap2.moveMotherNature(3));
        assertEquals(4, game.getGameBoard().getMotherNatureIndex());
        assertEquals(giuse.getTowerColor(), game.getGameBoard().getIslands().get(4).getTowerColor());
        assertEquals(1, Students.countColor(game.getGameBoard().getIslands().get(4).getStudents(), Color.PINK));
        assertThrows(InvalidCloudException.class,
                () -> pap2.chooseCloud(1),
                "The selected cloud is not valid"
        );
        assertEquals(5, giuse.getDashboard().getEntrance().getStudents().size());
        assertDoesNotThrow(() -> pap2.chooseCloud(2));
        assertTrue(game.getGameBoard().getClouds().get(2).isEmpty());
        assertEquals(9, giuse.getDashboard().getEntrance().getStudents().size());

        // Now it's Rick's turn
        PlayerActionPhase pap3 = game.getCurrentRound().getCurrentPlayerActionPhase();
        assertEquals(rick, pap3.getCurrentPlayer());

        assertThrows(InvalidActionException.class, () -> playAssistant(rick, 9));

        for (int i = 0; i < 2; i++) {
            assertDoesNotThrow(() -> pap3.moveStudent(Color.PINK, rick.getDashboard().getDiningRoom()));
        }
        assertFalse(rick.hasProfessor(Color.PINK));
        assertDoesNotThrow(() -> pap3.moveStudent(Color.PINK, rick.getDashboard().getDiningRoom()));
        assertTrue(rick.hasProfessor(Color.PINK));
        assertDoesNotThrow(() -> pap3.moveStudent(Color.GREEN, rick.getDashboard().getDiningRoom()));
        assertFalse(rick.hasProfessor(Color.GREEN));

        assertDoesNotThrow(() -> pap3.moveMotherNature(4));
        assertEquals(rick.getTowerColor(), game.getGameBoard().getIslands().get(8).getTowerColor());

        assertDoesNotThrow(() -> pap3.chooseCloud(0));
        assertEquals(9, rick.getDashboard().getEntrance().getStudents().size());

        Round secondRound = game.getCurrentRound();
        assertNotEquals(firstRound, secondRound);
        // We are in the PlanningPhase of the second round, clod has to start
        assertThrows(
                InvalidActionException.class,
                () -> playAssistant(rick, 5)
        );

        assertThrows(
                AlreadyPlayedAssistantException.class,
                () -> playAssistant(clod, 5)
        );
        assertDoesNotThrow(() -> playAssistant(clod, 2));
        assertThrows(
                SameAssistantPlayedException.class,
                () -> playAssistant(giuse, 2)
        );
        assertDoesNotThrow(() -> playAssistant(giuse, 3));
        assertDoesNotThrow(() -> playAssistant(rick, 1));

        // Now it's up to rick, then giuse and eventually clod
        assertEquals(rick, secondRound.getCurrentPlayerActionPhase().getCurrentPlayer());
        // Actions are the same as before, maybe test merging of two Islands
    }

    /**
     * Plays an {@code Assistant} from the hand of the {@code Player}
     *
     * @param player           the {@code Island} who plays the {@code Island}
     * @param indexOfAssistant the index of the {@code Island} to play
     * @throws InvalidActionException          if the player who is trying to play the {@code Assistant} is not the one actually
     *                                         playing this turn
     * @throws AlreadyPlayedAssistantException if the player who is trying to play the {@code Assistant} has already
     *                                         played it
     * @throws SameAssistantPlayedException    if the {@code Assistant} who is trying to be played has already been played
     *                                         by someone else this turn
     */
    private void playAssistant(Player player, int indexOfAssistant) throws InvalidActionException, AlreadyPlayedAssistantException, SameAssistantPlayedException {
        game.getCurrentRound().getPlanningPhase().addAssistant(player.getHand()[indexOfAssistant]);

        // If the code arrives here it means that the Exception hasn't been thrown
        assertNull(player.getHand()[indexOfAssistant]);
    }

    /**
     * Sets up the game to start: in a game of three, every player has a {@code Wizard}, has 10 {@code Assistant} cards,
     * has no students in their {@code DiningRoom} and has 9 students in their {@code Entrance}
     */
    private void setupGame() {

        assertEquals(players.size(), game.getGameBoard().getClouds().size());

        assertSame(players.get(0).getTowerColor(), TowerColor.WHITE);
        assertSame(players.get(1).getTowerColor(), TowerColor.BLACK);
        assertSame(players.get(2).getTowerColor(), TowerColor.GREY);

        checkInitialDashboard();

        for (Color color : Color.values()) {
            assertNull(game.getGameBoard().getOwnerOfProfessor(color));
        }
        assertEquals(5, game.getGameBoard().getColorsOfOwnedProfessors(null).size());

        for (Player player : players) {
            assertEquals(0, player.getDashboard().getDiningRoom().getStudents().size());
            assertEquals(0, player.getDashboard().getEntrance().getStudents().size());
            assertEquals(0, player.getColorsOfOwnedProfessors().size());
        }

        players.get(0).pickWizard(Wizard.KING);
        assertEquals(Wizard.KING, rick.getWizard());
        players.get(1).pickWizard(Wizard.MAGE);
        players.get(2).pickWizard(Wizard.MONK);

        TestGameFactory.fillThreeEntrances(rick, clod, giuse);

    }

    /**
     * Puts a pink {@code Student} on every {@code Island} except the one where Mother Nature is and the opposite one
     *
     * @param mnIndex the index of the {@code Island} where MotherNature is
     */
    private void prepareIslands(int mnIndex) throws InvalidStudentException, InvalidActionException {
        ArrayList<Island> islands = game.getGameBoard().getIslands();
        assertEquals(12, islands.size());
        for (int i = 0; i < 12; i++) {
            Island island = islands.get(i);
            if (island.getStudents().size() == 1) {
                island.giveStudent(bag, island.getStudents().get(0));
            }
            if (i != 0 && i != 6) {
                island.receiveStudent(new Student(Color.PINK));
            }
        }
        checkIslands();
    }

    /**
     * Checks if the set-up of the islands has been done correctly
     */
    private void checkIslands() {
        ArrayList<Island> islands = game.getGameBoard().getIslands();
        assertEquals(12, islands.size());
        for (int i = 0; i < 12; i++) {
            Island island = islands.get(i);
            if (i == 0 || i == 6) {
                assertEquals(0, island.getStudents().size());
            } else {
                assertEquals(1, island.getStudents().size());
            }
            assertEquals(0, island.getNoEntryNum());
            assertNull(island.getTowerColor());
        }
    }

    /**
     * Checks if the initial state of the {@code Dashboard} is correct
     */
    private void checkInitialDashboard() {
        for (Player player : players) {
            assertEquals(10, player.getHand().length);
            for (int i = 1; i <= 10; i++) {
                assertEquals(i, player.getHand()[i - 1].getValue());
                assertEquals(i / 2 + i % 2, player.getHand()[i - 1].getNumSteps());
            }
            assertEquals(1, player.getNumCoins());
            assertEquals(6, player.getRemainingTowers());
        }
    }

}