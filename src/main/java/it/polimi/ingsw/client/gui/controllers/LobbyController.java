package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.utils.DrawingComponents;
import it.polimi.ingsw.client.gui.utils.DrawingConstants;
import it.polimi.ingsw.client.gui.utils.GuiCharacterType;
import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.model.characters.CharacterName;
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
import org.w3c.dom.ranges.DocumentRange;

import java.util.List;

public class LobbyController implements GuiController {

    double width, height;
    GridPane gridPane;
    @FXML
    AnchorPane root;

    /**
     * Initializes the lobby page in the login phase
     */
    @FXML
    void initialize() {
        width = Screen.getPrimary().getVisualBounds().getWidth();
        height = Screen.getPrimary().getVisualBounds().getHeight();

        gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.setHgap(width * DrawingConstants.LOBBY_HGAP);
        gridPane.setVgap(width * DrawingConstants.LOBBY_VGAP);
        AnchorPane.setLeftAnchor(gridPane, width * DrawingConstants.LOBBY_LEFT);
        AnchorPane.setRightAnchor(gridPane, width * DrawingConstants.LOBBY_RIGHT);
        AnchorPane.setTopAnchor(gridPane, height / DrawingConstants.LOBBY_TOP);
        root.getChildren().add(1, gridPane);
    }

    @Override
    public void receiveData(GameLobby lobby, GameState gameState, List<String> actions, String username) {
        if (lobby == null) return;

        Platform.runLater(() -> drawLobby(lobby));
    }

    @Override
    public void askCharacterParameters(CharacterName name, GuiCharacterType characterType) {

    }

    /**
     * Shows the lobby on the user screen
     *
     * @param lobby the {@code GameLobby} to be drawn
     */
    private void drawLobby(GameLobby lobby) {
        // Remove all elements set previously
        gridPane.getChildren().removeIf(node -> true);

        String text = "PLAYERS: " + lobby.getPlayers().size();
        if (lobby.getNumPlayers() != -1) {
            text += "/" + lobby.getNumPlayers() + "| Expert mode: " + (lobby.isExpert() ? " " : "not ") + "active";
        }

        Text playersTitle = new Text(text);
        playersTitle.setFont(Font.font(DrawingConstants.FONT_NAME, FontWeight.NORMAL, DrawingConstants.TITLE_FONT_SIZE));
        gridPane.add(playersTitle, 0, 0);

        for (int i = 0; i < lobby.getPlayers().size(); i++) {
            Text newPlayerText = new Text(lobby.getPlayers().get(i));
            newPlayerText.setFont(Font.font(DrawingConstants.FONT_NAME, FontWeight.NORMAL, DrawingConstants.TITLE_FONT_SIZE));
            gridPane.add(newPlayerText, 0, i + 1);
        }
    }
}
