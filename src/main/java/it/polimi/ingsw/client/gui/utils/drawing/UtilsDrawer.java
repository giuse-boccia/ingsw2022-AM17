package it.polimi.ingsw.client.gui.utils.drawing;

import it.polimi.ingsw.utils.constants.Paths;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;

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
        ImageView iv = new ImageView(new Image(Paths.COIN));
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

}
