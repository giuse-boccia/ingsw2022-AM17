package it.polimi.ingsw.model.character;

import it.polimi.ingsw.exceptions.EmptyBagException;
import it.polimi.ingsw.exceptions.InvalidCharacterException;
import it.polimi.ingsw.model.game_actions.action_phase.PlayerActionPhase;
import it.polimi.ingsw.model.game_objects.*;
import it.polimi.ingsw.model.game_objects.dashboard_objects.Dashboard;
import it.polimi.ingsw.model.game_objects.dashboard_objects.Entrance;

import java.util.ArrayList;

public class MovingCharacter extends GameboardCharacter implements Place {

    private final ArrayList<Student> students;
    private final int numStudents;
    private final int initialStudents;

    public MovingCharacter(CharacterName characterName, GameBoard gb, int initialStudents, int numStudents) {
        super(characterName, gb);
        this.numStudents = numStudents;
        this.initialStudents = initialStudents;
        students = new ArrayList<>();
    }

    @Override
    public void useEffect(PlayerActionPhase currentPlayerActionPhase, Island island, Color color, ArrayList<Student> srcStudents, ArrayList<Student> dstStudents) throws InvalidCharacterException {

        switch (this.getCardName()) {
            case move1FromCardToIsland -> {
                moveStudentAwayFromCard(island, srcStudents);
            }
            case move1FromCardToDining -> {
                moveStudentAwayFromCard(currentPlayerActionPhase.getCurrentPlayer().getDashboard().getDiningRoom(), srcStudents);
            }
            case swapUpTo3FromEntranceToCard -> {
                Entrance curEntrance = currentPlayerActionPhase.getCurrentPlayer().getDashboard().getEntrance();
                swapStudents(curEntrance, this, srcStudents, dstStudents);
            }
            case swapUpTo2FromEntranceToDiningRoom -> {
                Dashboard curDashBoard = currentPlayerActionPhase.getCurrentPlayer().getDashboard();
                swapStudents(curDashBoard.getEntrance(), curDashBoard.getDiningRoom(), srcStudents, dstStudents);
            }
            default -> {
                throw new InvalidCharacterException("This is not a valid character");
            }
        }

        super.addCoinAfterFirstUse();
    }

    public void fillCardFromBag() throws EmptyBagException {
        Bag bag = getGameBoard().getBag();
        while (students.size() < initialStudents) {
            bag.giveStudent(this, bag.getRandStudent());
        }
    }

    private void swapStudents(Place src, Place dst, ArrayList<Student> srcStudents, ArrayList<Student> dstStudents) {
        for (int i = 0; i < srcStudents.size(); i++) {
            Student firstStudentToSwap = srcStudents.get(i);
            Student secondStudentToSwap = dstStudents.get(i);
            if (firstStudentToSwap == null || secondStudentToSwap == null)
                break;
            src.giveStudent(dst, firstStudentToSwap);
            dst.giveStudent(src, secondStudentToSwap);
        }
    }

    private void moveStudentAwayFromCard(Place destination, ArrayList<Student> srcStudents) {
        for (int i = 0; i < srcStudents.size(); i++) {
            giveStudent(destination, srcStudents.get(i));
        }
    }

    @Override
    public void giveStudent(Place destination, Student student) {
        students.remove(student);
        destination.receiveStudent(student);
    }

    @Override
    public void receiveStudent(Student student) {
        students.add(student);
    }

    public ArrayList<Student> getStudents() {
        return students;
    }

    public int getNumStudents() {
        return numStudents;
    }

    public int getInitialStudents() {
        return initialStudents;
    }
}
