package it.polimi.ingsw.client.observers.choices.action;

import it.polimi.ingsw.client.MessageHandler;

public class SendActionChoiceObserver implements ActionChoiceObserver {

    private final MessageHandler mh;

    public SendActionChoiceObserver(MessageHandler mh) {
        this.mh = mh;
        mh.attachActionChoiceObserver(this);
    }

    @Override
    public void onActionChosen(String action) {
        mh.handleAction(action);
    }
}
