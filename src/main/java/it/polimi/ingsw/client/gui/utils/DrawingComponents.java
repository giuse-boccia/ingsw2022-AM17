package it.polimi.ingsw.client.gui.utils;

import it.polimi.ingsw.client.gui.utils.drawing.*;
import it.polimi.ingsw.constants.Messages;
import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.server.game_state.GameState;
import it.polimi.ingsw.server.game_state.PlayerState;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DrawingComponents {

    private static final List<BorderPane> characterImages = new ArrayList<>();
    public static HashMap<Integer, List<BorderPane>> studentsOnIslands = new HashMap<>();
    private static List<AnchorPane> cloudImages = new ArrayList<>();
    private static final List<BorderPane> entranceStudents = new ArrayList<>();
    private static final List<BorderPane> diningGaps = new ArrayList<>();
    private static final List<BorderPane> diningStudents = new ArrayList<>();
    private static final HashMap<CharacterName, List<BorderPane>> studentsOnCharacter = new HashMap<>();
    private static List<BorderPane> assistantCards = new ArrayList<>();
    private static List<BorderPane> islands = new ArrayList<>();
    private static List<String> lastActions;

    /**
     * Draws the components of the {@code Game}
     *
     * @param gameState  the {@code GameState} to draw
     * @param pageWidth  the width of the screen
     * @param pageHeight the height of the screen
     * @param root       the {@code AnchorPane} to draw the different components on
     * @param username   the username of the {@code Player} who sees the screen
     */
    public static void drawComponents(GameState gameState, double pageWidth, double pageHeight, AnchorPane root, String username) {
        root.getStylesheets().add("/css/game_elements.css");
        List<PlayerState> players = gameState.getPlayers();
        double otherDashboardHeight = pageHeight * DrawingConstants.OTHER_DASHBOARD_HEIGHT;
        double megaDashboardHeight = pageHeight * DrawingConstants.MEGA_DASHBOARD_HEIGHT;
        VBox otherDashboards = new VBox();
        otherDashboards.setAlignment(Pos.CENTER);
        otherDashboards.setPrefHeight(pageHeight - megaDashboardHeight - pageHeight * 0.2);
        otherDashboards.setLayoutX(0);
        otherDashboards.setLayoutY(0);
        for (PlayerState player : players) {
            if (!player.getName().equals(username)) {
                VBox dashboardAndText = new VBox();
                BorderPane dashboard = DashboardsDrawer.drawDashboard(player, pageHeight, otherDashboards, username);
                AnchorPane text = DashboardsDrawer.drawDashboardText(player, pageWidth, pageHeight, gameState.isExpert());
                dashboardAndText.getChildren().add(dashboard);
                dashboardAndText.getChildren().add(text);
                otherDashboards.getChildren().add(dashboardAndText);
            } else {
                DashboardsDrawer.drawMegaDashboard(player, pageHeight, root, username);
                DashboardsDrawer.drawMegaDashboardText(player, pageWidth, pageHeight, root, gameState.isExpert());
            }
        }

        root.getChildren().add(otherDashboards);

        double assistantsAndCharactersStartingX = otherDashboardHeight / DrawingConstants.DASHBOARD_HEIGHT_OVER_WIDTH
                + pageWidth * DrawingConstants.ASSISTANT_AND_CHARACTERS_BEGINNING_PROPORTION;

        cloudImages = CloudsDrawer.drawClouds(gameState.getClouds(), pageWidth, pageHeight, root);
        islands = IslandsDrawer.drawIslands(gameState, pageWidth, pageHeight, root);
        assistantCards = AssistantsDrawer.drawAssistants(gameState, assistantsAndCharactersStartingX,
                pageHeight, root, username);
        // Draw all three characters
        if (gameState.isExpert()) {
            CharactersDrawer.drawCharacters(gameState.getCharacters(), pageWidth, pageHeight, assistantsAndCharactersStartingX, root);
        }
    }

    /**
     * Highlights with the correct color the given actions
     *
     * @param currentActions the list of actions to be highlighted
     */
    public static void highlightCurrentActions(List<String> currentActions) {
        lastActions = currentActions;
        for (String action : currentActions) {
            switch (action) {
                case Messages.ACTION_MOVE_STUDENT_TO_DINING, Messages.ACTION_MOVE_STUDENT_TO_ISLAND -> {
                    entranceStudents.forEach(DrawingComponents::setGoldenBorder);
                    diningGaps.forEach(DrawingComponents::setGoldenBorder);
                    islands.forEach(DrawingComponents::setGoldenBorder);
                }
                case Messages.ACTION_PLAY_ASSISTANT -> assistantCards.forEach(DrawingComponents::setGoldenBorder);
                case Messages.ACTION_PLAY_CHARACTER -> characterImages.forEach(DrawingComponents::setGoldenBorder);
                case Messages.ACTION_MOVE_MN -> islands.forEach(DrawingComponents::setGoldenBorder);
                case Messages.ACTION_FILL_FROM_CLOUD -> cloudImages.forEach(DrawingComponents::setGoldenBorder);
            }
        }
    }

    /**
     * Removes the golden border from all {@code Characters}
     */
    public static void removeGoldenBordersFromAllCharacters() {
        characterImages.forEach(character -> character.getStyleClass().clear());
    }

    /**
     * Sets a golden border to the given element
     *
     * @param element the element to set the golden border to
     */
    private static void setGoldenBorder(Node element) {
        element.getStyleClass().add(DrawingConstants.STYLE_HIGHLIGHT);
    }

    /**
     * Sets a golden blue to the given element
     *
     * @param element the element to set the golden border to
     */
    private static void setBlueBorders(Node element) {
        element.getStyleClass().add(DrawingConstants.STYLE_SWAP_CHARACTER_A);
    }

    /**
     * Sets a green border to the given element
     *
     * @param element the element to set the golden border to
     */
    private static void setGreenBorders(Node element) {
        element.getStyleClass().add(DrawingConstants.STYLE_MOVING_CHARACTER_A);
    }

    /**
     * Sets a blue border to the students in the {@code Entrance}
     */
    public static void setBlueBordersToEntranceStudents() {
        entranceStudents.forEach(DrawingComponents::setBlueBorders);
    }

    /**
     * Sets a blue border on the students on the given {@code Character}
     *
     * @param name the name of the {@code Character} whose students to set a blue border to
     */
    public static void setBlueBordersToCharacterStudents(CharacterName name) {
        studentsOnCharacter.get(name).forEach(DrawingComponents::setBlueBorders);
    }

    /**
     * Sets a blue border to the students in the {@code DinigRoom}
     */
    public static void setBlueBordersToDiningStudents() {
        diningStudents.forEach(DrawingComponents::setBlueBorders);
    }

    /**
     * Moves the selected {@code Student} away from the {@code Character} card to an {@code Island} or to the {@code DiningRoom}
     *
     * @param name     the name of the {@code Character} to move the selected {@code Student} from
     * @param toIsland true if the selected {@code Student} should be moved to an {@code Island}, false if it should be moved to the {@code DiningRoom}
     */
    public static void moveStudentAwayFromCard(CharacterName name, boolean toIsland) {
        if (studentsOnCharacter.containsKey(name)) {
            removeGoldenBordersFromAllElements();
            List<BorderPane> students = studentsOnCharacter.get(name);
            students.forEach(DrawingComponents::setGreenBorders);
            if (toIsland) {
                islands.forEach(DrawingComponents::setGreenBorders);
            } else {
                diningGaps.forEach(DrawingComponents::setGreenBorders);
            }
        }
    }

    /**
     * Makes every island able to be chosen by the user to fulfill an effect of a {@code Character} which involves an {@code Island}
     */
    public static void askIslandIndex() {
        removeGoldenBordersFromAllElements();
        islands.forEach(island -> island.getStyleClass().add(DrawingConstants.STYLE_ISLAND_CHARACTER_A));
    }

    /**
     * Removes the golden border from all the elements in the gameboard
     */
    public static void removeGoldenBordersFromAllElements() {
        diningGaps.forEach(gap -> gap.getStyleClass().clear());
        entranceStudents.forEach(student -> student.getStyleClass().clear());
        assistantCards.forEach(assistant -> assistant.getStyleClass().clear());
        cloudImages.forEach(cloud -> cloud.getStyleClass().clear());
        assistantCards.forEach(card -> card.getStyleClass().clear());
        islands.forEach(island -> island.getStyleClass().clear());
        studentsOnCharacter.values().forEach(list -> list.forEach(student -> student.getStyleClass().clear()));
        characterImages.forEach(character -> character.getStyleClass().clear());
    }

    /**
     * Clears every static list of elements which could be highlighted and also clears the given {@code AnchorPane}
     *
     * @param root the {@code AnchorPane} to be cleared
     */
    public static void clearAll(AnchorPane root) {
        root.getChildren().clear();

        characterImages.clear();
        cloudImages.clear();
        assistantCards.clear();
        entranceStudents.clear();
        diningStudents.clear();
        diningGaps.clear();
        studentsOnCharacter.clear();
        studentsOnIslands.clear();
        islands.clear();
    }

    /**
     * @return the {@code List} of {@code String} representing the possible actions for the player
     */
    public static List<String> getLastActions() {
        return lastActions;
    }

    public static void addCharacterImage(BorderPane characterImage) {
        characterImages.add(characterImage);
    }

    public static void addEntranceStudent(BorderPane student) {
        entranceStudents.add(student);
    }

    public static void addDiningStudent(BorderPane student) {
        diningStudents.add(student);
    }

    public static void addDiningGap(BorderPane gap) {
        diningGaps.add(gap);
    }

    public static void addStudentsOnCharacter(CharacterName characterName, List<BorderPane> students) {
        studentsOnCharacter.put(characterName, students);
    }
}
