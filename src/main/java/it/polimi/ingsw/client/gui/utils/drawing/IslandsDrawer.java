package it.polimi.ingsw.client.gui.utils.drawing;

import it.polimi.ingsw.client.gui.utils.DrawingComponents;
import it.polimi.ingsw.client.gui.utils.DrawingConstants;
import it.polimi.ingsw.client.gui.utils.ObjectClickListeners;
import it.polimi.ingsw.utils.constants.Constants;
import it.polimi.ingsw.model.game_objects.Student;
import it.polimi.ingsw.server.game_state.GameState;
import it.polimi.ingsw.server.game_state.IslandState;
import javafx.geometry.Bounds;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;

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
            // Draw the Island background
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

            bp.getChildren().add(getElementsOnIsland(
                    islands.get(i), islandIndex,
                    pageWidth * DrawingConstants.ISLAND_ELEMENTS_X, pageHeight * DrawingConstants.ISLAND_ELEMENTS_Y,
                    islandWidth));

            if (i == gameState.getMNIndex()) {
                bp.getChildren().add(getMotherNatureImage(
                        islandWidth * DrawingConstants.ISLAND_MN_DIM, islandHeight / DrawingConstants.ISLAND_MN_Y_DIVISOR));
            }
        }

        return islandIcons;
    }

    /**
     * Returns a {@code VBox} containing all the elements on the input island - towers, students and noEntry pawns
     *
     * @param island      the {@link IslandState} to draw all the elements of
     * @param islandIndex the index of the input {@link  IslandState} inside the array of islands
     * @param islandX     the x position of the upper-left angle of the island
     * @param islandY     the y position of the upper-left angle of the island
     * @param islandWidth the width of the drawn island
     * @return a {@code VBox} containing all the elements on the input island
     */
    private static VBox getElementsOnIsland(IslandState island, int islandIndex, double islandX, double islandY, double islandWidth) {
        VBox elementsOnIsland = new VBox();
        GridPane studentsOnIsland = new GridPane();
        HBox towersOnIsland = new HBox();
        HBox noEntryOnIsland = new HBox();
        elementsOnIsland.setLayoutX(islandX);
        elementsOnIsland.setLayoutY(islandY);
        elementsOnIsland.getChildren().add(studentsOnIsland);
        elementsOnIsland.getChildren().add(towersOnIsland);
        elementsOnIsland.getChildren().add(noEntryOnIsland);

        // Add the island placed on the island
        for (int j = 0; j < island.getNumOfTowers(); j++) {
            String towerPath = "/gameboard/towers/" + island.getTowerColor().toString().toLowerCase() + "_tower.png";
            ImageView tower = UtilsDrawer.getImageView(towerPath, islandWidth / DrawingConstants.ISLAND_TOWER_DIVISOR);
            towersOnIsland.getChildren().add(tower);
        }

        // Add all the students present on the island
        List<BorderPane> studentsToDraw = new ArrayList<>();
        for (int j = 0; j < island.getStudents().size(); j++) {
            Student s = island.getStudents().get(j);
            String studentPath = "/gameboard/students/student_" + s.getColor().toString().toLowerCase() + ".png";
            ImageView student = UtilsDrawer.getImageView(studentPath, islandWidth / DrawingConstants.ISLAND_STUDENT_DIVISOR);
            BorderPane studentBorderPane = new BorderPane(student);
            studentBorderPane.setOnMouseClicked(event ->
                    ObjectClickListeners.setStudentOnIslandClicked(studentBorderPane, s.getColor(), islandIndex));
            studentsToDraw.add(studentBorderPane);

            studentsOnIsland.add(studentBorderPane, j % 4, j / 4 + 1);
        }
        DrawingComponents.studentsOnIslands.put(islandIndex, studentsToDraw);

        // Draw all the noEntry pawn placed on the island, if any
        for (int j = 0; j < island.getNoEntryNum(); j++) {
            ImageView noEntry = UtilsDrawer.getImageView("/gameboard/deny_island_icon.png", islandWidth / DrawingConstants.ISLAND_NOENTRY_DIVISOR);
            BorderPane noEntryBorderPane = new BorderPane(noEntry);
            noEntryOnIsland.getChildren().add(noEntryBorderPane);
        }

        return elementsOnIsland;
    }

    /**
     * Returns an {@code ImageView} representing mother nature
     *
     * @param mnWidth the fixed width of mother nature image
     * @param mnY     the y position of the upper-left angle of mother nature image
     * @return an {@code ImageView} representing mother nature
     */
    private static ImageView getMotherNatureImage(double mnWidth, double mnY) {
        ImageView mn = UtilsDrawer.getImageView("/gameboard/mother_nature.png", mnWidth);
        BorderPane motherNature = new BorderPane(mn);
        motherNature.setLayoutX(0);
        motherNature.setLayoutY(mnY);
        return mn;
    }

}
