package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.ProfessorAlreadyPresentException;
import it.polimi.ingsw.exceptions.ProfessorNotFoundException;

import java.util.ArrayList;

public class ProfessorRoom {
    private final ArrayList<Professor> professors;

    public ProfessorRoom() {
        professors = new ArrayList<>();
    }

    /**
     * Transfers the professor from the object to destination
     * @param color the input {@code Color}
     * @param destination the destination professor room - of another player
     * @throws ProfessorNotFoundException if the object doesn't contain a professor of the input color
     */
    public void giveProfessor(Color color, ProfessorRoom destination) throws ProfessorNotFoundException{
        for (Professor p: professors) {
            if(p.getColor() == color){
                try{
                    destination.takeProfessor(p);
                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println(e.getMessage());
                }
                professors.remove(p);
                return;
            }
        }
        throw new ProfessorNotFoundException("You don't own this professor");
    }

    /**
     * Adds a professor to the collection of the object
     * @param professor The {@code Professor} to be added
     * @throws ProfessorAlreadyPresentException if the input professor is already present in the object
     */
    public void takeProfessor(Professor professor) throws ProfessorAlreadyPresentException{
        if (hasProfessorOfColor(professor.getColor())) throw new ProfessorAlreadyPresentException("You already own this professor");
        professors.add(professor);
    }

    /**
     * @param color The {@code Color} of the {@code Professor} we want to know if it is present in the object
     * @return true if the {@code ProfessorRoom} contains the {@code Professor} of the given {@code Color}
     */
    public boolean hasProfessorOfColor(Color color){
        for (Professor p : professors) {
            if(p.getColor() == color)
                return true;
        }
        return false;
    }
}
