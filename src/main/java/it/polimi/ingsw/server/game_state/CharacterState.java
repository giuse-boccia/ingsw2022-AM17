package it.polimi.ingsw.server.game_state;

import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.model.characters.MovingCharacter;
import it.polimi.ingsw.model.game_objects.Student;

import java.util.ArrayList;

public class CharacterState {
    private final CharacterName characterName;
    private final int cost;
    private boolean hasCoin;
    private ArrayList<Student> students;  // is null if the character doesn't have students

    public CharacterState(CharacterName characterName, int cost, boolean hasCoin, ArrayList<Student> students) {
        this.characterName = characterName;
        this.cost = cost;
        this.hasCoin = hasCoin;
        this.students = students;
    }

    public CharacterState(Character character) {
        this(
                character.getCardName(),
                character.getCost(),
                character.hasCoin(),
                (
                        character.getCardName().equals(CharacterName.move1FromCardToIsland) ||
                                character.getCardName().equals(CharacterName.swapUpTo3FromEntranceToCard) ||
                                character.getCardName().equals(CharacterName.move1FromCardToDining)
                ) ? ((MovingCharacter) character).getStudents() : null
        );
    }

    public CharacterName getCharacterName() {
        return characterName;
    }

    public int getCost() {
        return cost;
    }

    public boolean isHasCoin() {
        return hasCoin;
    }

    public ArrayList<Student> getStudents() {
        return students;
    }

    public void setStudents(ArrayList<Student> students) {
        this.students = students;
    }

    public void setHasCoin(boolean hasCoin) {
        this.hasCoin = hasCoin;
    }
}
