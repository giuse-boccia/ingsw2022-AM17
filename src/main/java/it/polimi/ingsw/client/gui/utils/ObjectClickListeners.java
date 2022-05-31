package it.polimi.ingsw.client.gui.utils;

import it.polimi.ingsw.client.gui.GuiView;
import it.polimi.ingsw.constants.Messages;
import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.model.game_objects.Color;
import javafx.scene.Node;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ObjectClickListeners {

    private static Color studentClickedColor;
    private static Node studentClicked;
    private static Color studentOnCardClickedColor;
    private static Node studentOnCardClicked;
    private static CharacterName lastCharacterPlayed;
    private static Node islandClicked;
    private static int islandIndex;
    private static Node cloudClicked;
    private static final List<Color> srcStudentColorsForCharacter = new ArrayList<>();
    private static final List<Color> dstStudentColorsForCharacter = new ArrayList<>();
    private static final List<Node> srcStudentsForCharacter = new ArrayList<>();
    private static final List<Node> dstStudentsForCharacter = new ArrayList<>();
    private static Node lastCharacterPlayedNode;
    private static Integer studentsToSwapForSwapCharacters;

    public static void setAssistantClicked(int value, Node element) {
        if (isMoveValid(element)) {
            GuiView.getGui().getCurrentObserver().sendActionParameters("PLAY_ASSISTANT", null, null,
                    null, null, value, null, null, null);
        }
    }

    public static void setStudentClicked(Color color, Node element) {
        if (isMoveValid(element)) {
            if (studentClicked != null) {
                studentClicked.getStyleClass().clear();
                studentClicked.getStyleClass().add("highlight_element");
            }
            studentClicked = element;
            studentClicked.getStyleClass().add("selected_element");
            studentClickedColor = color;
            studentOnCardClicked = null;
            studentOnCardClickedColor = null;
        } else if (hasSwapCharacterBeenPlayed(element) && srcStudentColorsForCharacter.size() < studentsToSwapForSwapCharacters) {
            element.getStyleClass().add("element_selected_for_swap_character");
            srcStudentColorsForCharacter.add(color);
            srcStudentsForCharacter.add(element);
            sendSwapCharacterPlayedMessage();
        }
    }

    public static void setDiningRoomClicked() {
        if (studentOnCardClicked != null && lastCharacterPlayed == CharacterName.move1FromCardToDining) {
            if (studentClicked != null) {
                setElementHighlighted(studentClicked);
            }
            studentOnCardClicked.getStyleClass().clear();
            studentOnCardClicked.getStyleClass().add("element_active_for_moving_character");
            GuiView.getGui().getCurrentObserver().sendActionParameters("PLAY_CHARACTER", null, null,
                    null, null, null, lastCharacterPlayed, List.of(studentOnCardClickedColor), null);
            studentOnCardClicked = null;
            studentOnCardClickedColor = null;
        } else if (studentClicked != null) {
            setElementHighlighted(studentClicked);
            // A student of the selected color has been moved to the dining room
            GuiView.getGui().getCurrentObserver().sendActionParameters("MOVE_STUDENT_TO_DINING", studentClickedColor, null,
                    null, null, null, null, null, null);
            studentClicked = null;
            studentClickedColor = null;
        }
    }

    public static void setStudentOnDiningClicked(Color color, Node element) {
        if (!hasSwapCharacterBeenPlayed(element)) return;
        if (dstStudentColorsForCharacter.size() >= studentsToSwapForSwapCharacters) return;
        element.getStyleClass().add("element_selected_for_swap_character");
        dstStudentsForCharacter.add(element);
        dstStudentColorsForCharacter.add(color);
        sendSwapCharacterPlayedMessage();
    }

    public static void setCharacterClicked(CharacterName name, Node element) {
        if (!isMoveValid(element)) return;
        DrawingComponents.removeGoldenBordersFromAllCharacters();
        lastCharacterPlayed = name;
        lastCharacterPlayedNode = element;
        try {
            GuiView.getGui().getCurrentObserver().sendCharacterName(name);
        } catch (IOException e) {
            GuiView.getGui().gracefulTermination(Messages.SERVER_CRASHED);
        }
    }

    public static void setStudentsOnCardClicked(Color color, Node element) {
        if (hasMovingCharacterBeenPlayed(element)) {
            if (studentOnCardClicked != null) {
                studentOnCardClicked.getStyleClass().clear();
                studentOnCardClicked.getStyleClass().add("element_active_for_moving_character");
            }
            studentOnCardClickedColor = color;
            studentOnCardClicked = element;
            studentOnCardClicked.getStyleClass().add("element_selected_for_moving_character");
            studentClicked = null;
            studentClickedColor = null;
        } else if (hasSwapCharacterBeenPlayed(element)) {
            if (dstStudentsForCharacter.size() < studentsToSwapForSwapCharacters) {
                element.getStyleClass().add("element_selected_for_swap_character");
                dstStudentColorsForCharacter.add(color);
                dstStudentsForCharacter.add(element);
                sendSwapCharacterPlayedMessage();
            }
        }
    }

    private static void sendSwapCharacterPlayedMessage() {
        if (srcStudentsForCharacter.size() != studentsToSwapForSwapCharacters ||
                dstStudentsForCharacter.size() != studentsToSwapForSwapCharacters) {
            return;
        }
        System.out.println("About to send info: " + Arrays.toString(new List[]{srcStudentColorsForCharacter}) + "and " +
                Arrays.toString(new List[]{dstStudentsForCharacter}));
        srcStudentsForCharacter.forEach(element -> element.getStyleClass().clear());
        dstStudentsForCharacter.forEach(element -> element.getStyleClass().clear());

        GuiView.getGui().getCurrentObserver().sendActionParameters("PLAY_CHARACTER", null, null, null,
                null, null, lastCharacterPlayed, srcStudentColorsForCharacter, dstStudentColorsForCharacter);

        srcStudentsForCharacter.clear();
        srcStudentColorsForCharacter.clear();
        dstStudentsForCharacter.clear();
        dstStudentColorsForCharacter.clear();
    }

    public static void setSwapCharacterPlayed(int studentsToMove) {
        lastCharacterPlayedNode.getStyleClass().clear();
        lastCharacterPlayedNode.getStyleClass().add("element_active_for_swap_character");

        DrawingComponents.addBlueBordersToEntranceStudents();
        if (lastCharacterPlayed == CharacterName.swapUpTo3FromEntranceToCard) {
            DrawingComponents.addBlueBordersToCharacterStudents(lastCharacterPlayed);
        } else {
            DrawingComponents.addBlueBordersToDiningStudents();
        }

        studentsToSwapForSwapCharacters = studentsToMove;
    }

    public static void setCloudClicked(Node element, int cloudIndex) {
        if (isMoveValid(element)) {
            if (cloudClicked != null) {
                cloudClicked.getStyleClass().clear();
                cloudClicked.getStyleClass().add("highlight_element");
            }
            cloudClicked = element;
            GuiView.getGui().getCurrentObserver().sendActionParameters(
                    Messages.ACTION_FILL_FROM_CLOUD,
                    null,
                    null,
                    null,
                    cloudIndex,
                    null,
                    null,
                    null,
                    null
            );
        }

    }

    public static void setIslandClicked(Node element, int numSteps, int islandIndex) {
        if (hasMovingCharacterBeenPlayed(element) && studentOnCardClicked != null && lastCharacterPlayed == CharacterName.move1FromCardToIsland) {
            GuiView.getGui().getCurrentObserver().sendActionParameters("PLAY_CHARACTER", null, islandIndex,
                    null, null, null, lastCharacterPlayed, List.of(studentOnCardClickedColor), null);
            studentOnCardClicked = null;
            studentOnCardClickedColor = null;
        } else if (hasIslandCharacterBeenPlayed(element)) {
            GuiView.getGui().getCurrentObserver().sendActionParameters("PLAY_CHARACTER", null, islandIndex,
                    null, null, null, lastCharacterPlayed, null, null);
        } else if (isMoveValid(element)) {
            if (studentClicked != null && studentClickedColor != null) {
                setElementHighlighted(studentClicked);
                GuiView.getGui().getCurrentObserver().sendActionParameters("MOVE_STUDENT_TO_ISLAND", studentClickedColor,
                        islandIndex, null, null, null, null, null, null);
                studentClicked = null;
                studentClickedColor = null;
            } else {
                element.getStyleClass().add("highlight_element");
                GuiView.getGui().getCurrentObserver().sendActionParameters(Messages.ACTION_MOVE_MN, null, null,
                        numSteps, null, null, null, null, null);
            }
        }
    }

    private static void setElementHighlighted(Node element) {
        element.getStyleClass().clear();
        element.getStyleClass().add("highlight_element");
    }

    private static boolean isMoveValid(Node element) {
        if (element.getStyleClass().contains("highlight_element")) {
            element.getStyleClass().clear();
            return true;
        }
        return false;
    }

    private static boolean hasSwapCharacterBeenPlayed(Node element) {
        if (element.getStyleClass().contains("element_active_for_swap_character")) {
            element.getStyleClass().clear();
            return true;
        }
        return false;
    }

    private static boolean hasMovingCharacterBeenPlayed(Node element) {
        if (element.getStyleClass().contains("element_active_for_moving_character")) {
            element.getStyleClass().clear();
            return true;
        }
        return false;
    }

    private static boolean hasIslandCharacterBeenPlayed(Node element) {
        return element.getStyleClass().contains("element_active_for_island_character");
    }

    public static CharacterName getLastCharacterPlayed() {
        return lastCharacterPlayed;
    }

    public static void setStudentOnIslandClicked(Node element, Color color, int islandIndex) {
        setStudentsOnCardClicked(color, element);
        ObjectClickListeners.islandIndex = islandIndex;
    }
}
