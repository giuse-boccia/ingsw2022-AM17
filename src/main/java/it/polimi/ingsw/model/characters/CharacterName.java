package it.polimi.ingsw.model.characters;

import it.polimi.ingsw.languages.MessageResourceBundle;

public enum CharacterName {
    move1FromCardToIsland(MessageResourceBundle.getMessage("monk"), 1, MessageResourceBundle.getMessage("desc_move_1_from_card_to_island")),
    takeProfWithEqualStudents(MessageResourceBundle.getMessage("peasant"), 2, MessageResourceBundle.getMessage("desc_take_prof_with_equal_students")),
    resolveIsland(MessageResourceBundle.getMessage("herald"), 3, MessageResourceBundle.getMessage("desc_resolve_island")),
    plus2MNMoves(MessageResourceBundle.getMessage("magic_postman"), 1, MessageResourceBundle.getMessage("desc_plus_2_mn_move")),
    noEntry(MessageResourceBundle.getMessage("herb_granny"), 2, MessageResourceBundle.getMessage("desc_no_entry")),
    ignoreTowers(MessageResourceBundle.getMessage("centaur"), 3, MessageResourceBundle.getMessage("desc_ignore_towers")),
    swapUpTo3FromEntranceToCard(MessageResourceBundle.getMessage("jester"), 1, MessageResourceBundle.getMessage("desc_swap_up_to_3")),
    swapUpTo2FromEntranceToDiningRoom(MessageResourceBundle.getMessage("minstrel"), 1, MessageResourceBundle.getMessage("desc_swap_up_to_2")),
    plus2Influence(MessageResourceBundle.getMessage("knight"), 2, MessageResourceBundle.getMessage("desc_plus_2_influence")),
    move1FromCardToDining(MessageResourceBundle.getMessage("spoiled_princess"), 2, MessageResourceBundle.getMessage("desc_move_1_to_dining")),
    ignoreColor(MessageResourceBundle.getMessage("fungus_man"), 3, MessageResourceBundle.getMessage("desc_ignore_color")),
    everyOneMove3FromDiningRoomToBag(MessageResourceBundle.getMessage("thief"), 3, MessageResourceBundle.getMessage("desc_everyone_moves"));

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
