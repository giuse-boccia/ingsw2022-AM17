package it.polimi.ingsw.client.gui.utils.drawing;

import it.polimi.ingsw.client.gui.utils.DrawingConstants;
import it.polimi.ingsw.client.gui.utils.ObjectClickListeners;
import it.polimi.ingsw.server.game_state.GameState;
import it.polimi.ingsw.server.game_state.PlayerState;
import it.polimi.ingsw.utils.constants.Paths;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;

public class AssistantsDrawer {

    private static final List<BorderPane> assistantCards = new ArrayList<>();
    public static ImageView lastHoveredCard;
    public static BorderPane lastHoveredBorderPane;

    /**
     * Draws the assistants accordingly to the given {@code GameState}
     *
     * @param gameState  the {@code GameState} to fetch the information about the assistants from
     * @param pageHeight the height of the screen
     * @param root       the {@code AnchorPane} to attach the islands to
     * @param username   the username of the player who owns the assistants
     */
    public static List<BorderPane> drawAssistants(GameState gameState, double initialX, double pageHeight, AnchorPane root, String username) {
        PlayerState player = gameState.getPlayers().stream().filter(p -> p.getName().equals(username)).findAny().orElse(null);
        if (player != null && player.getAssistants() != null) {
            double initialY = pageHeight * DrawingConstants.ASSISTANT_Y;
            HBox assistants = getAssistants(player.getAssistants(), pageHeight, initialX, initialY);
            root.getChildren().add(assistants);
        }
        return assistantCards;
    }

    /**
     * Returns a {@code HBox} containing the assistants to be drawn
     *
     * @param assistants the {@code Array} of the assistants to be drawn
     * @param pageHeight the height of the screen
     * @param initialX   the initial X coordinate for the assistants
     * @return a {@code HBox} containing the assistants to be drawn
     */
    private static HBox getAssistants(int[] assistants, double pageHeight, double initialX, double initialY) {
        if (assistants.length == 0) return new HBox();
        HBox assistantsHBox = new HBox();
        assistantsHBox.setLayoutX(initialX);
        assistantsHBox.setLayoutY(initialY);
        assistantsHBox.setSpacing(DrawingConstants.OFFSET_BETWEEN_ASSISTANTS);

        // Remember that the range of possible assistant values is between 1 and 10
        for (int value : assistants) {
            String path = Paths.ASSISTANT_START + value + Paths.ASSISTANT_END;
            ImageView assistant = UtilsDrawer.getImageView(path, DrawingConstants.ASSISTANT_BASE_WIDTH);

            BorderPane assistantPane = new BorderPane(assistant);

            assistant.hoverProperty().addListener((obs, oldVal, newVal) -> {
                if (lastHoveredCard == null || newVal) {
                    assistant.setFitWidth(DrawingConstants.ASSISTANT_HOVERED_WIDTH);
                    assistantPane.setViewOrder(-1);
                    assistant.setY(-50);
                    lastHoveredCard = assistant;
                    lastHoveredBorderPane = assistantPane;
                } else {
                    lastHoveredCard.setFitWidth(DrawingConstants.ASSISTANT_BASE_WIDTH);
                    lastHoveredBorderPane.setViewOrder(0);
                    lastHoveredCard.setY(0);
                }
            });

            assistantPane.setOnMouseClicked(event -> ObjectClickListeners.setAssistantClicked(value, assistantPane));
            assistantCards.add(assistantPane);

            assistantsHBox.getChildren().add(assistantPane);
        }

        return assistantsHBox;
    }


}
