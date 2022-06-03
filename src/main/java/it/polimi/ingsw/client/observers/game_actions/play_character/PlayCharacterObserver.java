package it.polimi.ingsw.client.observers.game_actions.play_character;

import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.model.game_objects.Color;

import java.util.List;

public interface PlayCharacterObserver {

    void onCharacterPlayed(CharacterName name, Color color, Integer island, List<Color> srcStudents, List<Color> dstStudents);

}
