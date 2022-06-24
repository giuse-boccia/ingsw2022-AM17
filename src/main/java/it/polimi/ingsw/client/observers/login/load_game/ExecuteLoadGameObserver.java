package it.polimi.ingsw.client.observers.login.load_game;

import it.polimi.ingsw.client.MessageHandler;
import it.polimi.ingsw.utils.constants.Messages;
import it.polimi.ingsw.messages.login.ClientLoginMessage;

public class ExecuteLoadGameObserver implements LoadGameObserver {

    private final MessageHandler mh;

    public ExecuteLoadGameObserver(MessageHandler mh) {
        this.mh = mh;
        mh.attachLoadGameObserver(this);
    }

    @Override
    public void loadGame() {
        ClientLoginMessage msg = new ClientLoginMessage();
        msg.setAction(Messages.ACTION_LOAD_GAME);
        mh.getNetworkClient().sendMessageToServer(msg.toJson());
    }
}
