package it.polimi.ingsw.client.observers.login.game_parameters;

import it.polimi.ingsw.client.MessageHandler;
import it.polimi.ingsw.utils.constants.Constants;
import it.polimi.ingsw.messages.login.ClientLoginMessage;

public class SendGameParametersObserver implements GameParametersObserver {

    private final MessageHandler mh;

    public SendGameParametersObserver(MessageHandler mh) {
        this.mh = mh;
        mh.attachGameParametersObserver(this);
    }

    @Override
    public void onGameParametersSet(int numPlayers, boolean isExpert) {
        ClientLoginMessage msg = new ClientLoginMessage();
        msg.setAction(Constants.ACTION_CREATE_GAME);
        msg.setNumPlayers(numPlayers);
        msg.setExpert(isExpert);
        mh.getNetworkClient().sendMessageToServer(msg.toJson());
    }
}
