package it.polimi.ingsw.server.game_state;

import it.polimi.ingsw.model.characters.*;
import it.polimi.ingsw.model.characters.Character;
import it.polimi.ingsw.model.game_objects.Student;
import it.polimi.ingsw.model.game_objects.gameboard_objects.GameBoard;

import java.util.ArrayList;
import java.util.List;

public class CharacterState {
    private final CharacterName characterName;
    private final int cost;
    private final boolean hasCoin;
    private final ArrayList<Student> students;  // is null if the character doesn't have students
    private final int noEntryNum;

    public CharacterState(CharacterName characterName, int cost, boolean hasCoin, ArrayList<Student> students, int noEntryNum) {
        this.characterName = characterName;
        this.cost = cost;
        this.hasCoin = hasCoin;
        this.students = students;
        this.noEntryNum = noEntryNum;
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
                ) ? ((MovingCharacter) character).getStudents() : null,
                (character.getCardName().equals(CharacterName.noEntry)) ? ((NoEntryCharacter) character).getNoEntryNum() : 0
        );
    }

    /**
     * Loads the game characters in a format compatible with the model's GameBoard
     *
     * @param loadedGame the game state from which to load the characters
     * @param gameBoard  the gameBoard object which will host the loaded characters
     * @return an array of three characters
     */
    public static Character[] loadCharacters(GameState loadedGame, GameBoard gameBoard) {
        List<Character> characterList = new ArrayList<>();
        for (CharacterState characterState : loadedGame.getCharacters()) {
            characterList.add(loadCharacter(characterState, loadedGame, gameBoard));
        }

        return characterList.toArray(Character[]::new);
    }

    /**
     * Load a Character from a character state
     *
     * @param characterState the {@code CharacterState} from which to load the {@code Character}
     * @param loadedGame     the game from which the character has to be loaded
     * @param gameBoard      the gameBoard object which will host the loaded character
     * @return the loaded {@code Character}
     */
    private static Character loadCharacter(CharacterState characterState, GameState loadedGame, GameBoard gameBoard) {
        switch (characterState.characterName) {
            case plus2MNMoves, takeProfWithEqualStudents, plus2Influence, ignoreTowers, ignoreColor -> {
                return new PassiveCharacter(characterState.characterName, characterState.hasCoin);
            }
            case move1FromCardToIsland -> {
                return new MovingCharacter(CharacterName.move1FromCardToIsland, gameBoard, 4, 1, characterState.hasCoin, characterState.students);
            }
            case swapUpTo2FromEntranceToDiningRoom -> {
                return new MovingCharacter(CharacterName.swapUpTo2FromEntranceToDiningRoom, gameBoard, 0, 2, characterState.hasCoin, new ArrayList<>());
            }
            case swapUpTo3FromEntranceToCard -> {
                return new MovingCharacter(CharacterName.swapUpTo3FromEntranceToCard, gameBoard, 6, 3, characterState.hasCoin, characterState.students);
            }
            case move1FromCardToDining -> {
                return new MovingCharacter(CharacterName.move1FromCardToDining, gameBoard, 4, 1, characterState.hasCoin, characterState.students);
            }
            case resolveIsland -> {
                return new ResolveIslandCharacter(CharacterName.resolveIsland, gameBoard, characterState.hasCoin);
            }
            case noEntry -> {
                return new NoEntryCharacter(CharacterName.noEntry, gameBoard, characterState.hasCoin, characterState.noEntryNum);
            }
            case everyOneMove3FromDiningRoomToBag -> {
                return new EveryOneMovesCharacter(CharacterName.everyOneMove3FromDiningRoomToBag, gameBoard, characterState.hasCoin);
            }
            default -> throw new RuntimeException();
        }
    }

    public CharacterName getCharacterName() {
        return characterName;
    }

    public int getCost() {
        return cost;
    }

    public boolean hasCoin() {
        return hasCoin;
    }

    public ArrayList<Student> getStudents() {
        return students;
    }

    public int getNoEntryNum() {
        return noEntryNum;
    }
}
