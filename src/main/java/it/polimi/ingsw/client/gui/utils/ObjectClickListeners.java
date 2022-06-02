package it.polimi.ingsw.client.gui.utils;

import it.polimi.ingsw.client.gui.GuiView;
import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.model.game_objects.Color;
import javafx.scene.Node;

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
            GuiView.getGui().getCurrentObserverHandler().notifyPlayAssistantObservers(value);
        }
    }

    public static void setStudentClicked(Color color, Node element) {
        if (isMoveValid(element)) {
            if (studentClicked != null) {
                setElementHighlighted(studentClicked);
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
            // Character move1FromCardToDining has been played
            if (studentClicked != null) {
                setElementHighlighted(studentClicked);
            }
            setElementHighlighted(studentOnCardClicked);
            GuiView.getGui().getCurrentObserverHandler().notifyPlayCharacterObservers(
                    lastCharacterPlayed, null, null, List.of(studentOnCardClickedColor), null
            );
            studentOnCardClicked = null;
            studentOnCardClickedColor = null;
            resetToCurrentHighlighting();
        } else if (studentClicked != null) {
            setElementHighlighted(studentClicked);
            // A student of the selected color has been moved to the dining room
            GuiView.getGui().getCurrentObserverHandler().notifyMoveStudentObservers(studentClickedColor, null);
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
        GuiView.getGui().getCurrentObserverHandler().notifyCharacterChoiceObservers(name);
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

        GuiView.getGui().getCurrentObserverHandler().notifyPlayCharacterObservers(
                lastCharacterPlayed, null, null, srcStudentColorsForCharacter, dstStudentColorsForCharacter
        );
        resetToCurrentHighlighting();

        srcStudentsForCharacter.clear();
        srcStudentColorsForCharacter.clear();
        dstStudentsForCharacter.clear();
        dstStudentColorsForCharacter.clear();
    }

    public static void setSwapCharacterPlayed(int studentsToMove) {
        lastCharacterPlayedNode.getStyleClass().clear();
        lastCharacterPlayedNode.getStyleClass().add("element_active_for_swap_character");

        DrawingComponents.setBlueBordersToEntranceStudents();
        if (lastCharacterPlayed == CharacterName.swapUpTo3FromEntranceToCard) {
            DrawingComponents.setBlueBordersToCharacterStudents(lastCharacterPlayed);
        } else {
            DrawingComponents.setBlueBordersToDiningStudents();
        }

        studentsToSwapForSwapCharacters = studentsToMove;
    }

    public static void setCloudClicked(Node element, int cloudIndex) {
        if (isMoveValid(element)) {
            if (cloudClicked != null) {
                setElementHighlighted(cloudClicked);
            }
            cloudClicked = element;
            GuiView.getGui().getCurrentObserverHandler().notifyChooseCloudObservers(cloudIndex);
        }

    }

    public static void setIslandClicked(Node element, int numSteps, int islandIndex) {
        if (hasMovingCharacterBeenPlayed(element) && studentOnCardClicked != null && lastCharacterPlayed == CharacterName.move1FromCardToIsland) {
            GuiView.getGui().getCurrentObserverHandler().notifyPlayCharacterObservers(
                    lastCharacterPlayed, null, islandIndex, List.of(studentOnCardClickedColor), null
            );
            setElementHighlighted(studentOnCardClicked);
            studentOnCardClicked = null;
            studentOnCardClickedColor = null;
            resetToCurrentHighlighting();
        } else if (hasIslandCharacterBeenPlayed(element)) {
            GuiView.getGui().getCurrentObserverHandler().notifyPlayCharacterObservers(
                    lastCharacterPlayed, null, islandIndex, null, null
            );
            setElementHighlighted(studentOnCardClicked);
            studentOnCardClicked = null;
            studentOnCardClickedColor = null;
            resetToCurrentHighlighting();
        } else if (isMoveValid(element)) {
            if (studentClicked != null && studentClickedColor != null) {
                setElementHighlighted(studentClicked);
                GuiView.getGui().getCurrentObserverHandler().notifyMoveStudentObservers(studentClickedColor, islandIndex);

                studentClicked = null;
                studentClickedColor = null;
            } else {
                element.getStyleClass().add("highlight_element");
                GuiView.getGui().getCurrentObserverHandler().notifyMoveMNObservers(numSteps);
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

    private static void resetToCurrentHighlighting() {
        DrawingComponents.removeGoldenBordersFromAllElements();
        DrawingComponents.setCurrentActions(DrawingComponents.getLastActions());
    }
}
