package it.polimi.ingsw.model.characters;

public enum CharacterName {
    move1FromCardToIsland(1, CharacterDescriptions.DESC_MOVE_1_FROM_CARD_TO_ISLAND),
    takeProfWithEqualStudents(2, CharacterDescriptions.DESC_TAKE_PROF_WITH_EQUAL_STUDENTS),
    resolveIsland(3, CharacterDescriptions.DESC_RESOLVE_ISLAND),
    plus2MNMoves(1, CharacterDescriptions.DESC_PLUS_2_MN_MOVE),
    noEntry(2, CharacterDescriptions.DESC_NO_ENTRY),
    ignoreTowers(3, CharacterDescriptions.DESC_IGNORE_TOWERS),
    swapUpTo3FromEntranceToCard(1, CharacterDescriptions.DESC_SWAP_UP_TO_3),
    swapUpTo2FromEntranceToDiningRoom(1, CharacterDescriptions.DESC_SWAP_UP_TO_2),
    plus2Influence(2, CharacterDescriptions.DESC_PLUS_2_INFLUENCE),
    move1FromCardToDining(2, CharacterDescriptions.DESC_MOVE_1_TO_DINING),
    ignoreColor(3, CharacterDescriptions.DESC_IGNORE_COLOR),
    everyOneMove3FromDiningRoomToBag(3, CharacterDescriptions.DESC_EVERYONE_MOVES);

    private final int initialCost;
    private final String description;

    CharacterName(int initialCost, String description) {
        this.initialCost = initialCost;
        this.description = description;
    }

    public int getInitialCost() {
        return initialCost;
    }

    public String getDescription() {
        return description;
    }
}
