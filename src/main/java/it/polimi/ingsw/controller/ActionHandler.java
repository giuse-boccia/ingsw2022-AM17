package it.polimi.ingsw.controller;

import it.polimi.ingsw.client.NetworkClient;
import it.polimi.ingsw.messages.action.Action;
import it.polimi.ingsw.messages.action.ActionArgs;
import it.polimi.ingsw.messages.action.ClientActionMessage;
import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.model.game_objects.Color;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ActionHandler {

    public static void handlePlayAssistant(NetworkClient nc) throws IOException {
        int value = nc.getClient().getAssistantValue();
        ActionArgs args = new ActionArgs();
        args.setValue(value);
        Action action = new Action("PLAY_ASSISTANT", args);
        sendMessageToServer(nc, action);
    }

    public static void handleMoveStudentToDining(NetworkClient nc) throws IOException {
        Color chosenColor = nc.getClient().askStudentColor();
        ActionArgs args = new ActionArgs();
        args.setColor(chosenColor);
        sendMessageToServer(nc, new Action("MOVE_STUDENT_TO_DINING", args));
    }

    public static void handleMoveStudentToIsland(NetworkClient nc) throws IOException {
        Color chosenColor = nc.getClient().askStudentColor();
        int islandIndex = nc.getClient().askIslandIndex() - 1;
        ActionArgs args = new ActionArgs();
        args.setIsland(islandIndex);
        args.setColor(chosenColor);
        sendMessageToServer(nc, new Action("MOVE_STUDENT_TO_ISLAND", args));
    }

    public static void handleMoveMotherNature(NetworkClient nc) throws IOException {
        int numSteps = nc.getClient().askNumStepsOfMotherNature();
        ActionArgs args = new ActionArgs();
        args.setNum_steps(numSteps);
        sendMessageToServer(nc, new Action("MOVE_MN", args));
    }

    public static void handleFillFromCloud(NetworkClient nc) throws IOException {
        int cloudNumber = nc.getClient().askCloudIndex() - 1;
        ActionArgs args = new ActionArgs();
        args.setCloud(cloudNumber);
        sendMessageToServer(nc, new Action("FILL_FROM_CLOUD", args));
    }

    public static void handlePlayCharacter(NetworkClient nc) throws IOException {
        // FIXME change this line
        nc.getClient().setCharacters(new String[]{"move1FromCardToIsland", "noEntry", "swapUpTo2FromEntranceToDiningRoom"});
        nc.getClient().showAllCharactersWithIndex();
        int characterIndex = nc.getClient().askCharacterIndex() - 1;
        handleCharacterPlayed(nc.getClient().getCharacters()[characterIndex], nc);
    }

    private static void handleCharacterPlayed(String characterName, NetworkClient nc) throws IOException {
        CharacterName name = CharacterName.valueOf(characterName);
        ActionArgs args = new ActionArgs();
        args.setCharacterName(name);
        switch (name) {
            case move1FromCardToIsland -> {
                Color color = nc.getClient().askStudentColor();
                int islandIndex = nc.getClient().askIslandIndex() - 1;
                args.setSourceStudents(new ArrayList<>(List.of(color)));
                args.setIsland(islandIndex);
            }
            case resolveIsland, noEntry -> {
                int islandIndex = nc.getClient().askIslandIndex() - 1;
                args.setIsland(islandIndex);
            }
            case swapUpTo3FromEntranceToCard -> {
                ArrayList<Color> allColors = nc.getClient().askColorListForSwapCharacters(3, "this card");
                args.setSourceStudents(allColors.subList(0, (allColors.size() / 2)));
                args.setDstStudents(allColors.subList(allColors.size() / 2, allColors.size()));
            }
            case swapUpTo2FromEntranceToDiningRoom -> {
                ArrayList<Color> allColors = nc.getClient().askColorListForSwapCharacters(2, "your dining room");
                args.setSourceStudents(allColors.subList(0, (allColors.size() / 2)));
                args.setDstStudents(allColors.subList(allColors.size() / 2, allColors.size()));
            }
            case move1FromCardToDining -> {
                Color color = nc.getClient().askStudentColor();
                args.setSourceStudents(new ArrayList<>(List.of(color)));
            }
            case ignoreColor, everyOneMove3FromDiningRoomToBag -> {
                Color color = nc.getClient().askStudentColor();
                args.setColor(color);
            }
        }
        sendMessageToServer(nc, new Action("PLAY_CHARACTER", args));
    }

    private static void sendMessageToServer(NetworkClient nc, Action action) {
        ClientActionMessage toSend = new ClientActionMessage();
        toSend.setAction(action);
        toSend.setPlayer(nc.getUsername());
        nc.sendMessageToServer(toSend.toJson());
    }
}
