package it.polimi.ingsw.model;

import it.polimi.ingsw.exceptions.InvalidCharacterException;

public class NoEntryCharacter extends ActiveCharacter {
    private int noEntryNum;

    public NoEntryCharacter(CardName cardName) {
        super(cardName);
        noEntryNum = 4;
    }

    public NoEntryCharacter(CardName cardName, int noEntryNum) {
        super(cardName);
        this.noEntryNum = noEntryNum;
    }

    public void removeNoEntry() throws InvalidCharacterException {
        if (noEntryNum == 0) throw new InvalidCharacterException("Nessun segnalino no entry rimasto");
        noEntryNum--;
    }

    public void addNoEntry() {
        noEntryNum++;
    }
}
