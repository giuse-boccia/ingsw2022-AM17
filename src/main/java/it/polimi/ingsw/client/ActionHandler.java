package it.polimi.ingsw.client;

import it.polimi.ingsw.model.characters.CharacterName;

import java.io.IOException;

/**
 * Class which handles the action messages
 */
public class ActionHandler {

    /**
     * Handles the action message with Status "PLAY_ASSISTANT"
     *
     * @param nc the {@code NetworkClient} of the {@code Client} to send the response message from
     */
    public static void handlePlayAssistant(NetworkClient nc) throws IOException {
        nc.getClient().getAssistantValue();
    }

    /**
     * Handles the action message with Status "MOVE_STUDENT_TO_DINING"
     *
     * @param nc the {@code NetworkClient} of the {@code Client} to send the response message from
     */
    public static void handleMoveStudentToDining(NetworkClient nc) throws IOException {
        nc.getClient().askMoveStudentToDining();
    }

    /**
     * Handles the action message with Status "MOVE_STUDENT_TO_ISLAND"
     *
     * @param nc the {@code NetworkClient} of the {@code Client} to send the response message from
     */
    public static void handleMoveStudentToIsland(NetworkClient nc) throws IOException {
        nc.getClient().askMoveStudentToIsland();
    }

    /**
     * Handles the action message with Status "MOVE_MN"
     *
     * @param nc the {@code NetworkClient} of the {@code Client} to send the response message from
     */
    public static void handleMoveMotherNature(NetworkClient nc) throws IOException {
        nc.getClient().askNumStepsOfMotherNature();
    }

    /**
     * Handles the action message with Status "FILL_FROM_CLOUD"
     *
     * @param nc the {@code NetworkClient} of the {@code Client} to send the response message from
     */
    public static void handleFillFromCloud(NetworkClient nc) throws IOException {
        nc.getClient().askCloudIndex();
    }

    /**
     * Handles the action message with Status "PLAY_CHARACTER" making the user choose between one the three in the {@code Game}
     *
     * @param nc the {@code NetworkClient} of the {@code Client} to send the response message from
     */
    public static void handlePlayCharacter(NetworkClient nc) throws IOException {
        nc.getClient().showAllCharactersWithIndex();
        nc.getClient().askCharacterIndex();
    }

    /**
     * Sends the correct {@code Message} to the {@code Server} containing the needed args for each {@code Character}
     *
     * @param name the name of the chosen {@code Character}
     * @param nc   the {@code NetworkClient} of the {@code Client} to send the response message from
     */
    public static void handleCharacterPlayed(CharacterName name, NetworkClient nc) throws IOException {
        switch (name) {
            case move1FromCardToIsland -> nc.getClient().askToMoveOneStudentFromCard(true);
            case move1FromCardToDining -> nc.getClient().askToMoveOneStudentFromCard(false);
            case resolveIsland, noEntry -> nc.getClient().askIslandIndexForCharacter(name);
            case swapUpTo3FromEntranceToCard -> nc.getClient().askColorListForSwapCharacters(3, "this card", name);
            case swapUpTo2FromEntranceToDiningRoom -> nc.getClient().askColorListForSwapCharacters(2, "your dining room", name);
            case ignoreColor, everyOneMove3FromDiningRoomToBag -> nc.getClient().pickColorForPassive(name);
            default -> nc.getClient().playCharacterWithoutArguments(name);
        }
    }
}
