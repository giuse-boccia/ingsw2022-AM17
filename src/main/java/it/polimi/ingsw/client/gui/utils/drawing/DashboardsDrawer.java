package it.polimi.ingsw.client.gui.utils.drawing;

import it.polimi.ingsw.client.gui.utils.DrawingComponents;
import it.polimi.ingsw.client.gui.utils.DrawingConstants;
import it.polimi.ingsw.client.gui.utils.ObjectClickListeners;
import it.polimi.ingsw.model.Player;
import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.model.game_objects.Student;
import it.polimi.ingsw.server.game_state.PlayerState;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.List;

public class DashboardsDrawer {

    private static final List<Color> colorsInOrder = List.of(Color.GREEN, Color.RED, Color.YELLOW, Color.PINK, Color.BLUE);

    /**
     * Draws a {@code DashBoard} with all the students in the {@code Entrance} and {@code DiningRoom}, the professors and the towers
     *
     * @param player     the player to draw the {@code Dashboard} of
     * @param pageHeight the height of the screen
     * @param vBox       the {@code VBox} to attach the {@code Dashboard} to
     * @param username   the username of the player who owns the {@code Dashboard}
     */
    public static BorderPane drawDashboard(PlayerState player, double pageHeight, VBox vBox, String username) {
        ImageView dashboardImage = new ImageView(new Image("/gameboard/Plancia_DEF.png"));
        double otherDashboardHeight = pageHeight * DrawingConstants.OTHER_DASHBOARD_HEIGHT;
        dashboardImage.setPreserveRatio(true);
        dashboardImage.setFitHeight(otherDashboardHeight);
        double dashboardWidth = dashboardImage.getBoundsInParent().getWidth();
        BorderPane dashboard = new BorderPane(dashboardImage);
        vBox.setLayoutX(0);
        vBox.setLayoutY(0);
        vBox.getChildren().add(dashboard);
        addElementsToDashboard(player, dashboardWidth, dashboard, username);

        return dashboard;

    }

    /**
     * Draws the text under/over the {@code DashBoard} with the username of the given {@code Player} and, if the game is in ExpertMode, the number of coins they own
     *
     * @param player     the player to insert in the text
     * @param pageWidth  the width of the screen
     * @param pageHeight the height of the screen
     * @param isExpert   true if the {@code Game} is in Expert Mode
     */
    public static AnchorPane drawDashboardText(PlayerState player, double pageWidth, double pageHeight, boolean isExpert) {
        double dashboardHeight = pageHeight * DrawingConstants.OTHER_DASHBOARD_HEIGHT;
        double dashboardWidth = dashboardHeight / DrawingConstants.DASHBOARD_HEIGHT_OVER_WIDTH;
        double startingX = dashboardWidth * DrawingConstants.PLAYER_NAME_INITIAL_PADDING;
        AnchorPane playerInfo = new AnchorPane();
        Text text;

        if (!isExpert) {
            text = new Text("↑ " + player.getName());
            text.setX(startingX);
            text.setY(pageHeight * 0.02);
            text.setFont(Font.font(DrawingConstants.FONT_NAME, FontWeight.BOLD, DrawingConstants.PARAGRAPH_FONT_SIZE));
            playerInfo.getChildren().add(text);
        } else {
            text = new Text("↑ " + player.getName() + " | " + player.getNumCoins() + "x");
            text.setX(startingX);
            text.setY(pageHeight * 0.02);
            text.setFont(Font.font(DrawingConstants.FONT_NAME, FontWeight.BOLD, DrawingConstants.PARAGRAPH_FONT_SIZE));
            playerInfo.getChildren().add(text);
            ImageView coin = UtilsDrawer.getCoinImageView(startingX + text.getLayoutBounds().getWidth(),
                    0, pageWidth * DrawingConstants.COIN_PROPORTION_SMALL);
            playerInfo.getChildren().add(coin);
        }
        return playerInfo;
    }

    /**
     * Draws the dashboard of the player seeing the screen
     *
     * @param player     the player who sees the screen
     * @param pageHeight the height of the screen
     * @param root       the {@code AnchorPane} to attach the different components to
     * @param username   the username of the {@code Player} ehose GUI will be drawn the given {@code GameState} on
     */
    public static void drawMegaDashboard(PlayerState player, double pageHeight, AnchorPane root, String username) {
        ImageView dashboardImage = new ImageView(new Image("/gameboard/Plancia_DEF.png"));
        double megaDashboardHeight = pageHeight * DrawingConstants.MEGA_DASHBOARD_HEIGHT;
        dashboardImage.setPreserveRatio(true);
        dashboardImage.setFitHeight(megaDashboardHeight);
        double dashboardWidth = dashboardImage.getBoundsInParent().getWidth();
        BorderPane dashboard = new BorderPane(dashboardImage);
        dashboard.setLayoutX(0);
        dashboard.setLayoutY(pageHeight - megaDashboardHeight - pageHeight * 0.03);
        root.getChildren().add(dashboard);
        addElementsToDashboard(player, dashboardWidth, dashboard, username);
    }

    /**
     * Draws the text under/over the {@code DashBoard} with the username of the given {@code Player} and, if the game is in ExpertMode, the number of coins they own
     *
     * @param player     the player to insert in the text
     * @param pageWidth  the width of the screen
     * @param pageHeight the height of the screen
     * @param root       the {@code AnchorPane} to attach te text to
     * @param isExpert   true if the the {@code Game} is in Expert Mode
     */
    public static void drawMegaDashboardText(PlayerState player, double pageWidth, double pageHeight, AnchorPane root, boolean isExpert) {
        Text text;
        AnchorPane playerInfo = new AnchorPane();
        double megaDashboardHeight = pageHeight * DrawingConstants.MEGA_DASHBOARD_HEIGHT;
        double megaDashboardWidth = megaDashboardHeight / DrawingConstants.DASHBOARD_HEIGHT_OVER_WIDTH;
        playerInfo.setLayoutX(megaDashboardWidth * 0.4);
        playerInfo.setLayoutY(pageHeight - megaDashboardHeight - pageHeight * 0.05);
        if (!isExpert) {
            text = new Text("↓ " + player.getName());
            text.setX(0);
            text.setY(0);
            text.setFont(Font.font(DrawingConstants.FONT_NAME, FontWeight.BOLD, DrawingConstants.TITLE_FONT_SIZE));
            playerInfo.getChildren().add(text);
        } else {
            text = new Text("↓ " + player.getName() + " | " + player.getNumCoins() + "x");
            text.setX(0);
            text.setY(0);
            text.setFont(Font.font(DrawingConstants.FONT_NAME, FontWeight.BOLD, DrawingConstants.TITLE_FONT_SIZE));
            playerInfo.getChildren().add(text);
            ImageView coin = UtilsDrawer.getCoinImageView(text.getLayoutBounds().getWidth(),
                    -pageHeight / DrawingConstants.COIN_DIMENSION_IN_TEXT_DIVISOR, pageWidth * DrawingConstants.COIN_PROPORTION);
            playerInfo.getChildren().add(coin);
        }
        root.getChildren().add(playerInfo);
    }

    /**
     * Adds the different elements to the given dashboard
     *
     * @param player         the {@code Player} who owns the {@code Dashboard}
     * @param dashboardWidth the width of the {@code Dashboard}
     * @param dashboard      the {@code Dashboard} to be drawn
     * @param username       the username of the player who owns the {@code Dashboard}
     */
    private static void addElementsToDashboard(PlayerState player, double dashboardWidth, BorderPane dashboard, String username) {

        // Add students to entrance
        GridPane newEntrance = UtilsDrawer.getGridPane(
                dashboardWidth * DrawingConstants.INITIAL_X_OFFSET_ENTRANCE, dashboardWidth * DrawingConstants.INITIAL_Y_OFFSET_ENTRANCE,
                dashboardWidth * DrawingConstants.ENTRANCE_HGAP, dashboardWidth * DrawingConstants.ENTRANCE_VGAP
        );
        addStudentsToEntrance(player, username, dashboardWidth, newEntrance);
        // Students in entrance are attached to the gameboard and not to the root directly
        dashboard.getChildren().add(newEntrance);

        // Add students to dining room
        GridPane newDiningRoom = UtilsDrawer.getGridPane(dashboardWidth * DrawingConstants.INITIAL_X_OFFSET_DINING_ROOM,
                dashboardWidth * DrawingConstants.INITIAL_Y_OFFSET_DINING_ROOM, dashboardWidth * DrawingConstants.DINING_ROOM_HGAP,
                dashboardWidth * DrawingConstants.DINING_ROOM_VGAP);
        addStudentsToDiningRoom(player, username, dashboardWidth, newDiningRoom);
        // Students in dining room are attached to the dashboard and not to the root directly
        dashboard.getChildren().add(newDiningRoom);

        // Add professors
        GridPane professorRoom = UtilsDrawer.getGridPane(
                dashboardWidth * DrawingConstants.INITIAL_X_OFFSET_PROFESSOR_ROOM, dashboardWidth * DrawingConstants.INITIAL_Y_OFFSET_PROFESSOR_ROOM,
                0, dashboardWidth * DrawingConstants.PROFESSOR_ROOM_VGAP
        );
        addProfessorsToProfessorRoom(player, dashboardWidth, professorRoom);
        // Professors are attached to the gameboard and not to the root directly
        dashboard.getChildren().add(professorRoom);

        // Add towers
        GridPane towers = UtilsDrawer.getGridPane(
                dashboardWidth * DrawingConstants.INITIAL_X_OFFSET_TOWERS, dashboardWidth * DrawingConstants.INITIAL_Y_OFFSET_TOWERS,
                dashboardWidth * DrawingConstants.TOWERS_HGAP, dashboardWidth * DrawingConstants.TOWERS_VGAP
        );
        addAllTowersToTowerSpace(player, dashboardWidth, towers);
        // Towers are attached to the gameboard and not to the root directly
        dashboard.getChildren().add(towers);
    }

    /**
     * Adds all the correct students to the provided {@code GridPane}, representing the entrance of the player
     *
     * @param player         the player to insert in the text
     * @param username       the username of the player who owns the {@code Dashboard}
     * @param dashboardWidth the width of the drawn dashboard
     * @param entrancePane   {@code GridPane} representing the entrance of the player
     */
    private static void addStudentsToEntrance(PlayerState player, String username, double dashboardWidth, GridPane entrancePane) {
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

            entrancePane.add(studentPane, (i + 1) % 2, i / 2 + i % 2);
        }
    }

    /**
     * Adds all the correct students to the provided {@code GridPane}, representing the dining room of the player
     *
     * @param player         the player to insert in the text
     * @param username       the username of the player who owns the {@code Dashboard}
     * @param dashboardWidth the width of the drawn dashboard
     * @param diningRoom     {@code GridPane} representing the dining room of the player
     */
    private static void addStudentsToDiningRoom(PlayerState player, String username, double dashboardWidth, GridPane diningRoom) {
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
            diningRoom.add(studentWithBorder, column, row);

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

            diningRoom.add(emptySpace, positionOfEmptySpace, colorsInOrder.indexOf(color));

            if (username.equals(player.getName())) {
                DrawingComponents.addDiningGap(emptySpace);
            }
        }
    }

    /**
     * Adds all the professors owned by the player to the provided {@code GridPane}, representing his professor room
     *
     * @param player         the player to insert in the text
     * @param dashboardWidth the width of the drawn dashboard
     * @param professorRoom  {@code GridPane} representing the professor room of the player
     */
    private static void addProfessorsToProfessorRoom(PlayerState player, double dashboardWidth, GridPane professorRoom) {
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
    }


    /**
     * Adds all the towers owned by the player to the provided {@code GridPane}, representing his tower space
     *
     * @param player         the player to insert in the text
     * @param dashboardWidth the width of the drawn dashboard
     * @param towers         {@code GridPane} representing the tower space of the player
     */
    private static void addAllTowersToTowerSpace(PlayerState player, double dashboardWidth, GridPane towers) {
        for (int i = 0; i < player.getRemainingTowers(); i++) {
            String path = "/gameboard/towers/" + player.getTowerColor().toString().toLowerCase() + "_tower.png";
            ImageView tower = UtilsDrawer.getImageView(path, dashboardWidth * DrawingConstants.TOWERS_SIZE);

            towers.add(tower, i % 2, i / 2);
        }
    }

}
