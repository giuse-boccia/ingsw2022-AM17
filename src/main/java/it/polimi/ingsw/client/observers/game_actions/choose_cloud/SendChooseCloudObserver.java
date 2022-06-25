package it.polimi.ingsw.client.observers.game_actions.choose_cloud;

import it.polimi.ingsw.client.MessageHandler;
import it.polimi.ingsw.utils.constants.Constants;
import it.polimi.ingsw.messages.action.Action;
import it.polimi.ingsw.messages.action.ActionArgs;
import it.polimi.ingsw.messages.action.ClientActionMessage;

public class SendChooseCloudObserver implements ChooseCloudObserver {

    private final MessageHandler mh;

    public SendChooseCloudObserver(MessageHandler mh) {
        this.mh = mh;
        mh.attachChooseCloudObserver(this);
    }

    @Override
    public void onCloudChosen(int index) {
        ActionArgs args = new ActionArgs();
        args.setCloud(index);

        Action action = new Action(Constants.ACTION_FILL_FROM_CLOUD, args);

        ClientActionMessage toSend = new ClientActionMessage();
        toSend.setAction(action);
        toSend.setPlayer(mh.getNetworkClient().getClient().getUsername());

        mh.getNetworkClient().sendMessageToServer(toSend.toJson());
    }
}
