package it.polimi.ingsw.client.gui.utils.drawing;

import it.polimi.ingsw.client.gui.utils.DrawingComponents;
import it.polimi.ingsw.client.gui.utils.DrawingConstants;
import it.polimi.ingsw.client.gui.utils.ObjectClickListeners;
import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.model.game_objects.Student;
import it.polimi.ingsw.server.game_state.PlayerState;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.List;

public class DashboardsDrawer {

    /**
     * Draws a {@code DashBoard} with all the students in the {@code Entrance} and {@code DiningRoom}, the professors and the towers
     *
     * @param player   the player to draw the {@code DashBoard} of
     * @param y        the starting Y coordinate of the {@code DashBoard}
     * @param root     the {@code AnchorPane} to attach the {@code DashBoard} to
     * @param username the username of the player who owns the {@code DashBoard}
     */
    public static void drawDashboard(PlayerState player, double y, double dashboardHeight, AnchorPane root, String username) {
        ImageView dashboardImage = new ImageView(new Image("/gameboard/Plancia_DEF_circles.png"));
        dashboardImage.setPreserveRatio(true);
        dashboardImage.setFitHeight(dashboardHeight);
        double dashboardWidth = dashboardImage.getBoundsInParent().getWidth();
        BorderPane dashboard = new BorderPane(dashboardImage);
        dashboard.setLayoutX(0);
        dashboard.setLayoutY(y);
        root.getChildren().add(dashboard);

        // Add students to entrance
        GridPane newEntrance = UtilsDrawer.getGridPane(
                dashboardWidth * DrawingConstants.INITIAL_X_OFFSET_ENTRANCE, dashboardWidth * DrawingConstants.INITIAL_Y_OFFSET_ENTRANCE,
                dashboardWidth * DrawingConstants.ENTRANCE_HGAP, dashboardWidth * DrawingConstants.ENTRANCE_VGAP
        );
        for (int i = 0; i < player.getEntrance().size(); i++) {
            Student s = player.getEntrance().get(i);
            String resourceName = "/gameboard/students/student_" + s.getColor().toString().toLowerCase() + ".png";
            ImageView student = UtilsDrawer.getImageView(resourceName, dashboardWidth / DrawingConstants.STUDENT_DIMENSION_DIVISOR);
            student.setOnMouseClicked(mouseEvent -> ObjectClickListeners.setStudentClicked(s.getColor(), student));

            BorderPane studentPane = new BorderPane(student);
            if (username.equals(player.getName())) {
                studentPane.setOnMouseClicked(event -> ObjectClickListeners.setStudentClicked(s.getColor(), studentPane));
                DrawingComponents.addEntranceStudent(studentPane);
            }

            newEntrance.add(studentPane, (i + 1) % 2, i / 2 + i % 2);
        }
        // Students in entrance are attached to the gameboard and not to the root directly
        dashboard.getChildren().add(newEntrance);

        // Add students to dining room
        GridPane newDiningRoom = UtilsDrawer.getGridPane(dashboardWidth * DrawingConstants.INITIAL_X_OFFSET_DINING_ROOM,
                dashboardWidth * DrawingConstants.INITIAL_Y_OFFSET_DINING_ROOM, dashboardWidth * DrawingConstants.DINING_ROOM_HGAP,
                dashboardWidth * DrawingConstants.DINING_ROOM_VGAP);

        List<Color> colorsInOrder = List.of(Color.GREEN, Color.RED, Color.YELLOW, Color.PINK, Color.BLUE);
        HashMap<Color, Integer> diningStudents = new HashMap<>();
        for (int i = 0; i < player.getDining().size(); i++) {
            Student s = player.getDining().get(i);
            String resourceName = "/gameboard/students/student_" + s.getColor().toString().toLowerCase() + ".png";
            ImageView student = UtilsDrawer.getImageView(resourceName, dashboardWidth / DrawingConstants.STUDENT_DIMENSION_DIVISOR);

            int row = colorsInOrder.indexOf(s.getColor());
            Integer column = diningStudents.get(s.getColor());
            if (column == null) {
                column = 0;
            }

            BorderPane studentWithBorder = new BorderPane(student);
            studentWithBorder.setOnMouseClicked(event -> ObjectClickListeners.setStudentOnDiningClicked(s.getColor(), studentWithBorder));
            diningStudents.put(s.getColor(), column + 1);
            newDiningRoom.add(studentWithBorder, column, row);

            if (username.equals(player.getName())) {
                DrawingComponents.addDiningStudent(studentWithBorder);
            }
        }

        for (Color color : colorsInOrder) {
            diningStudents.putIfAbsent(color, 0);
            int positionOfEmptySpace = diningStudents.get(color);
            if (positionOfEmptySpace == 10) break;

            // If you want to change to a circle, the radius is width / 25 * 0,55
            BorderPane emptySpace = new BorderPane();
            emptySpace.setOnMouseClicked(mouseEvent -> ObjectClickListeners.setDiningRoomClicked());
            emptySpace.setMaxSize(dashboardWidth / DrawingConstants.STUDENT_DIMENSION_DIVISOR, dashboardWidth / DrawingConstants.STUDENT_DIMENSION_DIVISOR);
            emptySpace.setMinSize(dashboardWidth / DrawingConstants.STUDENT_DIMENSION_DIVISOR, dashboardWidth / DrawingConstants.STUDENT_DIMENSION_DIVISOR);

            newDiningRoom.add(emptySpace, positionOfEmptySpace, colorsInOrder.indexOf(color));

            if (username.equals(player.getName())) {
                DrawingComponents.addDiningGap(emptySpace);
            }
        }
        // Students in dining room are attached to the dashboard and not to the root directly
        dashboard.getChildren().add(newDiningRoom);

        // Add professors
        GridPane professorRoom = UtilsDrawer.getGridPane(
                dashboardWidth * DrawingConstants.INITIAL_X_OFFSET_PROFESSOR_ROOM, dashboardWidth * DrawingConstants.INITIAL_Y_OFFSET_PROFESSOR_ROOM,
                0, dashboardWidth * DrawingConstants.PROFESSOR_ROOM_VGAP
        );
        for (int i = 0; i < colorsInOrder.size(); i++) {
            Color color = colorsInOrder.get(i);
            if (player.getOwnedProfessors().contains(color)) {
                String path = "/gameboard/professors/teacher_" + color.toString().toLowerCase() + ".png";
                ImageView professor = UtilsDrawer.getImageView(path, dashboardWidth / DrawingConstants.PROFESSOR_DIMENSION_DIVISOR);
                professor.setRotate(DrawingConstants.PROFESSOR_ROTATION);

                professorRoom.add(professor, 0, i);
            } else {
                Pane emptyPlace = new Pane();
                emptyPlace.setMaxSize(dashboardWidth / DrawingConstants.PROFESSOR_DIMENSION_DIVISOR, dashboardWidth / DrawingConstants.PROFESSOR_DIMENSION_DIVISOR);
                emptyPlace.setMinSize(dashboardWidth / DrawingConstants.PROFESSOR_DIMENSION_DIVISOR, dashboardWidth / DrawingConstants.PROFESSOR_DIMENSION_DIVISOR);
                professorRoom.add(emptyPlace, 0, i);
            }

        }
        // Professors are attached to the gameboard and not to the root directly
        dashboard.getChildren().add(professorRoom);

        // Add towers
        GridPane towers = UtilsDrawer.getGridPane(
                dashboardWidth * DrawingConstants.INITIAL_X_OFFSET_TOWERS, dashboardWidth * DrawingConstants.INITIAL_Y_OFFSET_TOWERS,
                dashboardWidth * DrawingConstants.TOWERS_HGAP, dashboardWidth * DrawingConstants.TOWERS_VGAP
        );
        for (int i = 0; i < player.getRemainingTowers(); i++) {
            String path = "/gameboard/towers/" + player.getTowerColor().toString().toLowerCase() + "_tower.png";
            ImageView tower = UtilsDrawer.getImageView(path, dashboardWidth * DrawingConstants.TOWERS_SIZE);

            towers.add(tower, i % 2, i / 2);
        }
        // Towers are attached to the gameboard and not to the root directly
        dashboard.getChildren().add(towers);

    }

    /**
     * Draws the text under/over the {@code DashBoard} with the username of the given {@code Player} and, if the game is in ExpertMode, the number of coins they own
     *
     * @param player     the player to insert in the text
     * @param x          the starting X coordinate of the text
     * @param y          the starting Y coordinate of the text
     * @param pageWidth  the width of the screen
     * @param pageHeight the height of the screen
     * @param root       the {@code AnchorPane} to attach te text to
     * @param isExpert   true if the the {@code Game} is in Expert Mode
     */
    public static void drawDashboardText(PlayerState player, double x, double y, double pageWidth, double pageHeight, AnchorPane root, boolean isExpert) {
        double startingX = x + pageWidth * DrawingConstants.PLAYER_NAME_INITIAL_PADDING;
        Text text;

        if (!isExpert) {
            if (x == 0 - pageWidth * DrawingConstants.XOFFSET_DASH_TEXT) {
                text = new Text("↑ | " + player.getName());
            } else {
                text = new Text("↓ | " + player.getName());
            }
            text.setX(startingX);
            text.setY(y);
            text.setFont(Font.font(DrawingConstants.FONT_NAME, FontWeight.BOLD, DrawingConstants.TITLE_FONT_SIZE));
            root.getChildren().add(text);
        } else {
            if (x == 0 - pageWidth * DrawingConstants.XOFFSET_DASH_TEXT) {
                text = new Text("↑ | " + player.getName() + " | " + player.getNumCoins() + "x");
            } else {
                text = new Text("↓ | " + player.getName() + " | " + player.getNumCoins() + "x");
            }
            text.setX(startingX);
            text.setY(y);
            text.setFont(Font.font(DrawingConstants.FONT_NAME, FontWeight.BOLD, DrawingConstants.TITLE_FONT_SIZE));
            root.getChildren().add(text);
            ImageView coin = UtilsDrawer.getCoinImageView(startingX + text.getLayoutBounds().getWidth(),
                    y - pageHeight / DrawingConstants.COIN_DIMENSION_IN_TEXT_DIVISOR, pageWidth * DrawingConstants.COIN_PROPORTION);
            root.getChildren().add(coin);
        }
    }

}
