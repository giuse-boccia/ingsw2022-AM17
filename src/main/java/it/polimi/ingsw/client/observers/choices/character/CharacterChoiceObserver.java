package it.polimi.ingsw.client.observers.choices.character;

import it.polimi.ingsw.model.characters.CharacterName;

import java.io.IOException;

public interface CharacterChoiceObserver {

    void onCharacterChosen(CharacterName name) throws IOException;

}
