package it.polimi.ingsw.client.observers.choices.character;

import it.polimi.ingsw.client.ActionHandler;
import it.polimi.ingsw.client.MessageHandler;
import it.polimi.ingsw.model.characters.CharacterName;

import java.io.IOException;

public class SendCharacterChoiceObserver implements CharacterChoiceObserver {

    private final MessageHandler mh;

    public SendCharacterChoiceObserver(MessageHandler mh) {
        this.mh = mh;
        mh.attachCharacterChoiceObserver(this);
    }

    @Override
    public void onCharacterChosen(CharacterName name) throws IOException {
        ActionHandler.handleCharacterPlayed(name, mh.getNetworkClient());
    }
}
