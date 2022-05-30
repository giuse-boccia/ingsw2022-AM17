package it.polimi.ingsw.client;

import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.model.game_objects.Color;

import java.io.IOException;
import java.util.List;

public interface Observer {

    // TODO: separate into 2 methods
    void sendLoginParameters(String username, Integer numPlayers, Boolean isExpert);

    void sendLoadGame();

    void sendActionParameters(String actionName, Color color, Integer island,
                              Integer num_steps, Integer cloud, Integer value,
                              CharacterName characterName, List<Color> sourceStudents, List<Color> dstStudents);

    void sendActionName(String action);

    void sendCharacterName(CharacterName name) throws IOException;
}
