package it.polimi.ingsw.client.observers.game_actions.move_mn;

import it.polimi.ingsw.client.MessageHandler;
import it.polimi.ingsw.utils.constants.Messages;
import it.polimi.ingsw.messages.action.Action;
import it.polimi.ingsw.messages.action.ActionArgs;
import it.polimi.ingsw.messages.action.ClientActionMessage;

public class SendMoveMNObserver implements MoveMNObserver {

    private final MessageHandler mh;

    public SendMoveMNObserver(MessageHandler mh) {
        this.mh = mh;
        mh.attachMoveMNObserver(this);
    }


    @Override
    public void onMotherNatureMoved(int numSteps) {
        ActionArgs args = new ActionArgs();
        args.setNum_steps(numSteps);

        Action action = new Action(Messages.ACTION_MOVE_MN, args);

        ClientActionMessage toSend = new ClientActionMessage();
        toSend.setAction(action);
        toSend.setPlayer(mh.getNetworkClient().getClient().getUsername());

        mh.getNetworkClient().sendMessageToServer(toSend.toJson());
    }
}
