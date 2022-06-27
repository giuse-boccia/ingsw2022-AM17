package it.polimi.ingsw.client.observers.choices.character;

import it.polimi.ingsw.model.characters.CharacterName;

import java.io.IOException;

public interface CharacterChoiceObserver {

    /**
     * This method is triggered when a player - in Gui - chooses a character to play
     *
     * @param name the name of the chosen character
     */
    void onCharacterChosen(CharacterName name) throws IOException;

}
