package it.polimi.ingsw.model;

public enum CardName {
    move1FromCardToIsland(1),
    takeProfWithEqualStudents(2),
    resolveIsland(3),
    plus2MNMoves(1),
    noEntry(2),
    ignoreTowers(3),
    swap3FromEntranceToCard(1),
    swap2FromEntranceToDiningRoom(1),
    plus2Influence(2),
    move1FromCardToDining(2),
    ignoreColor(3),
    everyOneMove3FromDiningRoomToBag(3);

    private final int initialCost;

    CardName(int initialCost) {
        this.initialCost = initialCost;
    }

    public int getInitialCost() {
        return initialCost;
    }
}
