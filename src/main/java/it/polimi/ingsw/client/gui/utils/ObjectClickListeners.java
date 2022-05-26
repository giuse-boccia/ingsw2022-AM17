package it.polimi.ingsw.client.gui.utils;


import it.polimi.ingsw.client.gui.GuiView;
import it.polimi.ingsw.model.game_objects.Color;
import javafx.scene.Node;

public class ObjectClickListeners {

    private static Color studentClicked;

    public static void setAssistantClicked(int value, Node element) {
        if (element.getStyleClass().contains("highlight_element")) {
            GuiView.getGui().getCurrentObserver().sendActionParameters("PLAY_ASSISTANT", null, null,
                    null, null, value, null, null, null);
            element.getStyleClass().removeAll();
        }
    }

    public static void setStudentClicked(Color color, Node element) {
        if (element.getStyleClass().contains("highlight_element")) {
            studentClicked = color;
        }
        System.out.println("Color clicked: " + studentClicked);
    }

    public static void setDiningRoomClicked() {
        if (studentClicked != null) {
            System.out.println("Received " + studentClicked + " student");
            studentClicked = null;
        }
    }
}
