package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.exceptions.*;
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
    public void useEffect(PlayerActionPhase currentPlayerActionPhase, Island island, Color color, ArrayList<Student> srcStudents, ArrayList<Student> dstStudents)
            throws InvalidCharacterException, StudentNotOnTheCardException, InvalidActionException, InvalidStudentException {

        switch (this.getCardName()) {
            case move1FromCardToIsland -> {
                moveStudentAwayFromCard(island, srcStudents);
            }
            case move1FromCardToDining -> {
                moveStudentAwayFromCard(currentPlayerActionPhase.getCurrentPlayer().getDashboard().getDiningRoom(), srcStudents);
            }
            case swapUpTo3FromEntranceToCard -> {
                Entrance curEntrance = currentPlayerActionPhase.getCurrentPlayer().getDashboard().getEntrance();
                if (!this.students.containsAll(dstStudents)) {
                    throw new InvalidActionException("One or more students are not on the card");
                }
                if (!curEntrance.getStudents().containsAll(srcStudents)) {
                    throw new InvalidActionException("One or more students are not on the entrance");
                }
                swapStudents(curEntrance, this, srcStudents, dstStudents);
            }
            case swapUpTo2FromEntranceToDiningRoom -> {
                Dashboard curDashBoard = currentPlayerActionPhase.getCurrentPlayer().getDashboard();
                if (!curDashBoard.getEntrance().getStudents().containsAll(srcStudents)) {
                    throw new InvalidActionException("One or more students are not on the entrance");
                }
                if (!curDashBoard.getDiningRoom().getStudents().containsAll(dstStudents)) {
                    throw new InvalidActionException("One or more students are not on the dining room");
                }
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

    private void swapStudents(Place src, Place dst, ArrayList<Student> srcStudents, ArrayList<Student> dstStudents) throws InvalidActionException, InvalidStudentException {
        if (srcStudents.size() > numStudents) {
            throw new InvalidActionException("You can move up to two students");
        }
        if (srcStudents.size() != dstStudents.size()) {
            throw new InvalidActionException("This is not a valid swap");
        }
        for (int i = 0; i < srcStudents.size(); i++) {
            Student firstStudentToSwap = srcStudents.get(i);
            Student secondStudentToSwap = dstStudents.get(i);
            if (firstStudentToSwap == null || secondStudentToSwap == null)
                break;
            src.giveStudent(dst, firstStudentToSwap);
            dst.giveStudent(src, secondStudentToSwap);
        }
    }

    private void moveStudentAwayFromCard(Place destination, ArrayList<Student> srcStudents) throws StudentNotOnTheCardException, InvalidActionException {
        if (!students.containsAll(srcStudents)) {
            throw new StudentNotOnTheCardException("The student is not on the card");
        }
        if (srcStudents.size() != 1) {
            throw new InvalidActionException("You can move up to two students");
        }
        for (int i = 0; i < numStudents; i++) {
            this.giveStudent(destination, srcStudents.get(i));
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
