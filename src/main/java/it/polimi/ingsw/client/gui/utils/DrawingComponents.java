package it.polimi.ingsw.client.gui.utils;

import it.polimi.ingsw.model.characters.CharacterName;
import it.polimi.ingsw.model.game_objects.Color;
import it.polimi.ingsw.model.game_objects.Student;
import it.polimi.ingsw.server.game_state.*;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DrawingComponents {

    private static double dashboardHeight;
    private static final List<BorderPane> characterImages = new ArrayList<>();
    private static final List<AnchorPane> cloudImages = new ArrayList<>();
    private static final List<BorderPane> assistantCards = new ArrayList<>();
    private static final List<BorderPane> entranceStudents = new ArrayList<>();
    private static final List<BorderPane> diningGaps = new ArrayList<>();
    private static final List<BorderPane> diningStudents = new ArrayList<>();
    private static final HashMap<CharacterName, List<BorderPane>> studentsOnCharacter = new HashMap<>();
    private static ImageView motherNature;
    private static List<ImageView> islands = new ArrayList<>();
    private static List<String> currentActions;

    public static void drawFourPlayersGame(GameState gameState, double width, double height, AnchorPane root, String username) {
        List<PlayerState> players = gameState.getPlayers();
        drawGameComponentsForTwo(width, height, root, gameState, username);
        drawDashboard(players.get(2), 0, height - (width * (1454.0 / 3352.0) * 0.4), width * 0.4, root, username);
        double textStartingY = height - (width * (1454.0 / 3352.0) * 0.4) - height / 45;
        drawDashboardText(players.get(2), 0, textStartingY, root, width, height);
        drawDashboard(players.get(3), width * 0.6, height - (width * (1454.0 / 3352.0) * 0.4), width * 0.4, root, username);
        drawDashboardText(players.get(3), width * 0.6, textStartingY, root, width, height);

        drawAssistants(gameState, width, height, root, username, 4);
    }

    public static void drawThreePlayersGame(GameState gameState, double width, double height, AnchorPane root, String username) {
        List<PlayerState> players = gameState.getPlayers();
        drawGameComponentsForTwo(width, height, root, gameState, username);
        drawDashboard(players.get(2), 0, height - (width * (1454.0 / 3352.0) * 0.4), width * 0.4, root, username);
        drawDashboardText(players.get(2), 0, height - (width * (1454.0 / 3352.0) * 0.4) - height / 45, root, width, height);

        drawAssistants(gameState, width, height, root, username, 3);
    }

    public static void drawTwoPlayersGame(GameState gameState, double width, double height, AnchorPane root, String username) {
        drawGameComponentsForTwo(width, height, root, gameState, username);

        drawAssistants(gameState, width, height, root, username, 2);
    }

    private static void drawGameComponentsForTwo(double width, double height, AnchorPane root, GameState gameState, String username) {
        root.getStylesheets().add("/css/game_elements.css");

        dashboardHeight = width * (1454.0 / 3352.0) * 0.4;
        List<PlayerState> players = gameState.getPlayers();
        drawDashboard(players.get(0), 0, 0, width * 0.4, root, username);
        double textStartingY = dashboardHeight + height / 30;
        drawDashboardText(players.get(0), 0, textStartingY, root, width, height);
        drawDashboard(players.get(1), width * 0.6, 0, width * 0.4, root, username);
        drawDashboardText(players.get(1), width * 0.6, textStartingY, root, width, height);

        drawClouds(gameState.getClouds(), width, height, root);
        drawIslands(gameState.getIslands(), width, height, root);
        // Draw all three characters
        if (gameState.isExpert()) {
            drawCharacters(gameState.getCharacters(), width, height, root);
        }
    }

    private static void drawIslands(List<IslandState> islands, double width, double height, AnchorPane root) {
        double heightForIslands = height - 2 * dashboardHeight;
        // TODO draw islands - using some math...

    }

    private static void drawAssistants(GameState gameState, double width, double height, AnchorPane root, String username, int numPlayers) {
        PlayerState player = gameState.getPlayers().stream().filter(p -> p.getName().equals(username)).findAny().orElse(null);
        if (player != null && player.getAssistants() != null) {
            double initialWidth = width * (0.4 + DrawingConstants.OFFSET_OF_FIRST_ASSISTANT);
            double finalWidth = width * (0.6 - DrawingConstants.OFFSET_OF_FIRST_ASSISTANT);
            switch (numPlayers) {
                case 2 -> {
                    initialWidth = width * DrawingConstants.OFFSET_OF_FIRST_ASSISTANT;
                    finalWidth = width * (1 - DrawingConstants.OFFSET_OF_FIRST_ASSISTANT);
                }
                case 3 -> {
                    finalWidth = width * (1 - DrawingConstants.OFFSET_OF_FIRST_ASSISTANT);
                }
            }
            GridPane assistants = getAssistants(player.getAssistants(), width, height, initialWidth, finalWidth, numPlayers < 4);
            root.getChildren().add(assistants);
        }

    }

    private static void drawClouds(List<CloudState> clouds, double width, double height, AnchorPane root) {
        // Draw the first two clouds on the left side of the screen and the remaining ones - if present - on the right
        root.getChildren().add(getCloudsGroup(clouds.subList(0, 2), width, height, false));
        root.getChildren().add(getCloudsGroup(clouds.subList(2, clouds.size()), width, height, true));
    }

    private static VBox getCloudsGroup(List<CloudState> clouds, double width, double height, boolean isOnRightSide) {
        VBox cloudsBox = new VBox();
        double layoutX;
        if (isOnRightSide) {
            layoutX = width - height * DrawingConstants.CLOUD_HEIGHT - width * DrawingConstants.OFFSET_OF_CLOUD_FROM_BORDER;
        } else {
            layoutX = width * DrawingConstants.OFFSET_OF_CLOUD_FROM_BORDER;
        }
        cloudsBox.setLayoutY(height * DrawingConstants.CLOUD_STARTING_HEIGHT);
        cloudsBox.setLayoutX(layoutX);
        cloudsBox.setSpacing(height * DrawingConstants.SPACE_BETWEEN_CLOUDS);

        for (int i = 0; i < Math.min(2, clouds.size()); i++) {
            CloudState cloud = clouds.get(i);
            AnchorPane cloudToDraw = getCloudWithStudents(cloud, width, height);
            cloudsBox.getChildren().add(cloudToDraw);
            cloudImages.add(cloudToDraw);
        }

        return cloudsBox;
    }

    private static AnchorPane getCloudWithStudents(CloudState cloud, double width, double height) {
        ImageView cloudBackground = new ImageView(new Image("/gameboard/clouds/cloud_card.png"));
        cloudBackground.setPreserveRatio(true);
        cloudBackground.setFitHeight(height * DrawingConstants.CLOUD_HEIGHT);

        GridPane studentsPane = new GridPane();
        Bounds imageBounds = cloudBackground.boundsInParentProperty().get();
        studentsPane.setLayoutX(imageBounds.getWidth() * DrawingConstants.OFFSET_OF_STUDENT_FROM_CLOUD);
        studentsPane.setLayoutY(imageBounds.getHeight() * DrawingConstants.OFFSET_OF_STUDENT_FROM_CLOUD);
        studentsPane.setHgap(imageBounds.getWidth() * DrawingConstants.OFFSET_BETWEEN_STUDENTS_IN_CLOUD);
        studentsPane.setVgap(imageBounds.getHeight() * DrawingConstants.OFFSET_BETWEEN_STUDENTS_IN_CLOUD);

        if (cloud.getStudents() != null) {
            for (int j = 0; j < cloud.getStudents().size(); j++) {
                String studentPath = "/gameboard/students/student_" +
                        cloud.getStudents().get(j).getColor().toString().toLowerCase() + ".png";
                ImageView student = new ImageView(new Image(studentPath));
                student.setPreserveRatio(true);
                student.setFitWidth(width * 0.4 / 25);

                studentsPane.add(student, j % 2, j / 2);
            }
        }

        return new AnchorPane(cloudBackground, studentsPane);
    }

    private static void drawCharacters(List<CharacterState> characters, double width, double height, AnchorPane root) {
        double coordX = width * DrawingConstants.CHARACTERS_BEGINNING_PROPORTION;
        int heightProportion = 13;

        for (CharacterState character : characters) {
            String imagePath = "/gameboard/characters/" + character.getCharacterName() + ".jpg";
            ImageView characterImage = new ImageView(new Image(imagePath));
            characterImage.setPreserveRatio(true);
            characterImage.setFitWidth(width * DrawingConstants.CHARACTER_CARD_PROPORTION);

            BorderPane characterToAdd = new BorderPane(characterImage);
            characterToAdd.setLayoutX(coordX);
            characterToAdd.setLayoutY(height / heightProportion);
            characterToAdd.setOnMouseClicked(event -> ObjectClickListeners.setCharacterClicked(character.getCharacterName(), characterToAdd));
            characterImages.add(characterToAdd);
            root.getChildren().add(characterToAdd);

            if (character.isHasCoin()) {
                double imageWidth = width * (DrawingConstants.COIN_PROPORTION);
                ImageView coin = getImageView(
                        "/gameboard/Moneta_base.png",
                        coordX + width * (DrawingConstants.CHARACTER_CARD_PROPORTION) - imageWidth,
                        height / 20,
                        imageWidth
                );
                root.getChildren().add(coin);
            }
            if (character.getStudents() != null) {
                GridPane grid = new GridPane();
                grid.setLayoutX(coordX + width * DrawingConstants.SPACE_BETWEEN_STUDENTS_ON_CHARACTERS);
                grid.setLayoutY(height / heightProportion + characterImage.boundsInParentProperty().get().getHeight());

                List<BorderPane> studentOnCharacter = new ArrayList<>();
                for (int i = 0; i < character.getStudents().size(); i++) {
                    String studentPath = "/gameboard/students/student_" +
                            character.getStudents().get(i).getColor().toString().toLowerCase() + ".png";
                    ImageView student = new ImageView(new Image(studentPath));
                    student.setPreserveRatio(true);
                    student.setFitWidth(width * 0.4 / 25);

                    BorderPane studentPane = new BorderPane(student);
                    int index = i;
                    studentPane.setOnMouseClicked(event ->
                            ObjectClickListeners.setStudentsOnCardClicked(
                                    character.getStudents().get(index).getColor(), studentPane))
                    ;
                    studentOnCharacter.add(studentPane);

                    grid.add(studentPane, i / 2, i % 2);
                }
                studentsOnCharacter.put(character.getCharacterName(), studentOnCharacter);

                root.getChildren().add(grid);
            }

            coordX += width * (DrawingConstants.CHARACTER_CARD_PROPORTION + DrawingConstants.SPACE_BETWEEN_CHARACTERS_PROPORTION);
        }
    }

    private static GridPane getAssistants(int[] assistants, double width, double height, double initialWidth, double finalWidth, boolean singleLine) {
        if (assistants.length == 0) return new GridPane();
        double assistantHeight = height - (width * (1454.0 / 3352.0) * 0.4);
        int assistantsPerLine = singleLine ? assistants.length : (assistants.length / 2);
        double spaceForAssistants = finalWidth - initialWidth - assistantsPerLine * width * DrawingConstants.OFFSET_BETWEEN_ASSISTANTS;
        GridPane gridPane = new GridPane();
        gridPane.setLayoutX(initialWidth);
        gridPane.setLayoutY(assistantHeight);
        gridPane.setHgap(width * DrawingConstants.OFFSET_BETWEEN_ASSISTANTS);
        gridPane.setVgap(height * DrawingConstants.OFFSET_OF_FIRST_ASSISTANT);

        for (int value : assistants) {
            String path = "/gameboard/assistants/Assistente (" + value + ").png";
            ImageView assistant = new ImageView(new Image(path));
            assistant.setPreserveRatio(true);
            double minWidth = singleLine ? DrawingConstants.ASSISTANT_MAX_WIDTH_SINGLE_LINE :
                    DrawingConstants.ASSISTANT_MAX_WIDTH_MULTILINE;
            assistant.setFitWidth(spaceForAssistants * Math.min((double) 2 / assistants.length, minWidth));

            BorderPane assistantPane = new BorderPane(assistant);
            assistantPane.setOnMouseClicked(event -> ObjectClickListeners.setAssistantClicked(value, assistantPane));
            assistantCards.add(assistantPane);

            if (singleLine) {
                gridPane.add(assistantPane, value - 1, 0);
            } else {
                int numRows = assistants.length / 2 + assistants.length % 2;
                gridPane.add(assistantPane, (value - 1) % numRows, (value - 1) / numRows);
            }
        }

        return gridPane;
    }

    private static void drawDashboard(PlayerState player, double x, double y, double width, AnchorPane root, String username) {
        ImageView dashboard = new ImageView(new Image("/gameboard/Plancia_DEF_circles.png"));
        dashboard.setPreserveRatio(true);
        dashboard.setFitWidth(width);
        dashboard.setX(x);
        dashboard.setY(y);
        root.getChildren().add(dashboard);

        // Add students to entrance
        GridPane newEntrance = getGridPane(
                x + width * DrawingConstants.INITIAL_X_OFFSET_ENTRANCE, y + width * DrawingConstants.INITIAL_Y_OFFSET_ENTRANCE,
                width * DrawingConstants.ENTRANCE_HGAP, width * DrawingConstants.ENTRANCE_VGAP
        );
        for (int i = 0; i < player.getEntrance().size(); i++) {
            Student s = player.getEntrance().get(i);
            String resourceName = "/gameboard/students/student_" + s.getColor().toString().toLowerCase() + ".png";
            ImageView student = new ImageView(new Image(resourceName));
            student.setPreserveRatio(true);
            student.setFitWidth(width / 25);
            student.setOnMouseClicked(mouseEvent -> ObjectClickListeners.setStudentClicked(s.getColor(), student));

            BorderPane studentPane = new BorderPane(student);
            if (username.equals(player.getName())) {
                studentPane.setOnMouseClicked(event -> ObjectClickListeners.setStudentClicked(s.getColor(), studentPane));
                entranceStudents.add(studentPane);
            }

            newEntrance.add(studentPane, (i + 1) % 2, i / 2 + i % 2);
        }
        root.getChildren().add(newEntrance);

        // Add students to dining room
        GridPane newDiningRoom = getGridPane(x + width * DrawingConstants.INITIAL_X_OFFSET_DINING_ROOM,
                y + width * DrawingConstants.INITIAL_Y_OFFSET_DINING_ROOM, width * DrawingConstants.DINING_ROOM_HGAP,
                width * DrawingConstants.DINING_ROOM_VGAP);

        List<Color> colorsInOrder = List.of(Color.GREEN, Color.RED, Color.YELLOW, Color.PINK, Color.BLUE);
        HashMap<Color, Integer> diningStudents = new HashMap<>();
        for (int i = 0; i < player.getDining().size(); i++) {
            Student s = player.getDining().get(i);
            String resourceName = "/gameboard/students/student_" + s.getColor().toString().toLowerCase() + ".png";
            ImageView student = new ImageView(new Image(resourceName));
            student.setPreserveRatio(true);
            student.setFitWidth(width / 25);

            int row = colorsInOrder.indexOf(s.getColor());
            Integer column = diningStudents.get(s.getColor());
            if (column == null) {
                column = 0;
            }

            BorderPane studentWithBorder = new BorderPane(student);
            studentWithBorder.setOnMouseClicked(event -> ObjectClickListeners.setStudentOnDiningClicked(s.getColor(), studentWithBorder));
            diningStudents.put(s.getColor(), column + 1);
            newDiningRoom.add(studentWithBorder, column, row);

            if (username.equals(player.getName())) {
                DrawingComponents.diningStudents.add(studentWithBorder);
            }
        }

        for (Color color : colorsInOrder) {
            diningStudents.putIfAbsent(color, 0);
            int positionOfEmptySpace = diningStudents.get(color);
            if (positionOfEmptySpace == 10) break;

            // If you want to change to a circle, the radius is width / 25 * 0,55
            BorderPane emptySpace = new BorderPane();
            emptySpace.setOnMouseClicked(mouseEvent -> ObjectClickListeners.setDiningRoomClicked());
            emptySpace.setPrefWidth(width / 25);
            emptySpace.setPrefHeight(width / 25);

            newDiningRoom.add(emptySpace, positionOfEmptySpace, colorsInOrder.indexOf(color));

            if (username.equals(player.getName())) {
                diningGaps.add(emptySpace);
            }
        }
        root.getChildren().add(newDiningRoom);

        // Add professors
        GridPane professorRoom = getGridPane(
                x + width * DrawingConstants.INITIAL_X_OFFSET_PROFESSOR_ROOM, y + width * DrawingConstants.INITIAL_Y_OFFSET_PROFESSOR_ROOM,
                0, width * DrawingConstants.PROFESSOR_ROOM_VGAP
        );
        for (int i = 0; i < colorsInOrder.size(); i++) {
            Color color = colorsInOrder.get(i);
            if (player.getOwnedProfessors().contains(color)) {
                String path = "/gameboard/professors/teacher_" + color.toString().toLowerCase() + ".png";
                ImageView professor = new ImageView(new Image(path));
                professor.setPreserveRatio(true);
                professor.setFitWidth(width / 20);
                professor.setRotate(90);

                professorRoom.add(professor, 0, i);
            } else {
                Pane emptyPlace = new Pane();
                emptyPlace.setPrefWidth(width / 20);
                emptyPlace.setPrefHeight(width / 20);
                professorRoom.add(emptyPlace, 0, i);
            }

        }
        root.getChildren().add(professorRoom);

        // Add towers
        GridPane towers = getGridPane(
                x + width * DrawingConstants.INITIAL_X_OFFSET_TOWERS, y + width * DrawingConstants.INITIAL_Y_OFFSET_TOWERS,
                width * DrawingConstants.TOWERS_HGAP, width * DrawingConstants.TOWERS_VGAP
        );
        for (int i = 0; i < player.getRemainingTowers(); i++) {
            String path = "/gameboard/towers/" + player.getTowerColor().toString().toLowerCase() + "_tower.png";
            ImageView tower = new ImageView(new Image(path));
            tower.setPreserveRatio(true);
            tower.setFitWidth(width * DrawingConstants.TOWERS_SIZE);

            towers.add(tower, i % 2, i / 2);
        }
        root.getChildren().add(towers);

    }

    private static void drawDashboardText(PlayerState player, double x, double y, AnchorPane root, double width, double height) {
        double startingX = x + width * DrawingConstants.PLAYER_NAME_INITIAL_PADDING;

        Text text = new Text(player.getName() + " | " + player.getNumCoins() + "x");
        text.setX(startingX);
        text.setY(y);
        text.setFont(Font.font(DrawingConstants.FONT_NAME, FontWeight.BOLD, DrawingConstants.TITLE_FONT_SIZE));
        root.getChildren().add(text);
        ImageView coin = getImageView("/gameboard/Moneta_base.png", startingX + text.getLayoutBounds().getWidth(),
                y - height / 24, width * DrawingConstants.COIN_PROPORTION);
        root.getChildren().add(coin);
    }

    private static ImageView getImageView(String path, double x, double y, double fitWidth) {
        ImageView iv = new ImageView(new Image(path));
        iv.setPreserveRatio(true);
        iv.setFitWidth(fitWidth);
        iv.setX(x);
        iv.setY(y);
        return iv;
    }

    private static GridPane getGridPane(double x, double y, double hgap, double vgap) {
        GridPane grid = new GridPane();
        grid.setLayoutX(x);
        grid.setLayoutY(y);
        grid.setHgap(hgap);
        grid.setVgap(vgap);
        return grid;
    }

    public static void setCurrentActions(List<String> currentActions) {
        DrawingComponents.currentActions = currentActions;
        // TODO highlight the corresponding parts
        for (String action : currentActions) {
            highlightAction(action);
        }
    }

    private static void highlightAction(String action) {
        switch (action) {
            case "MOVE_STUDENT_TO_DINING", "MOVE_STUDENT_TO_ISLAND" -> {
                entranceStudents.forEach(DrawingComponents::setGoldenBorder);
                diningGaps.forEach(DrawingComponents::setGoldenBorder);
                islands.forEach(DrawingComponents::setGoldenBorder);
            }
            case "PLAY_ASSISTANT" -> {
                assistantCards.forEach(DrawingComponents::setGoldenBorder);
            }
            case "PLAY_CHARACTER" -> {
                characterImages.forEach(DrawingComponents::setGoldenBorder);
            }
            case "MOVE_MN" -> {
                setGoldenBorder(motherNature);
            }
            case "FILL_FROM_CLOUD" -> {
                cloudImages.forEach(DrawingComponents::setGoldenBorder);
            }
        }
    }

    public static void removeGoldenBordersFromAllCharacters() {
        characterImages.forEach(character -> character.getStyleClass().clear());
    }

    private static void setGoldenBorder(Node element) {
        element.getStyleClass().add("highlight_element");
    }

    private static void setBlueBorders(Node element) {
        element.getStyleClass().add("element_active_for_swap_character");
    }

    private static void setGreenBorders(Node element) {
        element.getStyleClass().add("element_active_for_moving_character");
    }

    public static void addBlueBordersToEntranceStudents() {
        entranceStudents.forEach(DrawingComponents::setBlueBorders);
    }

    public static void addBlueBordersToCharacterStudents(CharacterName name) {
        studentsOnCharacter.get(name).forEach(DrawingComponents::setBlueBorders);
    }

    public static void addBlueBordersToDiningStudents() {
        diningStudents.forEach(DrawingComponents::setBlueBorders);
    }

    public static void moveStudentAwayFromCard(CharacterName name, boolean toIsland) {
        if (studentsOnCharacter.containsKey(name)) {
            List<BorderPane> students = studentsOnCharacter.get(name);
            students.forEach(DrawingComponents::setGreenBorders);
            if (toIsland) {
                islands.forEach(DrawingComponents::setGreenBorders);
            } else {
                diningGaps.forEach(DrawingComponents::setGreenBorders);
            }
        }
    }

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

}
