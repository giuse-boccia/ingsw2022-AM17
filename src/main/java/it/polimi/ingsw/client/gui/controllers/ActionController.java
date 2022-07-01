package it.polimi.ingsw.client.gui.controllers;

import it.polimi.ingsw.client.gui.GuiView;
import it.polimi.ingsw.client.gui.utils.ChatView;
import it.polimi.ingsw.client.gui.utils.DrawingComponents;
import it.polimi.ingsw.client.gui.utils.DrawingConstants;
import it.polimi.ingsw.client.gui.utils.GuiCharacterType;
import it.polimi.ingsw.languages.Messages;
import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.server.game_state.GameState;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.geometry.Rectangle2D;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Screen;

import java.util.List;

public class ActionController implements GuiController {

    AnchorPane root;
    double width, height;

    public void initialize() {
        Rectangle2D bounds = Screen.getPrimary().getVisualBounds();
        width = bounds.getWidth();
        height = bounds.getHeight();
    }

    @Override
    public void receiveData(GameLobby lobby, GameState gameState, List<String> actions, String username) {
        if (actions != null) {
            Platform.runLater(() -> DrawingComponents.highlightCurrentActions(actions));
        }

        if (gameState == null) return;

        Platform.runLater(() -> drawGameState(gameState, username));
    }

    @Override
    public void askCharacterParameters(CharacterName name, GuiCharacterType characterType) {
        switch (characterType) {
            case MOVE_ONE_STUDENT_AWAY -> Platform.runLater(() ->
                    DrawingComponents.moveStudentAwayFromCard(name, name == CharacterName.move1FromCardToIsland));
            case SWAP -> {
                int maxStudents = name == CharacterName.swapUpTo3FromEntranceToCard ? 3 : 2;
                GuiView.showPopupForColorOrBound(maxStudents);
            }
            case COLOR -> GuiView.showPopupForColorOrBound(-1);
            case ISLAND -> Platform.runLater(DrawingComponents::askIslandIndex);
        }
    }

    /**
     * Draws the given {@code GameState} on the GUI
     *
     * @param gameState the {@code GameState} to be drawn
     * @param username  the username of the {@code Player} whose GUI will be drawn the given {@code GameState} on
     */
    private void drawGameState(GameState gameState, String username) {
        DrawingComponents.clearAll(root);
        DrawingComponents.drawComponents(gameState, width, height, root, username);

        root.getChildren().add(getOpenChatButton());
    }

    /**
     * Returns a {@code VBox} containing a text and the button which has to be clicked in order to open the chat panel
     *
     * @return a {@code VBox} with a {@code Text} and a {@code Button}
     */
    private VBox getOpenChatButton() {
        Button openChatButton = new Button("...");
        openChatButton.setStyle("-fx-font-size: 24; -fx-background-color: white; -fx-background-radius: 40; -fx-border-radius: 40");
        openChatButton.setOnMouseClicked(event -> ChatView.openChatPanel());

        Text chatText = new Text(Messages.getMessage("game_chat"));
        chatText.setFont(Font.font(DrawingConstants.FONT_NAME, FontWeight.NORMAL, DrawingConstants.SUBTITLE_FONT_SIZE));

        VBox chatVbox = new VBox(openChatButton, chatText);
        chatVbox.setSpacing(10);
        chatVbox.setAlignment(Pos.CENTER);
        chatVbox.setLayoutX(width - 100);
        chatVbox.setLayoutY(height - 120);

        return chatVbox;
    }

    public void setRoot(AnchorPane root) {
        this.root = root;
    }
}
