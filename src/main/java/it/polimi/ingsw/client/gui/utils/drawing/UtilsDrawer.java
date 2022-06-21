package it.polimi.ingsw.client.gui.utils.drawing;

import it.polimi.ingsw.client.gui.utils.DrawingConstants;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Popup;

public class UtilsDrawer {
    /**
     * Returns an {@code ImageView} containing the {@code Image} from the given path
     *
     * @param x        the starting X coordinate of the {@code ImageView}
     * @param y        the starting Y coordinate of the {@code ImageView}
     * @param fitWidth the witdth to be set to the {@code ImageView}
     * @return an {@code ImageView} containing the {@code Image} from the given path
     */
    public static ImageView getCoinImageView(double x, double y, double fitWidth) {
        ImageView iv = new ImageView(new Image("/gameboard/Moneta_base.png"));
        iv.setPreserveRatio(true);
        iv.setFitWidth(fitWidth);
        iv.setX(x);
        iv.setY(y);
        return iv;
    }

    /**
     * Returns an {@code ImageView} containing the {@code Image} from the given path
     *
     * @param path     the {@code String} containing the position of the desired image
     * @param fitWidth the witdth to be set to the {@code ImageView}
     * @return an {@code ImageView} containing the {@code Image} from the given path
     */
    public static ImageView getImageView(String path, double fitWidth) {
        ImageView iv = new ImageView(new Image(path));
        iv.setPreserveRatio(true);
        iv.setFitWidth(fitWidth);
        return iv;
    }

    /**
     * Returns a {@code GridPane} accordingly to the given parameters
     *
     * @param x    the starting X coordinate of the {@code GridPane}
     * @param y    the starting Y coordinate of the {@code GridPane}
     * @param hgap the gap to be set horizontally between two elements of the {@code GridPane}
     * @param vgap the gap to be set vertically between two elements of the {@code GridPane}
     * @return a {@code GridPane} accordingly to the given parameters
     */
    public static GridPane getGridPane(double x, double y, double hgap, double vgap) {
        GridPane grid = new GridPane();
        grid.setLayoutX(x);
        grid.setLayoutY(y);
        grid.setHgap(hgap);
        grid.setVgap(vgap);
        return grid;
    }

    /**
     * Returns a {@code Popup} containing the name and the description of a character
     *
     * @param name        the name of the characters
     * @param description the description of the character
     * @return a {@code Popup} containing the name and the description of a character
     */
    public static Popup getCharactersHoverPanel(String name, String description) {
        Popup popup = new Popup();

        Text characterName = new Text(name);
        characterName.setFont(Font.font(DrawingConstants.FONT_NAME, FontWeight.NORMAL, DrawingConstants.SUBTITLE_FONT_SIZE));
        Label characterDescription = new Label(description);
        characterDescription.setFont(Font.font(DrawingConstants.FONT_NAME, FontWeight.NORMAL, DrawingConstants.PARAGRAPH_FONT_SIZE));
        characterDescription.setWrapText(true);
        VBox vBox = new VBox(characterName, characterDescription);
        vBox.setPrefSize(DrawingConstants.CHARACTER_HOVER_POPUP_WIDTH, DrawingConstants.CHARACTER_HOVER_POPUP_HEIGHT);
        vBox.setAlignment(Pos.CENTER);
        vBox.setStyle("-fx-background-image: url('/gameboard/backgrounds/parchment_bacgkround.png'); -fx-padding: 10");

        popup.setAutoHide(true);
        popup.getContent().add(vBox);
        return popup;
    }
}
