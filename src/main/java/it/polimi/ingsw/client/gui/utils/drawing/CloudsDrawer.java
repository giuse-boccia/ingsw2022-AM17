package it.polimi.ingsw.client.gui.utils.drawing;

import it.polimi.ingsw.client.gui.utils.DrawingConstants;
import it.polimi.ingsw.client.gui.utils.ObjectClickListeners;
import it.polimi.ingsw.server.game_state.CloudState;
import it.polimi.ingsw.utils.constants.Paths;
import javafx.geometry.Bounds;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;

public class CloudsDrawer {

    /**
     * Draws the given clouds with the correct students on each one
     *
     * @param clouds     the {@code List} of clouds to be drawn
     * @param pageWidth  the width of the screen
     * @param pageHeight the height of the screen
     * @param root       the {@code AnchorPane} to attach the clouds to
     */
    public static List<AnchorPane> drawClouds(List<CloudState> clouds, double pageWidth, double pageHeight, AnchorPane root) {
        GridPane cloudGrid = new GridPane();
        List<AnchorPane> cloudImages = new ArrayList<>();
        double layoutX = pageWidth * DrawingConstants.CLOUD_STARTING_X - pageWidth * DrawingConstants.X_OFFSET_OF_CLOUD_FROM_CENTER;
        double layoutY = pageHeight * DrawingConstants.CLOUD_STARTING_Y - pageHeight * DrawingConstants.Y_OFFSET_OF_CLOUD_FROM_CENTER;
        cloudGrid.setLayoutY(layoutY);
        cloudGrid.setLayoutX(layoutX);
        cloudGrid.setHgap(pageHeight * DrawingConstants.SPACE_BETWEEN_CLOUDS);
        cloudGrid.setVgap(pageHeight * DrawingConstants.SPACE_BETWEEN_CLOUDS);

        for (int i = 0; i < clouds.size(); i++) {
            CloudState cloud = clouds.get(i);
            AnchorPane cloudToDraw = getCloudWithStudents(cloud, pageWidth, pageHeight);
            int cloudIndex = i;
            cloudToDraw.setOnMouseClicked(event -> ObjectClickListeners.setCloudClicked(cloudToDraw, cloudIndex));
            cloudGrid.add(cloudToDraw, i % 2, i / 2);
            cloudImages.add(cloudToDraw);
        }
        root.getChildren().add(cloudGrid);
        return cloudImages;
    }


    /**
     * Returns an {@code AnchorPane} containing the given cloud with the correct students on it
     *
     * @param cloud      the cloud to put te correct students on
     * @param pageWidth  the width of the screen
     * @param pageHeight the height of the screen
     * @return an {@code AnchorPane} containing the given cloud with the correct students on it
     */
    private static AnchorPane getCloudWithStudents(CloudState cloud, double pageWidth, double pageHeight) {
        ImageView cloudBackground = new ImageView(new Image(Paths.CLOUD));
        cloudBackground.setPreserveRatio(true);
        cloudBackground.setFitHeight(pageHeight * DrawingConstants.CLOUD_HEIGHT);

        GridPane studentsPane = new GridPane();
        Bounds imageBounds = cloudBackground.boundsInParentProperty().get();
        studentsPane.setLayoutX(imageBounds.getWidth() * DrawingConstants.OFFSET_OF_STUDENT_FROM_CLOUD);
        studentsPane.setLayoutY(imageBounds.getHeight() * DrawingConstants.OFFSET_OF_STUDENT_FROM_CLOUD);
        studentsPane.setHgap(imageBounds.getWidth() * DrawingConstants.OFFSET_BETWEEN_STUDENTS_IN_CLOUD);
        studentsPane.setVgap(imageBounds.getHeight() * DrawingConstants.OFFSET_BETWEEN_STUDENTS_IN_CLOUD);

        if (cloud.getStudents() != null) {
            for (int j = 0; j < cloud.getStudents().size(); j++) {
                String studentPath = Paths.STUDENT_START +
                        cloud.getStudents().get(j).getColor().toString().toLowerCase() + Paths.PNG;
                ImageView student = UtilsDrawer.getImageView(studentPath, pageWidth * DrawingConstants.STUDENT_ON_CLOUD_DIMENSION);

                studentsPane.add(student, j % 2, j / 2);
            }
        }

        return new AnchorPane(cloudBackground, studentsPane);
    }

}
