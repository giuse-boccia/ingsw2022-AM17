package it.polimi.ingsw.client.gui.utils.languages;

import it.polimi.ingsw.client.gui.utils.DrawingConstants;
import it.polimi.ingsw.utils.constants.Paths;
import javafx.scene.control.ListCell;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class FlagListCell extends ListCell<String> {

    @Override
    protected void updateItem(String languageTag, boolean isEmpty) {
        super.updateItem(languageTag, isEmpty);
        setText(languageTag);
        if (isEmpty) return;
        ImageView icon = new ImageView(new Image(Paths.FLAGS_START + languageTag + Paths.FLAGS_END));
        icon.setFitWidth(DrawingConstants.FLAG_ICON_WIDTH);
        icon.setPreserveRatio(true);
        setGraphic(icon);
    }
}
