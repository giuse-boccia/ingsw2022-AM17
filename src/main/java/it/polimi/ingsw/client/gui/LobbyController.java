package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.utils.DrawingConstants;
import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.server.game_state.GameState;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;

import java.util.List;

public class LobbyController implements GuiController {

    double width, height;
    GridPane gridPane;
    @FXML
    AnchorPane root;

    @FXML
    void initialize() {
        width = Screen.getPrimary().getVisualBounds().getWidth();
        height = Screen.getPrimary().getVisualBounds().getHeight();

        gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(width / 64);
        gridPane.setVgap(width / 64);
        AnchorPane.setLeftAnchor(gridPane, width / 64);
        AnchorPane.setRightAnchor(gridPane, width / 64);
        AnchorPane.setTopAnchor(gridPane, height / 8);
        root.getChildren().add(1, gridPane);
    }

    @Override
    public void receiveData(GameLobby lobby, GameState gameState, List<String> actions, String message, String username) {
        if (lobby == null) return;

        Platform.runLater(() -> drawLobby(lobby));
    }

    private void drawLobby(GameLobby lobby) {
        // Remove all elements set previously
        gridPane.getChildren().removeIf(node -> true);

        String text = "PLAYERS: " + lobby.getPlayers().length;
        if (lobby.getNumPlayers() != -1) {
            text += "/" + lobby.getNumPlayers() + "| Expert mode: " + (lobby.isExpert() ? " " : "not ") + "active";
        }

        Text playersTitle = new Text(text);
        playersTitle.setFont(Font.font(DrawingConstants.FONT_NAME, FontWeight.NORMAL, DrawingConstants.TITLE_FONT_SIZE));
        gridPane.add(playersTitle, 0, 0);

        for (int i = 0; i < lobby.getPlayers().length; i++) {
            Text newPlayerText = new Text(lobby.getPlayers()[i]);
            newPlayerText.setFont(Font.font(DrawingConstants.FONT_NAME, FontWeight.NORMAL, DrawingConstants.TITLE_FONT_SIZE));
            gridPane.add(newPlayerText, 0, i + 1);
        }
    }
}
