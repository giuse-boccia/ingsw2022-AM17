package it.polimi.ingsw.client.gui.utils.drawing;

import it.polimi.ingsw.client.gui.utils.DrawingComponents;
import it.polimi.ingsw.client.gui.utils.DrawingConstants;
import it.polimi.ingsw.client.gui.utils.ObjectClickListeners;
import it.polimi.ingsw.server.game_state.CharacterState;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Popup;

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
    public static void drawCharacters(List<CharacterState> characters, double pageWidth, double pageHeight, double firstCharacterX, AnchorPane root) {
        double characterY = pageHeight / DrawingConstants.CHARACTER_Y_DIVISOR;

        for (CharacterState character : characters) {
            String imagePath = "/gameboard/characters/" + character.getCharacterName() + ".jpg";
            ImageView characterImage = UtilsDrawer.getImageView(imagePath, pageWidth * DrawingConstants.CHARACTER_CARD_PROPORTION);

            BorderPane characterToAdd = new BorderPane(characterImage);
            characterToAdd.setLayoutX(firstCharacterX);
            characterToAdd.setLayoutY(characterY);
            characterToAdd.setOnMouseClicked(event -> ObjectClickListeners.setCharacterClicked(character.getCharacterName(), characterToAdd));
            DrawingComponents.addCharacterImage(characterToAdd);
            Popup characterPopup = getCharactersHoverPanel(character.getCharacterName().getName(), character.getCharacterName().getDescription());
            double characterXCoord = firstCharacterX;
            double characterYCoord = characterY + characterToAdd.getBoundsInParent().getHeight();
            characterImage.hoverProperty().addListener((observableValue, oldValue, newValue) -> {
                if (newValue) {
                    characterPopup.show(characterToAdd, characterXCoord, characterYCoord);
                } else {
                    characterPopup.hide();
                }
            });

            root.getChildren().add(characterToAdd);

            if (character.hasCoin()) {
                double imageWidth = pageWidth * DrawingConstants.COIN_PROPORTION;
                ImageView coin = UtilsDrawer.getCoinImageView(
                        firstCharacterX + pageWidth * (DrawingConstants.CHARACTER_CARD_PROPORTION) - imageWidth,
                        pageHeight * DrawingConstants.CHARACTER_COIN_DIM,
                        imageWidth);
                root.getChildren().add(coin);
            }
            if (character.getStudents() != null) {
                GridPane grid = new GridPane();
                grid.setLayoutX(firstCharacterX + pageWidth * DrawingConstants.SPACE_BETWEEN_STUDENTS_ON_CHARACTERS);
                grid.setLayoutY(characterY - 2 * pageWidth * DrawingConstants.CHARACTER_STUDENT_DIM);
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

            firstCharacterX += pageWidth * (DrawingConstants.CHARACTER_CARD_PROPORTION + DrawingConstants.SPACE_BETWEEN_CHARACTERS_PROPORTION);
        }
    }

    /**
     * Returns a {@code Popup} containing the name and the description of a character
     *
     * @param name        the name of the characters
     * @param description the description of the character
     * @return a {@code Popup} containing the name and the description of a character
     */
    private static Popup getCharactersHoverPanel(String name, String description) {
        Popup popup = new Popup();

        Text characterName = new Text(name);
        characterName.setFont(Font.font(DrawingConstants.FONT_NAME, FontWeight.NORMAL, DrawingConstants.SUBTITLE_FONT_SIZE));
        Label characterDescription = new Label(description);
        characterDescription.setFont(Font.font(DrawingConstants.FONT_NAME, FontWeight.NORMAL, DrawingConstants.PARAGRAPH_FONT_SIZE));
        characterDescription.setWrapText(true);
        VBox vBox = new VBox(characterName, characterDescription);
        vBox.setPrefWidth(DrawingConstants.CHARACTER_HOVER_POPUP_WIDTH);
        vBox.setAlignment(Pos.CENTER);
        vBox.setStyle("-fx-background-image: url('/gameboard/backgrounds/parchment_bacgkround.png'); -fx-padding: 10");

        popup.setAutoHide(true);
        popup.getContent().add(vBox);
        return popup;
    }

}
