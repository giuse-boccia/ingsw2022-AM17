package it.polimi.ingsw.client.gui.utils.drawing;

import it.polimi.ingsw.client.gui.utils.DrawingComponents;
import it.polimi.ingsw.client.gui.utils.DrawingConstants;
import it.polimi.ingsw.client.gui.utils.ObjectClickListeners;
import it.polimi.ingsw.server.game_state.CharacterState;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;

public class CharactersDrawer {

    /**
     * Draws the given characters with the correct students on each one (if they have some)
     *
     * @param characters the {@code List} of characters to be drawn
     * @param pageWidth  the width of the screen
     * @param pageHeight the height of the screen
     * @param root       the {@code AnchorPane} to attach the clouds to
     */
    public static void drawCharacters(List<CharacterState> characters, double pageWidth, double pageHeight, double dashboardHeight, AnchorPane root) {
        double coordX = dashboardHeight / DrawingConstants.DASHBOARD_HEIGHT_OVER_WIDTH + pageWidth * DrawingConstants.CHARACTERS_BEGINNING_PROPORTION;
        int heightProportion = 13;

        for (CharacterState character : characters) {
            String imagePath = "/gameboard/characters/" + character.getCharacterName() + ".jpg";
            ImageView characterImage = UtilsDrawer.getImageView(imagePath, pageWidth * DrawingConstants.CHARACTER_CARD_PROPORTION);

            BorderPane characterToAdd = new BorderPane(characterImage);
            characterToAdd.setLayoutX(coordX);
            characterToAdd.setLayoutY(pageHeight / heightProportion);
            characterToAdd.setOnMouseClicked(event -> ObjectClickListeners.setCharacterClicked(character.getCharacterName(), characterToAdd));
            DrawingComponents.addCharacterImage(characterToAdd);
            root.getChildren().add(characterToAdd);

            if (character.hasCoin()) {
                double imageWidth = pageWidth * DrawingConstants.COIN_PROPORTION;
                ImageView coin = UtilsDrawer.getCoinImageView(
                        coordX + pageWidth * (DrawingConstants.CHARACTER_CARD_PROPORTION) - imageWidth,
                        pageHeight * DrawingConstants.CHARACTER_COIN_DIM,
                        imageWidth);
                root.getChildren().add(coin);
            }
            if (character.getStudents() != null) {
                GridPane grid = new GridPane();
                grid.setLayoutX(coordX + pageWidth * DrawingConstants.SPACE_BETWEEN_STUDENTS_ON_CHARACTERS);
                grid.setLayoutY(pageHeight / heightProportion - 2 * pageWidth * DrawingConstants.CHARACTER_STUDENT_DIM);
                List<BorderPane> studentsOnCharacter = new ArrayList<>();
                for (int i = 0; i < character.getStudents().size(); i++) {
                    String studentPath = "/gameboard/students/student_" +
                            character.getStudents().get(i).getColor().toString().toLowerCase() + ".png";
                    ImageView student = UtilsDrawer.getImageView(studentPath, pageWidth * DrawingConstants.CHARACTER_STUDENT_DIM);

                    BorderPane studentPane = new BorderPane(student);
                    int index = i;
                    studentPane.setOnMouseClicked(event ->
                            ObjectClickListeners.setStudentOnCharacterClicked(
                                    character.getStudents().get(index).getColor(), studentPane))
                    ;
                    studentsOnCharacter.add(studentPane);

                    grid.add(studentPane, i / 2, i % 2);
                }
                DrawingComponents.addStudentsOnCharacter(character.getCharacterName(), studentsOnCharacter);

                root.getChildren().add(grid);
            }

            coordX += pageWidth * (DrawingConstants.CHARACTER_CARD_PROPORTION + DrawingConstants.SPACE_BETWEEN_CHARACTERS_PROPORTION);
        }
    }

}
