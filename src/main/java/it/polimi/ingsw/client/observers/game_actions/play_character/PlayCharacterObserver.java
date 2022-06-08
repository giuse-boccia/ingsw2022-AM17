package it.polimi.ingsw.client.observers.game_actions.play_character;

import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.model.game_objects.Color;

import java.util.List;

public interface PlayCharacterObserver {

    /**
     * This method is triggered when a player chooses a character to play
     *
     * @param name        the name of the character to play
     * @param color       the color needed for the effect of the character to be activated
     * @param island      the island needed for the effect of the character to be activated
     * @param srcStudents the students to eventually move/swap
     * @param dstStudents the students to eventually swap with srcStudents
     */
    void onCharacterPlayed(CharacterName name, Color color, Integer island, List<Color> srcStudents, List<Color> dstStudents);

}
