package it.polimi.ingsw.controller;

import it.polimi.ingsw.client.NetworkClient;
import it.polimi.ingsw.messages.action.Action;
import it.polimi.ingsw.messages.action.ActionArgs;
import it.polimi.ingsw.messages.action.ClientActionMessage;
import it.polimi.ingsw.model.game_objects.Color;

import java.io.IOException;

public class ActionHandler {

    public static void handlePlayAssistant(NetworkClient nc) throws IOException {
        int value = nc.getClient().getAssistantValue();
        ActionArgs args = new ActionArgs();
        args.setValue(value);
        Action action = new Action("PLAY_ASSISTANT", args);
        sendMessageToServer(nc, action);
    }

    public static void handleMoveStudentToDining(NetworkClient nc) throws IOException {
        Color chosenColor = Color.GREEN;
        ActionArgs args = new ActionArgs();
        args.setColor(chosenColor);
        sendMessageToServer(nc, new Action("MOVE_STUDENT_TO_DINING", args));
    }

    public static void handleMoveStudentToIsland(NetworkClient nc) throws IOException {
        int islandIndex = 2;
        ActionArgs args = new ActionArgs();
        args.setIsland(islandIndex);
        sendMessageToServer(nc, new Action("MOVE_STUDENT_TO_ISLAND", args));
    }

    public static void handleMoveMotherNature(NetworkClient nc) throws IOException {
        int numSteps = 1;
        ActionArgs args = new ActionArgs();
        args.setNum_steps(numSteps);
        sendMessageToServer(nc, new Action("MOVE_MN", args));
    }

    public static void handleFillFromCloud(NetworkClient nc) throws IOException {
        int cloudNumber = 1;
        ActionArgs args = new ActionArgs();
        args.setCloud(cloudNumber);
        sendMessageToServer(nc, new Action("FILL_FROM_CLOUD", args));
    }

    public static void handlePlayCharacter(NetworkClient nc) throws IOException {

    }

    private static void sendMessageToServer(NetworkClient nc, Action action) {
        ClientActionMessage toSend = new ClientActionMessage();
        toSend.setAction(action);
        toSend.setPlayer(nc.getUsername());
        nc.sendMessageToServer(toSend.toJson());
    }
}
