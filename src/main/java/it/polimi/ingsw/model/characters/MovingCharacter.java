package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.constants.Messages;
import it.polimi.ingsw.exceptions.*;
import it.polimi.ingsw.model.Place;
import it.polimi.ingsw.model.game_actions.PlayerActionPhase;
import it.polimi.ingsw.model.game_objects.*;
import it.polimi.ingsw.model.game_objects.dashboard_objects.Dashboard;
import it.polimi.ingsw.model.game_objects.dashboard_objects.Entrance;
import it.polimi.ingsw.model.game_objects.gameboard_objects.Bag;
import it.polimi.ingsw.model.game_objects.gameboard_objects.GameBoard;
import it.polimi.ingsw.model.game_objects.gameboard_objects.Island;
import it.polimi.ingsw.model.utils.Students;

import java.util.ArrayList;
import java.util.List;

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

    /**
     * Uses the correct effect of one {@code MovingCharacter} accordingly to its name and moves the scrStudents to the
     * correct destination
     *
     * @param currentPlayerActionPhase the {@code PlayerActionPhase} which the effect is used in
     * @param island                   the {@code Island} which the {@code Character} affects
     * @param color                    the {@code Color} which the {@code Character} affects
     * @param srcColors                the students to be moved to the destination
     * @param dstColors                the students to be moved to the source (only if the effect is a "swap" effect)
     * @throws InvalidCharacterException    if the {@code Character} is not valid
     * @throws StudentNotOnTheCardException if the {@code Student} is not on the card
     * @throws InvalidActionException       if the action is not valid
     * @throws InvalidStudentException      if the {@code Student} is not valid
     */
    @Override
    public void useEffect(PlayerActionPhase currentPlayerActionPhase, Island island, Color color, List<Color> srcColors, List<Color> dstColors) throws InvalidCharacterException, StudentNotOnTheCardException, InvalidActionException, InvalidStudentException, EmptyBagException {

        switch (this.getCardName()) {
            case move1FromCardToIsland -> {
                moveStudentAwayFromCard(island, srcColors);
                fillCardFromBag();
            }

            case move1FromCardToDining -> {
                moveStudentAwayFromCard(currentPlayerActionPhase.getCurrentPlayer().getDashboard().getDiningRoom(), srcColors);
                fillCardFromBag();
            }

            case swapUpTo3FromEntranceToCard -> {
                Entrance curEntrance = currentPlayerActionPhase.getCurrentPlayer().getDashboard().getEntrance();
                ArrayList<Student> srcStudents = getStudentListFromColorList(srcColors, curEntrance);
                ArrayList<Student> dstStudents = getStudentListFromColorList(dstColors, this);
                if (!this.students.containsAll(dstStudents)) {
                    throw new StudentNotOnTheCardException(Messages.STUDENT_NOT_ON_CARD);
                }
                if (!curEntrance.getStudents().containsAll(srcStudents)) {
                    throw new InvalidActionException("One or more students are not yet in the entrance");
                }
                swapStudents(curEntrance, this, srcStudents, dstStudents);
            }
            case swapUpTo2FromEntranceToDiningRoom -> {
                Dashboard curDashBoard = currentPlayerActionPhase.getCurrentPlayer().getDashboard();
                ArrayList<Student> srcStudents = getStudentListFromColorList(srcColors, curDashBoard.getEntrance());
                ArrayList<Student> dstStudents = getStudentListFromColorList(dstColors, curDashBoard.getDiningRoom());
                if (!curDashBoard.getEntrance().getStudents().containsAll(srcStudents)) {
                    throw new InvalidActionException("One or more students are not on the entrance");
                }
                if (!curDashBoard.getDiningRoom().getStudents().containsAll(dstStudents)) {
                    throw new InvalidActionException("One or more students are not on the dining room");
                }
                swapStudents(curDashBoard.getEntrance(), curDashBoard.getDiningRoom(), srcStudents, dstStudents);
            }
            default -> throw new InvalidCharacterException("This is not a valid character");

        }

        super.addCoinAfterFirstUse();
    }

    /**
     * Fills the selected {@code Character} with the correct number of students from the {@code Bag}
     *
     * @throws EmptyBagException if the {@code Bag} is empty
     */
    public void fillCardFromBag() throws EmptyBagException {
        Bag bag = getGameBoard().getBag();
        while (students.size() < initialStudents) {
            bag.giveStudent(this, bag.getRandStudent());
        }
    }

    /**
     * Moves the scrStudents from the src to the dst and viceversa
     *
     * @param src         the scource of scrStudents
     * @param dst         the destination of srcStudents
     * @param srcStudents the students to be moved from the stc to the dst
     * @param dstStudents the students to be moved from the dst to the scr
     * @throws InvalidActionException  if the action is not valid
     * @throws InvalidStudentException it the student is not valid
     */
    private void swapStudents(Place src, Place dst, ArrayList<Student> srcStudents, ArrayList<Student> dstStudents) throws InvalidActionException, InvalidStudentException {
        if (srcStudents.size() > numStudents) {
            throw new InvalidActionException(Messages.MOVING_MORE_STUDENTS);
        }
        if (srcStudents.size() != dstStudents.size()) {
            throw new InvalidActionException("This is not a valid swap");
        }
        for (int i = 0; i < srcStudents.size(); i++) {
            Student firstStudentToSwap = srcStudents.get(i);
            Student secondStudentToSwap = dstStudents.get(i);
            if (firstStudentToSwap == null || secondStudentToSwap == null) break;
            src.giveStudent(dst, firstStudentToSwap);
            dst.giveStudent(src, secondStudentToSwap);
        }
    }

    /**
     * Moves the scrStudents to the destination
     *
     * @param destination the destination to move the students to
     * @param srcColors   the colors of the students to be moved
     * @throws StudentNotOnTheCardException if the {@code Student} is not on the {@code Character}
     * @throws InvalidActionException       if the action is not valid
     */
    private void moveStudentAwayFromCard(Place destination, List<Color> srcColors) throws StudentNotOnTheCardException, InvalidActionException, InvalidStudentException {
        if (destination == null) {
            throw new InvalidActionException(Messages.INVALID_ARGUMENT);
        }
        ArrayList<Student> srcStudents = getStudentListFromColorList(srcColors, this);
        if (!students.containsAll(srcStudents)) {
            throw new StudentNotOnTheCardException(Messages.STUDENT_NOT_ON_CARD);
        }
        if (srcStudents.size() != 1) {
            throw new InvalidActionException(Messages.MOVE_JUST_ONE);
        }
        for (int i = 0; i < numStudents; i++) {
            this.giveStudent(destination, srcStudents.get(i));
        }
    }

    private ArrayList<Student> getStudentListFromColorList(List<Color> colors, Place source) throws StudentNotOnTheCardException, InvalidActionException {
        if (colors == null) {
            throw new InvalidActionException(Messages.INVALID_ARGUMENT);
        }
        ArrayList<Student> res = new ArrayList<>();
        ArrayList<Student> sourceStudents = source.getStudents();
        for (Color color : colors) {
            Student toAdd = Students.findFirstStudentOfColor(sourceStudents, color);
            if (toAdd == null) throw new StudentNotOnTheCardException(Messages.STUDENT_NOT_ON_CARD);
            res.add(toAdd);
            sourceStudents.remove(toAdd);
        }
        return res;
    }

    @Override
    public void giveStudent(Place destination, Student student) throws InvalidStudentException {
        if (student == null || !students.contains(student)) {
            throw new InvalidStudentException("The character doesn't contain this student");
        }
        students.remove(student);
        destination.receiveStudent(student);
    }

    @Override
    public void receiveStudent(Student student) {
        students.add(student);
    }

    @Override
    public ArrayList<Student> getStudents() {
        return new ArrayList<>(students);
    }

    @Override
    public void setStudents(ArrayList<Student> students) {
        this.students.clear();
        this.students.addAll(students);
    }

    public int getNumStudents() {
        return numStudents;
    }

    public int getInitialStudents() {
        return initialStudents;
    }
}
