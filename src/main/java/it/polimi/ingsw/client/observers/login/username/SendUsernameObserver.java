package it.polimi.ingsw.client.observers.login.username;

import it.polimi.ingsw.client.MessageHandler;
import it.polimi.ingsw.messages.login.ClientLoginMessage;

public class SendUsernameObserver implements UsernameObserver {

    private final MessageHandler mh;

    public SendUsernameObserver(MessageHandler mh) {
        this.mh = mh;
        mh.attachUsernameObserver(this);
    }

    @Override
    public void onUsernameEntered(String username) {
        if (username != null) {
            ClientLoginMessage loginMessage = new ClientLoginMessage();
            loginMessage.setUsername(username);
            loginMessage.setAction("SET_USERNAME");

            mh.getNetworkClient().sendMessageToServer(loginMessage.toJson());
        }
    }

}