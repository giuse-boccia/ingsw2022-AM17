package it.polimi.ingsw.client.observers.login.username;

import it.polimi.ingsw.client.MessageHandler;
import it.polimi.ingsw.languages.Messages;
import it.polimi.ingsw.utils.constants.Constants;
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
            loginMessage.setAction(Constants.ACTION_SET_USERNAME);
            loginMessage.setLanguageTag(Messages.getCurrentLanguageTag());

            mh.getNetworkClient().sendMessageToServer(loginMessage.toJson());
        }
    }

}