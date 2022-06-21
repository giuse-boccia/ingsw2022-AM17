package it.polimi.ingsw.client.gui.utils.drawing;

import it.polimi.ingsw.client.gui.utils.DrawingComponents;
import it.polimi.ingsw.client.gui.utils.DrawingConstants;
import it.polimi.ingsw.client.gui.utils.ObjectClickListeners;
import it.polimi.ingsw.constants.Constants;
import it.polimi.ingsw.model.game_objects.Student;
import it.polimi.ingsw.server.game_state.GameState;
import it.polimi.ingsw.server.game_state.IslandState;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.List;

public class IslandsDrawer {

    /**
     * Draws the islands accordingly to the given {@code GameState}
     *
     * @param gameState  the {@code GameState} to fetch the information about the islands from
     * @param pageWidth  the width of the screen
     * @param pageHeight the height of the screen
     * @param root       the {@code AnchorPane} to attach the islands to
     */
    public static List<BorderPane> drawIslands(GameState gameState, double pageWidth, double pageHeight, AnchorPane root) {
        List<IslandState> islands = gameState.getIslands();
        List<BorderPane> islandIcons = new ArrayList<>();
        double deltaAngle = (2 * Constants.PI) / islands.size();
        double radius = pageHeight * DrawingConstants.ISLAND_RADIUS;
        for (int i = 0; i < islands.size(); i++) {
            String path = "/gameboard/islands/Isola_" + ((i % 3) + 1) + ".png";
            ImageView island = UtilsDrawer.getImageView(path, pageWidth * DrawingConstants.ISLAND_DIMENSION);
            Bounds imageBounds = island.boundsInParentProperty().get();
            double islandWidth = imageBounds.getWidth();
            double islandHeight = imageBounds.getHeight();
            BorderPane bp = new BorderPane(island);
            int steps = (i - gameState.getMNIndex() + gameState.getIslands().size()) % gameState.getIslands().size();
            int islandIndex = i;
            bp.setOnMouseClicked(event -> ObjectClickListeners.setIslandClicked(bp, steps, islandIndex));
            islandIcons.add(bp);
            double X = Math.cos(deltaAngle * i) * radius;
            double Y = Math.sin(deltaAngle * i) * radius;
            double startingXIsland = pageWidth * DrawingConstants.ISLAND_X - islandWidth / 2 + X;
            double startingYIsland = pageHeight * DrawingConstants.ISLAND_Y - islandHeight / 2 + Y;
            bp.setLayoutX(startingXIsland);
            bp.setLayoutY(startingYIsland);
            root.getChildren().add(bp);

            VBox elementsOnIsland = new VBox();
            GridPane studentsOnIsland = new GridPane();
            HBox towersOnIsland = new HBox();
            HBox noEntryOnIsland = new HBox();
            elementsOnIsland.setLayoutX(pageWidth * DrawingConstants.ISLAND_ELEMENTS_X);
            elementsOnIsland.setLayoutY(pageHeight * DrawingConstants.ISLAND_ELEMENTS_Y);
            elementsOnIsland.getChildren().add(studentsOnIsland);
            elementsOnIsland.getChildren().add(towersOnIsland);
            elementsOnIsland.getChildren().add(noEntryOnIsland);

            for (int j = 0; j < islands.get(i).getNumOfTowers(); j++) {
                String towerPath = "/gameboard/towers/" + islands.get(i).getTowerColor().toString().toLowerCase() + "_tower.png";
                ImageView tower = UtilsDrawer.getImageView(towerPath, islandWidth / DrawingConstants.ISLAND_TOWER_DIVISOR);
                towersOnIsland.getChildren().add(tower);
            }

            List<BorderPane> studentsToDraw = new ArrayList<>();
            for (int j = 0; j < islands.get(i).getStudents().size(); j++) {
                Student s = islands.get(i).getStudents().get(j);
                String studentPath = "/gameboard/students/student_" + s.getColor().toString().toLowerCase() + ".png";
                ImageView student = UtilsDrawer.getImageView(studentPath, islandWidth / DrawingConstants.ISLAND_STUDENT_DIVISOR);
                BorderPane studentBorderPane = new BorderPane(student);
                studentBorderPane.setOnMouseClicked(event ->
                        ObjectClickListeners.setStudentOnIslandClicked(studentBorderPane, s.getColor(), islandIndex));
                studentsToDraw.add(studentBorderPane);

                studentsOnIsland.add(studentBorderPane, j % 4, j / 4 + 1);
            }
            DrawingComponents.studentsOnIslands.put(i, studentsToDraw);

            for (int j = 0; j < islands.get(i).getNoEntryNum(); j++) {
                ImageView noEntry = UtilsDrawer.getImageView("/gameboard/deny_island_icon.png", islandWidth / DrawingConstants.ISLAND_NOENTRY_DIVISOR);

                BorderPane noEntryBorderPane = new BorderPane(noEntry);
                noEntryOnIsland.getChildren().add(noEntryBorderPane);
            }

            bp.getChildren().add(elementsOnIsland);

            if (i == gameState.getMNIndex()) {
                ImageView mn = UtilsDrawer.getImageView("/gameboard/mother_nature.png", islandWidth * DrawingConstants.ISLAND_MN_DIM);
                BorderPane motherNature = new BorderPane(mn);
                motherNature.setLayoutX(0);
                motherNature.setLayoutY(islandHeight / DrawingConstants.ISLAND_MN_Y_DIVISOR);
                bp.getChildren().add(motherNature);
            }
        }

        return islandIcons;
    }

}
