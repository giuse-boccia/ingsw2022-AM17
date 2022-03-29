package it.polimi.ingsw.model.character;

public enum CharacterName {
    move1FromCardToIsland(1),
    takeProfWithEqualStudents(2),
    resolveIsland(3),
    plus2MNMoves(1),
    noEntry(2),
    ignoreTowers(3),
    swapUpTo3FromEntranceToCard(1),
    swapUpTo2FromEntranceToDiningRoom(1),
    plus2Influence(2),
    move1FromCardToDining(2),
    ignoreColor(3),
    everyOneMove3FromDiningRoomToBag(3);

    private final int initialCost;

    CharacterName(int initialCost) {
        this.initialCost = initialCost;
    }

    public int getInitialCost() {
        return initialCost;
    }
}
