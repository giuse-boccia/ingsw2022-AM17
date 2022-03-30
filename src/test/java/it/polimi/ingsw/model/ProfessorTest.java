package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.ProfessorAlreadyPresentException;
import it.polimi.ingsw.model.character.CharacterName;
import it.polimi.ingsw.model.character.PassiveCharacter;
import it.polimi.ingsw.model.game_actions.action_phase.PlayerActionPhase;
import it.polimi.ingsw.model.game_actions.action_phase.PlayerActionPhaseFactory;
import it.polimi.ingsw.model.game_objects.*;
import it.polimi.ingsw.model.game_objects.dashboard_objects.DiningRoom;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test professor strategies
 */
class ProfessorTest {
    Game g = TestGameFactory.getNewGame();
    GameBoard gb = g.getGameBoard();

    @Test
    public void professorDefaultTest() throws ProfessorAlreadyPresentException {
        Player rick = g.getPlayers().get(0);        // Rick is player 0
        Player clod = g.getPlayers().get(1);        // Clod is player 1

        DiningRoom dr0 = rick.getDashboard().getDiningRoom();   // Rick's dining room
        DiningRoom dr1 = clod.getDashboard().getDiningRoom();   // Clod's dining room

        // Rick's turn
        Assistant a = new Assistant(3, 6, rick);
        PlayerActionPhase pap = PlayerActionPhaseFactory.createPlayerActionPhase(a, gb, true);

        // Clod has green professor and 1 green student
        clod.getDashboard().getProfessorRoom().takeProfessor(new Professor(Color.GREEN));
        clod.getDashboard().getDiningRoom().receiveStudent(new Student(Color.GREEN));

        assertEquals(1, dr1.getNumberOfStudentsOfColor(Color.GREEN));
        assertTrue(clod.getDashboard().getProfessorRoom().hasProfessorOfColor(Color.GREEN));

        // Rick has 2 green students on his entrance
        Student s1 = new Student(Color.GREEN);
        Student s2 = new Student(Color.GREEN);
        rick.getDashboard().getEntrance().receiveStudent(s1);
        rick.getDashboard().getEntrance().receiveStudent(s2);
        assertEquals(2, rick.getDashboard().getEntrance().getStudents().size());

        // Rick moves one of them to his dining room
        rick.moveStudent(rick.getDashboard().getEntrance(), dr0, s1);

        // Rick can't steal the Green Professor from Clod
        assertEquals(1, dr0.getNumberOfStudentsOfColor(Color.GREEN));
        assertEquals(1, dr1.getNumberOfStudentsOfColor(Color.GREEN));
        assertFalse(pap.canStealProfessor(Color.GREEN, dr0, dr1));

        // Rick moves another green student to his dining room
        rick.moveStudent(rick.getDashboard().getEntrance(), dr0, s2);

        // Rick can now steal the Green Professor from Clod
        assertEquals(2, dr0.getNumberOfStudentsOfColor(Color.GREEN));
        assertTrue(pap.canStealProfessor(Color.GREEN, dr0, dr1));
    }

    @Test
    public void professorOnDrawTest() throws ProfessorAlreadyPresentException {
        Player rick = g.getPlayers().get(0);        // Rick is player 0
        Player clod = g.getPlayers().get(1);        // Clod is player 1

        DiningRoom dr0 = rick.getDashboard().getDiningRoom();   // Rick's dining room
        DiningRoom dr1 = clod.getDashboard().getDiningRoom();   // Clod's dining room

        // Rick's turn
        Assistant a = new Assistant(3, 6, rick);
        PlayerActionPhase pap = PlayerActionPhaseFactory.createPlayerActionPhase(a, gb, true);

        // Rick plays the takeProfWithEqualStudents character
        PassiveCharacter pc = new PassiveCharacter(CharacterName.takeProfWithEqualStudents);
        pap.playPassiveCharacter(pc);

        // Clod has green professor and 1 green student
        clod.getDashboard().getProfessorRoom().takeProfessor(new Professor(Color.GREEN));
        clod.getDashboard().getDiningRoom().receiveStudent(new Student(Color.GREEN));

        assertEquals(1, dr1.getNumberOfStudentsOfColor(Color.GREEN));
        assertTrue(clod.getDashboard().getProfessorRoom().hasProfessorOfColor(Color.GREEN));

        // Rick has 2 green students on his entrance
        Student s1 = new Student(Color.GREEN);
        Student s2 = new Student(Color.GREEN);
        rick.getDashboard().getEntrance().receiveStudent(s1);
        rick.getDashboard().getEntrance().receiveStudent(s2);
        assertEquals(2, rick.getDashboard().getEntrance().getStudents().size());

        // Rick moves one of them to his dining room
        rick.moveStudent(rick.getDashboard().getEntrance(), dr0, s1);

        // Rick can steal the Green Professor from Clod (even with draw students)
        assertEquals(1, dr0.getNumberOfStudentsOfColor(Color.GREEN));
        assertEquals(1, dr1.getNumberOfStudentsOfColor(Color.GREEN));
        assertTrue(pap.canStealProfessor(Color.GREEN, dr0, dr1));

        // Rick moves another green student to his dining room
        rick.moveStudent(rick.getDashboard().getEntrance(), dr0, s2);

        // Rick can still steal the Green Professor from Clod
        assertEquals(2, dr0.getNumberOfStudentsOfColor(Color.GREEN));
        assertTrue(pap.canStealProfessor(Color.GREEN, dr0, dr1));
    }
}