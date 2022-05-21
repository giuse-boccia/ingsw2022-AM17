package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.model.game_state.GameState;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;

public class ActionController implements GuiController {

    @FXML
    AnchorPane root;
    double width, height;

    @FXML
    void initialize() {
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        width = bounds.getWidth();
        height = bounds.getHeight();

    }

    @Override
    public void receiveData(Object data) {
        if (data == null) return;
        GameState gameState = (GameState) data;

    }
}
