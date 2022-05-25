package it.polimi.ingsw.client.gui.utils;


import it.polimi.ingsw.model.game_objects.Color;

import java.util.ArrayList;
import java.util.List;

public class ObjectClickListeners {

    private static final List<Color> studentsClicked = new ArrayList<>();
    private static Color studentClicked;

    public static void setStudentClicked(Color color) {
        studentClicked = color;
        System.out.println("Color clicked: " + studentClicked);
    }

    public static void setDiningRoomClicked() {
        if (studentClicked != null) {
            System.out.println("Received " + studentClicked + " student");
            studentClicked = null;
        }
    }
}
