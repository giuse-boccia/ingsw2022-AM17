package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.CharacterAlreadyPlayedException;
import it.polimi.ingsw.exceptions.InvalidCharacterException;

import java.util.ArrayList;

public class Game {
    private final ArrayList<Player> players;
    private final GameBoard gameBoard;
    private final Round currentRound;

    public Game(ArrayList<Player> players) {
        this.players = players;
        gameBoard = new GameBoard(this);
        currentRound = new Round(0);
    }

    private boolean gameEnded(){
        return false;
    }

    public void setup() {

    }

    public void start() {

    }

    public void playCharacter(Character character) throws InvalidCharacterException, CharacterAlreadyPlayedException {
        switch (character.getCardName()) {
            case plus2MNMoves, takeProfWithEqualStudents, plus2Influence, ignoreTowers -> {
                PassiveCharacter passiveCharacter = (PassiveCharacter) character;
                currentRound.getCurrentPlayerActionPhase().playPassiveCharacter(passiveCharacter);
            }
            case move1FromCardToDining, swap3FromEntranceToCard, swap2FromEntranceToDiningRoom -> {
                ActiveCharacter activeCharacter = (ActiveCharacter) character;
                currentRound.getCurrentPlayerActionPhase().playActiveCharacter(activeCharacter);
            }
            default -> throw new InvalidCharacterException("This character requires a Color or an Island");
        }
    }

    public void playCharacter(Character character, Color color) throws InvalidCharacterException, CharacterAlreadyPlayedException {
        switch (character.getCardName()) {
            case ignoreColor -> {
                PassiveCharacter playedCharacter = (PassiveCharacter) character;
                currentRound.getCurrentPlayerActionPhase().playPassiveCharacter(playedCharacter, color);
            }
            case everyOneMove3FromDiningRoomToBag -> {
                MovingCharacter everyOneMovesCharacter = (MovingCharacter) character;
                currentRound.getCurrentPlayerActionPhase().playActiveCharacter(everyOneMovesCharacter, color);
            }
            default -> throw new InvalidCharacterException("This character does not require a Color");
        }
    }

    public void playCharacter(Character character, Island island) throws InvalidCharacterException, CharacterAlreadyPlayedException {
        if (character.getCardName() == CardName.move1FromCardToIsland || character.getCardName() == CardName.resolveIsland || character.getCardName() == CardName.noEntry) {
            ActiveCharacter activeCharacter = (ActiveCharacter) character;
            currentRound.getCurrentPlayerActionPhase().playActiveCharacter(activeCharacter, island);
        } else {
            throw new InvalidCharacterException("This character does not require an Island");
        }
    }

    public ArrayList<Player> getPlayers() {
        return new ArrayList<>(players);
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    public Round getCurrentRound() {
        return currentRound;
    }
}
