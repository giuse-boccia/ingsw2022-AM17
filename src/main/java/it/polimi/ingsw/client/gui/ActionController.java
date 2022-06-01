package it.polimi.ingsw.client.gui;

import it.polimi.ingsw.client.gui.utils.DrawingComponents;
import it.polimi.ingsw.messages.login.GameLobby;
import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.server.game_state.GameState;
import javafx.application.Platform;
import javafx.geometry.Rectangle2D;
import javafx.scene.layout.AnchorPane;
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
            Platform.runLater(() -> DrawingComponents.setCurrentActions(actions));
        }

        if (gameState == null) return;

        Platform.runLater(() -> drawGameState(gameState, username));
    }

    @Override
    public void askCharacterParameters(CharacterName name, boolean requireColor, boolean requireIsland, boolean isSwapCard, boolean moveOneStudentAway) {
        if (moveOneStudentAway) {
            Platform.runLater(() -> DrawingComponents.moveStudentAwayFromCard(name, name == CharacterName.move1FromCardToIsland));
        } else if (isSwapCard) {
            int maxStudents = name == CharacterName.swapUpTo3FromEntranceToCard ? 3 : 2;
            GuiView.showPopupForColorOrBound(maxStudents);
        } else if (requireColor) {
            GuiView.showPopupForColorOrBound(-1);
        } else if (requireIsland) {
            Platform.runLater(DrawingComponents::askIslandIndex);
        }
    }

    private void drawGameState(GameState gameState, String username) {
        DrawingComponents.clearAll(root);
        switch (gameState.getPlayers().size()) {
            case 2 -> DrawingComponents.drawTwoPlayersGame(gameState, width, height, root, username);
            case 3 -> DrawingComponents.drawThreePlayersGame(gameState, width, height, root, username);
            default -> DrawingComponents.drawFourPlayersGame(gameState, width, height, root, username);
        }
    }

    public void setRoot(AnchorPane root) {
        this.root = root;
    }
}
