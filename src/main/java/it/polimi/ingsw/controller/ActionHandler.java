package it.polimi.ingsw.controller;

import it.polimi.ingsw.client.NetworkClient;
import it.polimi.ingsw.messages.action.Action;
import it.polimi.ingsw.messages.action.ActionArgs;
import it.polimi.ingsw.messages.action.ClientActionMessage;

import java.io.IOException;

public class ActionHandler {

    public static void handlePlayAssistant(NetworkClient nc) throws IOException {
        int value = nc.getClient().getAssistantValue();
        ActionArgs args = new ActionArgs();
        args.setValue(value);
        Action action = new Action("PLAY_ASSISTANT", args);
        ClientActionMessage toSend = new ClientActionMessage();
        toSend.setAction(action);
        nc.sendMessageToServer(toSend.toJson());
    }

}
