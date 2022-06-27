package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.utils.DrawingConstants;
import it.polimi.ingsw.client.gui.utils.GuiCharacterType;
import it.polimi.ingsw.languages.MessageResourceBundle;
import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.server.game_state.GameState;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
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
    private Label gameLobbyLabel;
    @FXML
    private AnchorPane root;

    /**
     * Initializes the lobby page in the login phase
     */
    @FXML
    void initialize() {
        gameLobbyLabel.setText(MessageResourceBundle.getMessage("game_lobby_title"));

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

        String text = MessageResourceBundle.getMessage("players_title_create_or_load") + lobby.getPlayers().size();
        if (lobby.getNumPlayers() != -1) {
            text += "/" + lobby.getNumPlayers() + MessageResourceBundle.getMessage("expert_mode")
                    + (lobby.isExpert() ? " " : MessageResourceBundle.getMessage("not_with_space")) + MessageResourceBundle.getMessage("active");
        }

        Text playersTitle = new Text(text);
        playersTitle.setFont(Font.font(DrawingConstants.FONT_NAME, FontWeight.NORMAL, DrawingConstants.TITLE_FONT_SIZE));
        gridPane.add(playersTitle, 0, 0);

        if (lobby.isFromSavedGame()) {
            List<String> readyPlayers = lobby.getPlayers();
            String[] playersFromSavedGame = lobby.getPlayersFromSavedGame();
            for (int i = 0; i < playersFromSavedGame.length; i++) {
                String player = playersFromSavedGame[i];
                boolean playerIsReady = lobby.getPlayers().contains(player);
                String playerString = playerIsReady ? MessageResourceBundle.getMessage("ready") : MessageResourceBundle.getMessage("waiting");
                playerString += "  " + player;

                Text playerText = new Text(playerString);
                playerText.setFont(Font.font(DrawingConstants.FONT_NAME, FontWeight.NORMAL, DrawingConstants.TITLE_FONT_SIZE));
                gridPane.add(playerText, 0, i + 1);

            }
        } else {
            for (int i = 0; i < lobby.getPlayers().size(); i++) {
                Text newPlayerText = new Text(lobby.getPlayers().get(i));
                newPlayerText.setFont(Font.font(DrawingConstants.FONT_NAME, FontWeight.NORMAL, DrawingConstants.TITLE_FONT_SIZE));
                gridPane.add(newPlayerText, 0, i + 1);
            }
        }
    }
}
