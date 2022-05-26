package it.polimi.ingsw.client.gui.utils;


import it.polimi.ingsw.client.gui.GuiView;
import it.polimi.ingsw.constants.Messages;
import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.model.game_objects.Color;
import javafx.scene.Node;

import java.io.IOException;

public class ObjectClickListeners {

    private static Color studentClickedColor;
    private static Node studentClicked;

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
            }
            studentClicked = element;
            studentClicked.getStyleClass().add("selected_element");
            studentClickedColor = color;
        }
        System.out.println("Color clicked: " + studentClickedColor);
    }

    public static void setDiningRoomClicked() {
        if (studentClicked != null) {
            studentClicked.getStyleClass().clear();
            // A student of the selected color has been moved to the dining room
            GuiView.getGui().getCurrentObserver().sendActionParameters("MOVE_STUDENT_TO_DINING", studentClickedColor, null,
                    null, null, null, null, null, null);
            studentClicked = null;
            studentClickedColor = null;
        }
    }

    public static void setCharacterClicked(CharacterName name, Node element) {
        if (!isMoveValid(element)) return;
        DrawingComponents.removeGoldenBordersFromAllCharacters();
        System.out.println("Played character " + name);
        try {
            GuiView.getGui().getCurrentObserver().sendCharacterName(name);
        } catch (IOException e) {
            GuiView.getGui().gracefulTermination(Messages.SERVER_CRASHED);
        }
    }

    private static boolean isMoveValid(Node element) {
        if (element.getStyleClass().contains("highlight_element")) {
            element.getStyleClass().clear();
            return true;
        }
        return false;
    }
}
