package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.languages.Messages;

public enum CharacterName {
    move1FromCardToIsland(Messages.getMessage("monk"), 1, Messages.getMessage("desc_move_1_from_card_to_island")),
    takeProfWithEqualStudents(Messages.getMessage("peasant"), 2, Messages.getMessage("desc_take_prof_with_equal_students")),
    resolveIsland(Messages.getMessage("herald"), 3, Messages.getMessage("desc_resolve_island")),
    plus2MNMoves(Messages.getMessage("magic_postman"), 1, Messages.getMessage("desc_plus_2_mn_move")),
    noEntry(Messages.getMessage("herb_granny"), 2, Messages.getMessage("desc_no_entry")),
    ignoreTowers(Messages.getMessage("centaur"), 3, Messages.getMessage("desc_ignore_towers")),
    swapUpTo3FromEntranceToCard(Messages.getMessage("jester"), 1, Messages.getMessage("desc_swap_up_to_3")),
    swapUpTo2FromEntranceToDiningRoom(Messages.getMessage("minstrel"), 1, Messages.getMessage("desc_swap_up_to_2")),
    plus2Influence(Messages.getMessage("knight"), 2, Messages.getMessage("desc_plus_2_influence")),
    move1FromCardToDining(Messages.getMessage("spoiled_princess"), 2, Messages.getMessage("desc_move_1_to_dining")),
    ignoreColor(Messages.getMessage("fungus_man"), 3, Messages.getMessage("desc_ignore_color")),
    everyOneMove3FromDiningRoomToBag(Messages.getMessage("thief"), 3, Messages.getMessage("desc_everyone_moves"));

    private final String name;
    private final int initialCost;
    private final String description;

    CharacterName(String name, int initialCost, String description) {
        this.name = name;
        this.initialCost = initialCost;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public int getInitialCost() {
        return initialCost;
    }

    public String getDescription() {
        return description;
    }
}
