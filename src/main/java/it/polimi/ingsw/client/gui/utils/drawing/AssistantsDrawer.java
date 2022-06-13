package it.polimi.ingsw.client.gui.utils.drawing;

import it.polimi.ingsw.client.gui.utils.DrawingConstants;
import it.polimi.ingsw.client.gui.utils.ObjectClickListeners;
import it.polimi.ingsw.server.game_state.GameState;
import it.polimi.ingsw.server.game_state.PlayerState;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;

import java.util.ArrayList;
import java.util.List;

public class AssistantsDrawer {

    private static final List<BorderPane> assistantCards = new ArrayList<>();

    /**
     * Draws the assistants accordingly to the given {@code GameState}
     *
     * @param gameState  the {@code GameState} to fetch the information about the assistants from
     * @param pageWidth  the width of the screen
     * @param pageHeight the height of the screen
     * @param root       the {@code AnchorPane} to attach the islands to
     * @param username   the username of the player who owns the assistants
     */
    public static List<BorderPane> drawAssistants(GameState gameState, double startingX, double pageWidth, double pageHeight, AnchorPane root, String username) {
        PlayerState player = gameState.getPlayers().stream().filter(p -> p.getName().equals(username)).findAny().orElse(null);
        if (player != null && player.getAssistants() != null) {
            double initialX = startingX + pageWidth * DrawingConstants.OFFSET_OF_FIRST_ASSISTANT;
            double finalX = pageWidth * (1 - DrawingConstants.OFFSET_OF_FIRST_ASSISTANT);
            GridPane assistants = getAssistants(player.getAssistants(), pageWidth, pageHeight, initialX, finalX);
            root.getChildren().add(assistants);
        }
        return assistantCards;
    }

    /**
     * Returns a {@code GridPane} containing the assistants to be drawn
     *
     * @param assistants the {@code Array} of the assistants to be drawn
     * @param pageWidth  the width of the screen
     * @param pageHeight the height of the screen
     * @param initialX   the initial X coordinate for the assistants
     * @param finalX     the final X coordinate for the assistants
     * @return a {@code GridPane} containing the assistants to be drawn
     */
    private static GridPane getAssistants(int[] assistants, double pageWidth, double pageHeight, double initialX, double finalX) {
        if (assistants.length == 0) return new GridPane();
        double initialY = pageHeight * 0.77;
        double spaceForAssistants = finalX - initialX - (assistants.length - 1) * pageWidth * DrawingConstants.OFFSET_BETWEEN_ASSISTANTS;
        GridPane gridPane = new GridPane();
        gridPane.setLayoutX(initialX);
        gridPane.setLayoutY(initialY);
        gridPane.setHgap(pageWidth * DrawingConstants.OFFSET_BETWEEN_ASSISTANTS);
        gridPane.setVgap(pageHeight * DrawingConstants.OFFSET_OF_FIRST_ASSISTANT);

        for (int value : assistants) {
            String path = "/gameboard/assistants/Assistente (" + value + ").png";
            double minWidth = DrawingConstants.ASSISTANT_MAX_WIDTH_SINGLE_LINE;
            ImageView assistant = UtilsDrawer.getImageView(
                    path, spaceForAssistants * Math.min((double) 2 / assistants.length, minWidth));

            BorderPane assistantPane = new BorderPane(assistant);
            assistantPane.setOnMouseClicked(event -> ObjectClickListeners.setAssistantClicked(value, assistantPane));
            assistantCards.add(assistantPane);

            gridPane.add(assistantPane, value - 1, 0);
        }

        return gridPane;
    }


}
