package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.utils.DrawingComponents;
import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.server.game_state.GameState;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Screen;

import java.util.List;

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
    public void receiveData(GameLobby lobby, GameState gameState, List<String> actions, String message, String username) {
        if (actions != null) {

        }

        if (gameState == null) return;

        Platform.runLater(() -> drawGameState(gameState, username));
    }

    private void drawGameState(GameState gameState, String username) {
        root.getChildren().removeIf(node -> true);

        switch (gameState.getPlayers().size()) {
            case 2 -> DrawingComponents.drawTwoPlayersGame(gameState, width, height, root, username);
            case 3 -> DrawingComponents.drawThreePlayersGame(gameState, width, height, root, username);
            default -> DrawingComponents.drawFourPlayersGame(gameState, width, height, root, username);
        }
    }
}
